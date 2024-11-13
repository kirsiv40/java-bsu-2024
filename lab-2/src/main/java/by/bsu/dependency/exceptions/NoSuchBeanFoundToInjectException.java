package by.bsu.dependency.exceptions;

public class NoSuchBeanFoundToInjectException extends RuntimeException {
    public NoSuchBeanFoundToInjectException(String name) {
        super("Bean with name \"" + name + "\" was not found in this context. Can't inject.");
    }
}
