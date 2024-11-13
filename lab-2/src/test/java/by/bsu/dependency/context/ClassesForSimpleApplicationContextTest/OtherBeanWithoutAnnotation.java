package by.bsu.dependency.context.ClassesForSimpleApplicationContextTest;

import by.bsu.dependency.annotation.Inject;

public class OtherBeanWithoutAnnotation {

    @Inject
    private FirstBeanWithoutAnnotation firstBeanWithoutAnnotation;

    void doSomething() {
        System.out.println("Hi, I'm other bean");
    }

    void doSomethingWithFirst() {
        System.out.println("Trying to shake first bean...");
        firstBeanWithoutAnnotation.doSomething();
    }
}
