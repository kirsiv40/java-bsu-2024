package by.bsu.dependency.exceptions;

public class TwoBeansWithTheSameNameWereGivenToThisContextException extends RuntimeException {
    public TwoBeansWithTheSameNameWereGivenToThisContextException(String name) {
        super("Two beans with the same name \"" + name + "\" were given to the context");
    }
}
