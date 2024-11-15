package by.bsu.example2;

import by.bsu.dependency.context.AutoScanApplicationContext;

public class Main {
    /**
     * сравнение поведения двух новых типов бинов
     */
    public static void main(String[] args) {
        AutoScanApplicationContext ac = new AutoScanApplicationContext("by.bsu.example2");
        ac.start();
        var s = ac.getBean(Bean1.class);
        s.print();
    }
}
