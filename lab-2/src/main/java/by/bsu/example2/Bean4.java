package by.bsu.example2;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.BeanScope;

@Bean(scope = BeanScope.BLOCK_SINGLETON)
public class Bean4 {

    @Inject(canUseLastSameClass = true)
    private Bean1 someBean;

    int ctr = 1;
    void print() {
        System.out.println("BLOCK_SINGLETON Bean4 says: " + ctr);
        ctr++;
    }
}

