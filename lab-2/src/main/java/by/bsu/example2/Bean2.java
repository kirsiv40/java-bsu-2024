package by.bsu.example2;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class Bean2 {
    @Inject
    private Bean3 someElBean;
    @Inject
    private Bean4 someSIngletonBean;

    void print() {
        someElBean.print();
        someSIngletonBean.print();
    }
}
