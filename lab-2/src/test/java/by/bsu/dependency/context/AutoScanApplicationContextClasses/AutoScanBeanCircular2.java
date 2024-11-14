package by.bsu.dependency.context.AutoScanApplicationContextClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class AutoScanBeanCircular2 {
    @Inject
    private AutoScanBeanCircular1 otherBean;

    public AutoScanBeanCircular1 getOtherBean() {
        return otherBean;
    }
}
