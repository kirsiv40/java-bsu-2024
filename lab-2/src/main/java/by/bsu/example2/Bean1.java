package by.bsu.example2;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class Bean1 {
    @Inject
    private Bean2 someBean1;
    @Inject
    private Bean2 someBean2;
    @Inject
    private Bean2 someBean3;

    void print() {
        someBean1.print();
        someBean2.print();
        someBean3.print();
    }
}
