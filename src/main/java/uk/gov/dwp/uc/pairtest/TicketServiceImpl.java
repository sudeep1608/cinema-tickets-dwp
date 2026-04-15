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

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService,
                             SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        // Group ticket requests by type and sum their quantities.
        Map<TicketTypeRequest.Type,Integer> ticketCounts =

                Arrays.stream(ticketTypeRequests)
                        .collect(
                                Collectors.groupingBy(
                                        TicketTypeRequest::getTicketType,
                                        java.util.stream.Collectors.summingInt(TicketTypeRequest::getNoOfTickets)
                                ));

        //validation of the request needs to be added before processing
    }

}
