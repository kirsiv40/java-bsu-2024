package by.bsu.dependency.context;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    ContextStatus is_started = ContextStatus.NOT_STARTED;

}
