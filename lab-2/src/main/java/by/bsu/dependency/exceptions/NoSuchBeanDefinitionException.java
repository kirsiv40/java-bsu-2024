package by.bsu.dependency.exceptions;

public class NoSuchBeanDefinitionException extends RuntimeException {
    public NoSuchBeanDefinitionException() {
        super("Such bean wasn't given to this context while initialization");
    }
}
