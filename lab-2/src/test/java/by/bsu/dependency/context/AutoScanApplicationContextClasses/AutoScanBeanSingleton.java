package by.bsu.dependency.context.AutoScanApplicationContextClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

@Bean
public class AutoScanBeanSingleton {
    @Inject
    private AutoScanBeanPrototype someBean;

    public AutoScanBeanPrototype getSomeBean() {
        return someBean;
    }
}
