package by.bsu.dependency.context;

public class AutoScanApplicationContext extends AbstractApplicationContext {

    /**
     * Создает контекст, содержащий классы из пакета {@code packageName}, помеченные аннотацией {@code @Bean}.
     * <br/>
     * Если имя бина в анноации не указано ({@code name} пустой), оно берется из названия класса.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param packageName имя сканируемого пакета
     */
    public AutoScanApplicationContext(String packageName) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void start() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isRunning() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean containsBean(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Object getBean(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isPrototype(String name) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public boolean isSingleton(String name) {
        throw new IllegalStateException("not implemented");
    }
}
