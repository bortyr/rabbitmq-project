package de.hsbremen.mkss.restservice.exceptions;

public class OorderItemNotFoundException extends RuntimeException {

    public OorderItemNotFoundException(Long oorderId, Long itemId) {
        super("Could not find either order "+ oorderId +", or item " + itemId);
    }
}
