package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.TicketSummary;
import uk.gov.dwp.uc.pairtest.validator.TicketRequestValidator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private static final int ADULT_PRICE = 25;
    private static final int CHILD_PRICE = 15;
    // INFANT = 0 (implicitly handled)

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService,
                             SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        //validate the raw inputs before processing the data
        TicketRequestValidator.validate(accountId, ticketTypeRequests, null);

        // Group ticket requests by type and sum their quantities.
        Map<TicketTypeRequest.Type,Integer> ticketCounts =

                Arrays.stream(ticketTypeRequests)
                        .collect(
                                Collectors.groupingBy(
                                        TicketTypeRequest::getTicketType,
                                        java.util.stream.Collectors.summingInt(TicketTypeRequest::getNoOfTickets)
                                ));

        TicketSummary summary = new TicketSummary(ticketCounts);
        TicketRequestValidator.validate(accountId, ticketTypeRequests, summary);

        int totalAmount = calculateTotalAmount(summary);
        int totalSeats = calculateTotalSeats(summary);

        // Execute payment and seat reservation
        ticketPaymentService.makePayment(accountId, totalAmount);
        seatReservationService.reserveSeat(accountId, totalSeats);

    }

    private int calculateTotalAmount(TicketSummary summary) {
        return (summary.getAdultCount() * ADULT_PRICE)
                + (summary.getChildCount() * CHILD_PRICE);
        // infants are free
    }

    private int calculateTotalSeats(TicketSummary summary) {
        return summary.getAdultCount() + summary.getChildCount();
        // infants do not get seats
    }

}
