package by.bsu.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class SecondBean {
    public static String name = "B";
    public int counter = 0;

    @Inject(canUseLastSameClass = true)
    private ThirdBean someBean;

    public void ping() {
        if (counter < 3) {
            counter++;
            System.out.println("Entered Bean \"" + name + "\" for the " + counter + " time.");
            someBean.ping();
        } else {
            System.out.println("Got back to the first bean \"" + name + "\". Stopping.");
        }
    }
}
