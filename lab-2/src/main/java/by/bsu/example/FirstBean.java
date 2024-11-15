package by.bsu.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;

@Bean(scope = BeanScope.PROTOTYPE)
public class FirstBean {
    public static String name = "A";
    public int counter = 0;

    @Inject(canUseLastSameClass = true)
    private SecondBean someBean;

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
