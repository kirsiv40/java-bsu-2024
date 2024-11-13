package by.bsu.dependency.context.ClassesForSimpleApplicationContextTest;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "injPrototypeIntoSingleton5", scope = BeanScope.SINGLETON)
public class InjectionBean5 {
    @Inject
    private InjectionBean3 otherBean;

    public InjectionBean3 getOtherBean() {
        return otherBean;
    }
}
