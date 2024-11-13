package by.bsu.dependency.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    public record DefnRecord(Class<?> clazz, BeanScope scope, String name, Optional<Method> init) {
    }

    private final Map<String, DefnRecord> beanDefinitions;
    private Map<Class<?>, DefnRecord> beanDefinitionsBeansByClass = new HashMap<>();
    private Map<String, Object> singletonBeans = new HashMap<>();
    private Map<Class<?>, Object> singletonBeansByClass = new HashMap<>();
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
        this.beanDefinitions = Arrays.stream(beanClasses)
            .collect(Collectors.toMap(el -> {
                if (el.isAnnotationPresent(Bean.class)) {
                    return el.getAnnotation(Bean.class).name();
                } else {
                    String[] allnames = el.getName().split("\\.");
                    String realname = allnames[allnames.length - 1];
                    String temp = Character.toLowerCase(realname.charAt(0)) + realname.substring(1);
                    return temp;
                }
            }, 
            el -> {
                Optional<Method> method = Arrays.asList(el.getDeclaredMethods()).stream()
                    .filter(el_method -> el_method.isAnnotationPresent(PostConstruct.class))
                    .findAny();
                if (el.isAnnotationPresent(Bean.class)) {
                    return new DefnRecord(el, el.getAnnotation(Bean.class).scope(), el.getAnnotation(Bean.class).name(), method);
                } else {
                    return new DefnRecord(el, BeanScope.SINGLETON, Character.toLowerCase(el.getName().charAt(0)) + el.getName().substring(1), method);
                }
            }
        ));
        for (var el : beanDefinitions.values()) {
            beanDefinitionsBeansByClass.put(el.clazz, el);
        }
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
    @Override
    public void start() {
        for (String name : beanDefinitions.keySet()) {
            if (beanDefinitions.get(name).scope.equals(BeanScope.SINGLETON)) {
                try {
                    singletonBeans.put(name, beanDefinitions.get(name).clazz
                    .getConstructor()
                    .newInstance());
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        for (var el : singletonBeans.values()) {
            singletonBeansByClass.put(el.getClass(), el);
        }

        for (String name : singletonBeans.keySet()) {
            for (Field field : singletonBeans.get(name).getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    // if (beanDefinitionsBeansByClass.containsKey(field.getType()) && beanDefinitionsBeansByClass.get(field.getType()).scope.equals(BeanScope.PROTOTYPE)) {
                    //     throw new CantInjectPrototypeClassIntoSingletonException(field.getName(), singletonBeans.get(name).getClass().getName());
                    // }
                    if (!beanDefinitionsBeansByClass.containsKey(field.getType())) {
                        throw new NoSuchBeanFoundToInjectException(field.getName());
                    }
                    field.setAccessible(true);
                    try {
                        field.set(singletonBeans.get(name), getBean(field.getType(), new HashSet<>()));
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            if (beanDefinitions.get(name).init.isPresent()) {
                try {
                    Method method = beanDefinitions.get(name).init.get();
                    method.setAccessible(true);
                    method.invoke(singletonBeans.get(name));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        this.is_started = ContextStatus.STARTED;
    }

    @Override
    public boolean isRunning() {
        return is_started.equals(ContextStatus.STARTED);
    }

    @Override
    public boolean containsBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        if (beanDefinitions.containsKey(name)) {
            return true;
        }
        return false;
    }

    @Override
    public Object getBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        if (!this.containsBean(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return getBean(this.beanDefinitions.get(name).clazz);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getBean(Class<T> clazz, Set<Class<?>> rec_deps) {
        if (!beanDefinitionsBeansByClass.containsKey(clazz)) {
            throw new NoSuchBeanDefinitionException();
        }
        DefnRecord record = beanDefinitionsBeansByClass.get(clazz);
        if (record.scope.equals(BeanScope.SINGLETON)) {
            return (T) singletonBeansByClass.get(clazz);
        }

        if (rec_deps.contains(clazz)) {
            throw new CircularDependencyFoundException(clazz.getName());
        }

        Set<Class<?>> new_rec_deps = new HashSet<>(rec_deps);
        new_rec_deps.add(clazz);

        Object newInstance = null;
        try {
            newInstance = record.clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
        for (Field field : newInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    field.set(newInstance, getBean(field.getType(), new_rec_deps));
                } catch(NoSuchBeanDefinitionException e) {
                    throw e;
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (record.init.isPresent()) {
            try {
                Method method = record.init.get();
                method.setAccessible(true);
                method.invoke(newInstance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return (T) newInstance;
    }


    @Override
    public <T> T getBean(Class<T> clazz) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        return getBean(clazz, new HashSet<>());
    }

    @Override
    public boolean isPrototype(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return (beanDefinitions.get(name).scope.equals(BeanScope.PROTOTYPE));
    }

    @Override
    public boolean isSingleton(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return (beanDefinitions.get(name).scope.equals(BeanScope.SINGLETON));
    }
}
