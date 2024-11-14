package by.bsu.dependency.context;

public class Main {
    public static void main(String[] args) {
        AutoScanApplicationContext ac = new AutoScanApplicationContext("by.bsu.dependency.context.SimpleApplicationContextTestClasses");
        ac.start();
    }
}
