package by.bsu.dependency.context.SimpleApplicationContextTestClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;

@Bean(name = "firstBeanInjSingleton", scope = BeanScope.SINGLETON)
public class FirstBeanExSingleton {
    static private int counter = 0;

    public FirstBeanExSingleton() {
        ++counter;
    }

    public int getCounter() {
        return counter;
    }

    public void zeroCounter() {
        counter = 0;
    }

    void printSomething() {
        System.out.println("Hello, I'm first bean");
    }

    void doSomething() {
        System.out.println("First bean is working on a project...");
    }
}
