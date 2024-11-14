package by.bsu.dependency.context.AutoScanApplicationContextClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class AutoScanBeanPrototype {
    private int counter;
    public static int st_c = 0;
    @Inject
    private AutoScanBeanSingleton someBean;

    public AutoScanBeanPrototype() {
        counter = st_c;
        st_c++;
    }

    public AutoScanBeanSingleton getSomeBean() {
        return someBean;
    }

    public int getCounter() {
        return counter;
    }

    public static int getSt_c() {
        return st_c;
    }
}
