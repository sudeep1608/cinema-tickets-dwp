package uk.gov.dwp.uc.pairtest.model;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Map;

public class TicketSummary {
    private final Map<TicketTypeRequest.Type, Integer> ticketCounts;

    public TicketSummary(Map<TicketTypeRequest.Type, Integer> ticketCounts) {
        this.ticketCounts = ticketCounts;
    }

    public int getCount(TicketTypeRequest.Type type) {
        return ticketCounts.getOrDefault(type, 0);
    }

    public int getTotalTickets() {
        return ticketCounts.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
    public int getAdultCount() {
        return getCount(TicketTypeRequest.Type.ADULT);
    }
    public int getChildCount() {
        return getCount(TicketTypeRequest.Type.CHILD);
    }
    public int getInfantCount() {
        return getCount(TicketTypeRequest.Type.INFANT);
    }



}
