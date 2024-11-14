package by.bsu.dependency.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;


public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {

    private final Map<String, Object> beans = new HashMap<>();

    /**
     * ! Класс существует только для базового примера !
     * <br/>
     * Создает контекст, содержащий классы, переданные в параметре. Полагается на отсутсвие зависимостей в бинах,
     * а также на наличие аннотации {@code @Bean} на переданных классах.
     * <br/>
     * ! Контекст данного типа не занимается внедрением зависимостей !
     * <br/>
     * ! Создает только бины со скоупом {@code SINGLETON} !
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) {
        super(Arrays.stream(beanClasses).collect(
                Collectors.toMap(
                        beanClass -> beanClass.getAnnotation(Bean.class).name(),
                        beanClass -> new DefnRecord<>(beanClass, BeanScope.SINGLETON, beanClass.getAnnotation(Bean.class).name(), null)
                )
        ));
    }

    @Override
    public void start() {
        beanDefinitions.forEach((beanName, beanClass) -> beans.put(beanName, instantiateBean(beanClass)));
        this.is_started = ContextStatus.STARTED;
    }


    /**
     * В этой реализации отсутствуют проверки статуса контекста (запущен ли он) и исключения в случае отсутствия бина
     */
    @Override
    public Object getBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        if (!this.containsBean(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return beans.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> clazz) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        Optional<String> try_find = beanDefinitions.keySet().stream()
            .filter(el -> beanDefinitions.get(el).clazz().equals(clazz))
            .findAny();
        if (!try_find.isPresent()) {
            throw new NoSuchBeanDefinitionException();
        }
        return (T) getBean(try_find.get());
    }

    private <T> T instantiateBean(DefnRecord<T> beanRecord) {
        try {
            return beanRecord.clazz().getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
