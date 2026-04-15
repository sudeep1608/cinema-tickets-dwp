package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("TicketServiceImpl Tests")
public class TicketServiceImplTest {

    private TicketPaymentService paymentService;
    private SeatReservationService seatReservationService;
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, seatReservationService);
    }

    //Should throw exception when accountId is invalid (null, zero or negative).
    @Test
    void shouldThrowExceptionForInvalidAccountId() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        //Test zero account ID
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(0L, adult)
        );
        //Test negative account ID
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(-1L, adult)
        );
        //Test null account ID
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(null, adult)
        );
    }

    //Should throw exception when no ticket requests are provided.
    @Test
    void shouldThrowExceptionWhenNoTicketsProvided() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L)
        );
    }
    //Should throw exception when ticket requests is null.
    @Test
    void shouldThrowExceptionWhenTicketRequestsIsNull() {

        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, (TicketTypeRequest[]) null)
        );
    }

    //Should throw exception when any ticket request has zero or negative ticket count.
    @Test
    void shouldThrowExceptionForInvalidTicketCount() {
        TicketTypeRequest invalid = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, invalid));
    }

    //Should purchase adult tickets successfully and verify payment and seat reservation.
    @Test
    void shouldPurchaseAdultTicketsSuccessfully() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        ticketService.purchaseTickets(1L, adult);

        verify(paymentService).makePayment(1L, 50);
        verify(seatReservationService).reserveSeat(1L, 2);
    }

    //Should calculate total cost and seats for a mix of adult and child tickets.
    @Test
    void shouldCalculateCostAndSeatsForAdultAndChild() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        ticketService.purchaseTickets(1L, adult, child);
        verify(paymentService).makePayment(1L, (2 * 25) + (3 * 15));
        verify(seatReservationService).reserveSeat(1L, 5);
    }

    //Should not charge or reserve seats for infant tickets and verify that only adult and child tickets are processed.
    @Test
    void shouldNotChargeOrReserveSeatsForInfants() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        ticketService.purchaseTickets(1L, adult, infant);
        verify(paymentService).makePayment(1L, 50);
        verify(seatReservationService).reserveSeat(1L, 2);
    }
    //Should throw exception when infants exceed adults.
    @Test
    void shouldThrowExceptionWhenInfantsExceedAdults() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adult, infant));
    }

    //Should throw exception when child tickets are purchased without any adult tickets.
    @Test
    void shouldThrowExceptionWhenChildWithoutAdult() {
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, child));
    }

    //Should throw exception when total tickets exceed the maximum allowed (25).
    @Test
    void shouldThrowExceptionWhenExceedingMaxTickets() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adult));
    }

    //Should aggregate duplicate ticket types correctly and verify that the total amount and seats are calculated based on the aggregated counts.
    @Test
    void shouldAggregateDuplicateTicketTypes() {
        TicketTypeRequest adult1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest adult2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        ticketService.purchaseTickets(1L, adult1, adult2);
        verify(paymentService).makePayment(1L, 5 * 25);
        verify(seatReservationService).reserveSeat(1L, 5);
    }






}
