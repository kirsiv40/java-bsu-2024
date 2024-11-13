package by.bsu.dependency.context.ClassesForSimpleApplicationContextTest;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "otherBeanInj", scope = BeanScope.PROTOTYPE)
public class OtherBeanInj {

    @Inject
    private FirstBeanInj blablabla;

    @Inject
    private FirstBeanInjSingleton firstBeanInjSingleton;

    void doSomething() {
        System.out.println("Hi, I'm other bean");
    }

    void doSomethingWithFirst() {
        System.out.println("Trying to shake first bean...");
        blablabla.doSomething();
    }

    public FirstBeanInj getFirstBean() {
        return blablabla;
    }

    public FirstBeanInjSingleton getFirstSingletonBean() {
        return firstBeanInjSingleton;
    }
}
