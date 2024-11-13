package by.bsu.dependency.context;

public class SimpleApplicationContext extends AbstractApplicationContext {

    /**
     * Создает контекст, содержащий классы, переданные в параметре.
     * <br/>
     * Если на классе нет аннотации {@code @Bean}, имя бина получается из названия класса, скоуп бина по дефолту
     * считается {@code Singleton}.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public SimpleApplicationContext(Class<?>... beanClasses) {
        throw new IllegalStateException("not implemented");
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
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
