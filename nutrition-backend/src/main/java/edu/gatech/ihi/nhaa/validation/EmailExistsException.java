package edu.gatech.ihi.nhaa.validation;

public class EmailExistsException extends Throwable {

    public EmailExistsException(final String message) {
        super(message);
    }
}
