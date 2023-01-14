package de.hsbremen.mkss.restservice.exceptions;

public class OorderNotFoundException extends RuntimeException {

    public OorderNotFoundException(Long id) {
        super("Could not find order " + id);
    }
}
