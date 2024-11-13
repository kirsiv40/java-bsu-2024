package by.bsu.dependency.context;

public interface ApplicationContext {

    /**
     * Стартует контекст. При вызове этого метода происходит создание всех необходимых объектов, объявленных
     * {@code SINGLETON} бинами.
     * <br/>
     * При невозможности запустить контекст (например, отсутствие требуемых зависимостей) должен бросать исключение.
     */
    void start();

    /**
     * Проверяет, запущен ли контекст.
     *
     * @return {@code true}, если контекст запущен, иначе {@code false}.
     */
    boolean isRunning();

    /**
     * Проверяет, содержится ли в контексте бин с переданным идентификатором (именем).
     *
     * @param name имя бина
     * @throws ApplicationContextNotStartedException если контекст еще не запущен
     * @return {@code true}, если бин присутствует в контексте, иначе {@code false}.
     */
    boolean containsBean(String name);

    /**
     * Возвращает инстанс бина по имени (идентификатору). Для {@code SINGLETON} бинов каждый вызов должен возвращать
     * один и тот же объект, а для {@code PROTOTYPE} - каждый раз новый объект.
     *
     * @param name имя бина
     * @throws NoSuchBeanDefinitionException если бин с таким именем не был объявлен.
     * @throws ApplicationContextNotStartedException если контекст еще не запущен
     * @return соответствующий инстанс бина
     */
    Object getBean(String name);

    /**
     * Возвращает инстанс бина по его классу. Для {@code SINGLETON} бинов каждый вызов должен возвращать
     * один и тот же объект, а для {@code PROTOTYPE} - каждый раз новый объект.
     *
     * @param clazz класс бина
     * @throws NoSuchBeanDefinitionException если бин такого типа не был объявлен.
     * @throws ApplicationContextNotStartedException если контекст еще не запущен
     * @return соответствующий инстанс бина
     * @param <T> тип бина
     */
    <T> T getBean(Class<T> clazz);

    /**
     * Проверяет, имеет ли бин скоуп {@code SINGLETON}
     *
     * @param name имя бина
     * @throws NoSuchBeanDefinitionException если бин такого типа не был объявлен.
     * @return {@code true}, если бин имеет скоуп {@code SINGLETON}, иначе {@code false}.
     */
    boolean isSingleton(String name);

    /**
     * Проверяет, имеет ли бин скоуп {@code PROTOTYPE}
     *
     * @param name имя бина
     * @throws NoSuchBeanDefinitionException если бин такого типа не был объявлен.
     * @return {@code true}, если бин имеет скоуп {@code PROTOTYPE}, иначе {@code false}.
     */
    boolean isPrototype(String name);
}
