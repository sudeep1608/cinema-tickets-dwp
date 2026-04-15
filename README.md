# Cinema Tickets - Java Implementation

A clean, well-tested Java 21 implementation of the DWP Cinema Tickets coding exercise.

---

## Overview

This project implements a `TicketService` that handles cinema ticket purchasing with full validation, payment processing, and seat reservation. The solution is designed with separation of concerns, immutability, and testability as core principles.

---

## Project Structure

```
src/
├── main/java/uk/gov/dwp/uc/pairtest/
│   ├── TicketService.java                  # Interface (unmodified)
│   ├── TicketServiceImpl.java              # Core implementation
│   ├── domain/
│   │   └── TicketTypeRequest.java          # Immutable ticket request object
│   ├── model/
│   │   └── TicketSummary.java              # Aggregated ticket counts
│   ├── validator/
│   │   └── TicketRequestValidator.java     # All validation logic
│   └── exception/
│       └── InvalidPurchaseException.java   # Custom exception
│
└── test/java/uk/gov/dwp/uc/pairtest/
    └── TicketServiceImplTest.java          # Comprehensive unit tests
```

---

### 3. Purchase Flow

The `purchaseTickets` method follows a clear, sequential flow:

1. **Validate account ID** — must be non-null and greater than zero
2. **Validate ticket requests** — must not be null or empty
3. **Count tickets by type** — using Java Streams and `Collectors.groupingBy`
4. **Apply business rules** — check ticket limits and adult presence
5. **Calculate total cost** — adults at £25, children at £15, infants free
6. **Calculate seats** — adults + children only (infants excluded)
7. **Call payment service** — `makePayment(accountId, totalAmount)`
8. **Call reservation service** — `reserveSeat(accountId, totalSeats)`

### 4. Validation Rules

The following requests are considered invalid and will throw `InvalidPurchaseException`:

- Account ID is null or less than or equal to zero
- No ticket requests provided
- More than 25 tickets requested in a single purchase
- Child or Infant tickets requested without at least one Adult ticket
- Number of Infants exceeds number of Adults (each infant needs a lap)

### 5. Modern Java Features Used

- **Records** — for a clean, truly immutable `TicketTypeRequest`
- **Streams & Collectors** — for grouping and summing ticket counts
- **`Map.getOrDefault`** — for safe ticket count lookups
- **Compact constructors** — for validation inside the record

---

## Assumptions

- All accounts with an ID greater than zero are valid and have sufficient funds
- The `TicketPaymentService` is an external provider and always processes payments successfully
- The `SeatReservationService` is an external provider and always reserves seats successfully
- Each Infant must be accompanied by their own Adult (i.e. infants cannot exceed adults)

---

## Constraints Respected

- `TicketService` interface was not modified
- Code in `thirdparty.*` packages was not modified
- `TicketTypeRequest` remains immutable (implemented as a Java record)
- `TicketServiceImpl` only exposes the `purchaseTickets` method publicly; all helper logic is private

---

## Testing Strategy

The test suite is written using **JUnit 5** and **Mockito**, covering:

- Invalid account IDs (null, zero, negative values) via `@ParameterizedTest`
- Null and empty ticket requests
- Child/Infant tickets without an Adult
- Exceeding the 25-ticket limit
- Infants exceeding the number of Adults
- Happy path — correct payment amount and seat count for mixed ticket types
- Adult-only purchases
- Purchases with all three ticket types combined

All external services (`TicketPaymentService`, `SeatReservationService`) are mocked using Mockito, ensuring tests are fast, isolated, and deterministic.
 
---

## Building the Project

```bash
# Build the project
mvn clean install
 
# Run tests only
mvn test
 
# Package the project
mvn clean package
```

##  Tech Stack

- **Java 21**
- **Maven**
- **JUnit 5** (Testing)
- **Mockito** (Mocking)
