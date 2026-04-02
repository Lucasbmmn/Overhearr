package fr.lucasbmmn.overhearrserver.metadata.exception.deezer;

import fr.lucasbmmn.overhearrserver.metadata.exception.MetadataProviderException;

public class DeezerSearchException extends MetadataProviderException {
    public DeezerSearchException(String message) {
        super(message);
    }

    public DeezerSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
