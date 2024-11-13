package by.bsu.dependency.example;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.SimpleApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new SimpleApplicationContext(
                FirstBean.class, OtherBean.class
        );
        applicationContext.start();

        FirstBean firstBean = (FirstBean) applicationContext.getBean(FirstBean.class);
        OtherBean otherBean = (OtherBean) applicationContext.getBean(OtherBean.class);

        firstBean.printSomething();
        otherBean.doSomething();

        // Метод падает, так как в классе HardCodedSingletonApplicationContext не реализовано внедрение зависимостей
        otherBean.doSomethingWithFirst();
    }
}
