package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

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

        //initial validation of accountId and ticket requests
        validateAccountId(accountId);
        validateTicketRequests(ticketTypeRequests);

        // Group ticket requests by type and sum their quantities.
        Map<TicketTypeRequest.Type,Integer> ticketCounts =

                Arrays.stream(ticketTypeRequests)
                        .collect(
                                Collectors.groupingBy(
                                        TicketTypeRequest::getTicketType,
                                        java.util.stream.Collectors.summingInt(TicketTypeRequest::getNoOfTickets)
                                ));

        //counts total number of tickets requested
        int adultCount = ticketCounts.getOrDefault(TicketTypeRequest.Type.ADULT, 0);
        int childCount = ticketCounts.getOrDefault(TicketTypeRequest.Type.CHILD,0);

        //total amount is calculated based on the number of adult and child tickets requested
        int totalAmount = (adultCount * ADULT_PRICE) + (childCount * CHILD_PRICE);

        //total seats
        int totalSeats = adultCount + childCount;

        //further validation needs to be added

    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account ID.");
        }
    }

    private void validateTicketRequests(TicketTypeRequest... ticketTypeRequests) {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("No ticket requests provided.");
        }
    }


}
