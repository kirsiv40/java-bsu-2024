package by.bsu.dependency.exceptions;

public class ApplicationContextNotStartedException extends RuntimeException {
    public ApplicationContextNotStartedException() {
        super("Application is not started yet. You can't use it.");
    }
}
