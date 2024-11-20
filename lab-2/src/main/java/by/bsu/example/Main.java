package by.bsu.example;

import by.bsu.dependency.context.AutoScanApplicationContext;

/**
 * Разрешение явной циклической зависимости.
 * Можно заменить {@code canUseLastSameClass = true} на аннотацию 
 * {@code @Bean(scope = BeanScope.BLOCK_ELEMENT)}, как в {@code example2}.
 */
public class Main {
    public static void main(String[] args) {
        AutoScanApplicationContext ac = new AutoScanApplicationContext("by.bsu.example");
        ac.start();
        System.out.println("Starting from A:");
        FirstBean fb = ac.getBean(FirstBean.class);
        fb.ping();
        System.out.println("\nStarting from B:");
        SecondBean sb = ac.getBean(SecondBean.class);
        sb.ping();
        System.out.println("\nStarting from C:");
        ThirdBean tb = ac.getBean(ThirdBean.class);
        tb.ping();
    }
}
