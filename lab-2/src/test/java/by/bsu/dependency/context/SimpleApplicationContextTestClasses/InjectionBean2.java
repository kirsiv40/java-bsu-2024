package by.bsu.dependency.context.SimpleApplicationContextTestClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "injPrototypeIntoSingleton2", scope = BeanScope.PROTOTYPE)
public class InjectionBean2 {
    @Inject
    private InjectionBean1 otherBean;

    public InjectionBean1 getOtherBean() {
        return otherBean;
    }
}
