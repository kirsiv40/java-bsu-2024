package by.bsu.example2;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.BLOCK_ELEMENT)
public class Bean3 {

    @Inject(canUseLastSameClass = true)
    private Bean1 someBean;

    int ctr = 1;
    void print() {
        System.out.println("BLOCK_ELEMENT Bean3 says: " + ctr);
        ctr++;
    }
}
