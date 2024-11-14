package by.bsu.dependency.context.SimpleApplicationContextTestClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class InjectionBean3 {
    @Inject
    private InjectionBean4 otherBean;

    public InjectionBean4 getOtherBean() {
        return otherBean;
    }
}
