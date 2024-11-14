package by.bsu.dependency.context.SimpleApplicationContextTestClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "otherBeanInj", scope = BeanScope.PROTOTYPE)
public class OtherBeanEx {

    @Inject
    private FirstBeanEx blablabla;

    @Inject
    private FirstBeanExSingleton firstBeanInjSingleton;

    void doSomething() {
        System.out.println("Hi, I'm other bean");
    }

    void doSomethingWithFirst() {
        System.out.println("Trying to shake first bean...");
        blablabla.doSomething();
    }

    public FirstBeanEx getFirstBean() {
        return blablabla;
    }

    public FirstBeanExSingleton getFirstSingletonBean() {
        return firstBeanInjSingleton;
    }
}
