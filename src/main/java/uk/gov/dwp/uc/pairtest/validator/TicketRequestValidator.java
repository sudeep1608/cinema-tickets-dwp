package uk.gov.dwp.uc.pairtest.validator;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.TicketSummary;

public class TicketRequestValidator {

    private static final int MAX_TICKETS = 25;

    //Validation method to validate all the tickets based on the rules defined in the requirements
    public static void validate(Long accountId,
                                TicketTypeRequest[] requests,
                                TicketSummary summary) {

        validateAccountId(accountId);
        validateRequestsExist(requests);
        validateTicketQuantities(requests);

        if (summary != null) {
            validateMaximumTicketLimit(summary);
            validateAdultRequirement(summary);
            validateInfantToAdultRatio(summary);
        }
    }

    //Validation1
    private static void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account ID.");
        }
    }

    //Validation2
    private static void validateRequestsExist(TicketTypeRequest[] requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException("No ticket requests provided.");
        }
    }

    //Validation3
    private static void validateTicketQuantities(TicketTypeRequest[] requests) {
        for (TicketTypeRequest request : requests) {
            if (request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException("Invalid ticket count.");
            }
        }
    }

    //Validation4
    private static void validateMaximumTicketLimit(TicketSummary summary) {
        if (summary.getTotalTickets() > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than 25 tickets.");
        }
    }

    //Validation5
    private static void validateAdultRequirement(TicketSummary summary) {
        if (summary.getAdultCount() == 0 &&
                (summary.getChildCount() > 0 || summary.getInfantCount() > 0)) {
            throw new InvalidPurchaseException("Child/Infant tickets require an adult.");
        }
    }

    //Validation6
    private static void validateInfantToAdultRatio(TicketSummary summary) {
        if (summary.getInfantCount() > summary.getAdultCount()) {
            throw new InvalidPurchaseException("Each infant must be accompanied by an adult.");
        }
    }
}
