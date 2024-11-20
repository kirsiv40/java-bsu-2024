package by.bsu.dependency.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.CircularDependencyFoundException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import by.bsu.dependency.exceptions.NoSuchBeanFoundToInjectException;

public class SimpleApplicationContext extends AbstractApplicationContext {
    protected Map<Class<?>, DefnRecord<?>> beanDefinitionsByClass = new HashMap<>();
    protected Map<String, Object> singletonBeans = new HashMap<>();
    protected Map<Class<?>, Object> singletonBeansByClass = new HashMap<>();

    public SimpleApplicationContext(Collection<Class<?>> beanClassesCollection, boolean takeClassesWithoutAnnotation) {
        super(new HashMap<String, DefnRecord<?>>(beanClassesCollection.stream()
                .filter(el -> {
                    if (takeClassesWithoutAnnotation) {
                        return true;
                    }
                    return el.isAnnotationPresent(Bean.class);
                })
                .collect(Collectors.toMap(el -> {
                    if (el.isAnnotationPresent(Bean.class) && !el.getAnnotation(Bean.class).name().isEmpty()) {
                        return el.getAnnotation(Bean.class).name();
                    } else {
                        return GenerateBeanName(el);
                    }
                },
                el -> {
                    Optional<Method> method = Arrays.asList(el.getDeclaredMethods()).stream()
                            .filter(el_method -> el_method.isAnnotationPresent(PostConstruct.class))
                            .findAny();
                    if (el.isAnnotationPresent(Bean.class)) {
                        if (el.getAnnotation(Bean.class).name().isEmpty()) {
                            return new DefnRecord<>(el, el.getAnnotation(Bean.class).scope(),
                                    Character.toLowerCase(el.getName().charAt(0)) + el.getName().substring(1),
                                    method);
                        } else {
                            return new DefnRecord<>(el, el.getAnnotation(Bean.class).scope(),
                                    el.getAnnotation(Bean.class).name(), method);
                        }
                    } else {
                        return new DefnRecord<>(el, BeanScope.SINGLETON,
                                Character.toLowerCase(el.getName().charAt(0)) + el.getName().substring(1),
                                method);
                    }
                }))));
        for (var el : beanDefinitions.values()) {
            beanDefinitionsByClass.put(el.clazz(), el);
        }
    }

    /**
     * Создает контекст, содержащий классы, переданные в параметре.
     * <br/>
     * Если на классе нет аннотации {@code @Bean}, имя бина получается из названия
     * класса, скоуп бина по дефолту
     * считается {@code Singleton}.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор
     * без аргументов.
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public SimpleApplicationContext(Class<?>... beanClasses) {
        this(new ArrayList<Class<?>>(Arrays.asList(beanClasses)), true);
    }

    public SimpleApplicationContext(boolean takeWA, Class<?>... beanClasses) {
        this(new ArrayList<Class<?>>(Arrays.asList(beanClasses)), takeWA);
    }

    protected void InjectStaticBean(Field field, String name, Map<Class<?>, Object> rec_singletons) {
        if (!beanDefinitionsByClass.containsKey(field.getType())) {
            throw new NoSuchBeanFoundToInjectException(field.getName());
        }
        field.setAccessible(true);
        try {
            field.set(singletonBeans.get(name), getBean(field.getType(), new HashMap<>(), rec_singletons));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
    }

    protected void InjectBean(Field field, Object instance, Class<?> clazz, Map<Class<?>, Object> rec_singletons,
            Map<Class<?>, Object> new_rec_deps) {
        field.setAccessible(true);
        try {
            if (field.getAnnotation(Inject.class).canUseLastSameClass() && new_rec_deps.containsKey(field.getType())) {
                field.set(instance, new_rec_deps.get(field.getType()));
            } else if (beanDefinitionsByClass.get(field.getType()).scope().equals(BeanScope.BLOCK_SINGLETON)) {
                if (!rec_singletons.containsKey(clazz)) {
                    rec_singletons.put(clazz, getBean(field.getType(), new_rec_deps, rec_singletons));
                }
                field.set(instance, rec_singletons.get(clazz));
            } else {
                field.set(instance, getBean(field.getType(), new_rec_deps, rec_singletons));
            }
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected <T> void CallPostConstruct(DefnRecord<T> record, Object instance) {
        if (record.init().isPresent()) {
            try {
                Method method = record.init().get();
                method.setAccessible(true);
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые
     * объекты
     */
    @Override
    public void start() {
        // обход и вызов конструктора всех минглтон бинов
        for (String name : beanDefinitions.keySet()) {
            if (beanDefinitions.get(name).scope().equals(BeanScope.SINGLETON)) {
                try {
                    singletonBeans.put(name, beanDefinitions.get(name).clazz()
                            .getConstructor()
                            .newInstance());
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        // создание мапы для поиска по классу
        for (var el : singletonBeans.values()) {
            singletonBeansByClass.put(el.getClass(), el);
        }

        for (String name : singletonBeans.keySet()) {
            // заполнение полей
            Map<Class<?>, Object> rec_singletons = new HashMap<>();
            for (Field field : singletonBeans.get(name).getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    InjectStaticBean(field, name, rec_singletons);
                }
            }
            // пост конструкт
            if (beanDefinitions.get(name).init().isPresent()) {
                CallPostConstruct(beanDefinitions.get(name), singletonBeans.get(name));
            }
        }
        this.is_started = ContextStatus.STARTED;
    }

    @Override
    public Object getBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        if (!this.containsBean(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return getBean(this.beanDefinitions.get(name).clazz());
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        return getBean(clazz, new HashMap<>(), new HashMap<>());
    }

    /**
     * 
     * @param rec_deps для поиска и исправления зависимостей с BLOCK_ELEMENT
     * @param rec_singletons для поиска и исправления зависимостей с BLOCK_SINGLETON
     */
    @SuppressWarnings("unchecked")
    protected <T> T getBean(Class<T> clazz, Map<Class<?>, Object> rec_deps, Map<Class<?>, Object> rec_singletons) {
        if (!beanDefinitionsByClass.containsKey(clazz)) {
            throw new NoSuchBeanDefinitionException();
        }
        DefnRecord<T> record = (DefnRecord<T>) beanDefinitionsByClass.get(clazz);
        if (record.scope().equals(BeanScope.SINGLETON)) {
            return (T) singletonBeansByClass.get(clazz);
        }

        if (rec_deps.containsKey(clazz) && record.scope().equals(BeanScope.BLOCK_ELEMENT)) {
            return (T) rec_deps.get(clazz);
        }

        if (rec_deps.containsKey(clazz)) {
            throw new CircularDependencyFoundException(clazz.getName());
        }

        Map<Class<?>, Object> new_rec_deps = new HashMap<>(rec_deps);

        Object newInstance = null;
        try {
            newInstance = record.clazz().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }

        new_rec_deps.put(clazz, newInstance);

        // Заполнение полей
        for (Field field : newInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                InjectBean(field, newInstance, clazz, rec_singletons, new_rec_deps);
            }
        }

        // пост конструкт
        CallPostConstruct(record, newInstance);
        return (T) newInstance;
    }
}
