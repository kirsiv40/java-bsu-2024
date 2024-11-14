package by.bsu.dependency.context.AutoScanApplicationContextClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class AutoScanBeanCircular1 {
    @Inject
    private AutoScanBeanCircular2 otherBean;

    public AutoScanBeanCircular2 getOtherBean() {
        return otherBean;
    }
}
