package by.bsu.dependency.context.SimpleApplicationContextTestClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "injPrototypeIntoSingleton1", scope = BeanScope.SINGLETON)
public class InjectionBean1 {
    @Inject
    private InjectionBean2 otherBean;

    @Inject
    private InjectionBean1 thisBean;

    public InjectionBean2 getOtherBean() {
        return otherBean;
    }

    public InjectionBean1 getThisBean() {
        return thisBean;
    }
}
