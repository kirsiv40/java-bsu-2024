package by.bsu.dependency.exceptions;

public class CircularDependencyFoundException extends RuntimeException{
    public CircularDependencyFoundException(String name) {
        super("Circular dependency found in prototype class " + name + "fields.");
    }
}
