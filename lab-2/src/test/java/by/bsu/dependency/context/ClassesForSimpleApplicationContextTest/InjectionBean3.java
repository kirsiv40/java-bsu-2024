package by.bsu.dependency.context.ClassesForSimpleApplicationContextTest;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "injPrototypeIntoSingleton3", scope = BeanScope.PROTOTYPE)
public class InjectionBean3 {
    @Inject
    private InjectionBean4 otherBean;

    public InjectionBean4 getOtherBean() {
        return otherBean;
    }
}
