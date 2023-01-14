package de.hsbremen.mkss.restservice.exceptions;

public class OorderNotInPreparationException extends RuntimeException {

    public OorderNotInPreparationException(Long oorderId) {
        super("Order " + oorderId + "- status is not 'In Preparation'");
    }
}
