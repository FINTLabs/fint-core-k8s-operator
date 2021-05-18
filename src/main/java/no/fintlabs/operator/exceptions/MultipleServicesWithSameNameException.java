package no.fintlabs.operator.exceptions;

public class MultipleServicesWithSameNameException extends Exception{
    public MultipleServicesWithSameNameException(String message) {
        super(message);
    }
}
