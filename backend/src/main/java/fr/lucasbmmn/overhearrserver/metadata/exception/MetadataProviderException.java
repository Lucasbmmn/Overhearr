package fr.lucasbmmn.overhearrserver.metadata.exception;

import fr.lucasbmmn.overhearrserver.exception.BadGatewayException;

public abstract class MetadataProviderException extends BadGatewayException {
    protected MetadataProviderException(String message) {
        super(message);
    }

    protected MetadataProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
