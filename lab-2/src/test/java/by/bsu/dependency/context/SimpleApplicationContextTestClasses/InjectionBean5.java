package by.bsu.dependency.context.SimpleApplicationContextTestClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.SINGLETON)
public class InjectionBean5 {
    @Inject
    private InjectionBean3 otherBean;

    public InjectionBean3 getOtherBean() {
        return otherBean;
    }
}
