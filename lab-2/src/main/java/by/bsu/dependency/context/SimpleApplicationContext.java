package by.bsu.dependency.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    protected Map<Class<?>, DefnRecord<?>> beanDefinitionsByClass = new HashMap<>();
    protected Map<String, Object> singletonBeans = new HashMap<>();
    protected Map<Class<?>, Object> singletonBeansByClass = new HashMap<>();
    
    // protected record recClassStorage<T>(Class<>) {
    // }

    public SimpleApplicationContext(Collection<Class<?>> beanClassesCollection) {
        super(new HashMap<String, DefnRecord<?>>(beanClassesCollection.stream()
            .collect(Collectors.toMap(el -> {
                if (el.isAnnotationPresent(Bean.class) && !el.getAnnotation(Bean.class).name().isEmpty()) {
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
                    if (el.getAnnotation(Bean.class).name().isEmpty()) {
                        return new DefnRecord<>(el, el.getAnnotation(Bean.class).scope(), Character.toLowerCase(el.getName().charAt(0)) + el.getName().substring(1), method);
                    } else {
                        return new DefnRecord<>(el, el.getAnnotation(Bean.class).scope(), el.getAnnotation(Bean.class).name(), method);
                    }
                } else {
                    return new DefnRecord<>(el, BeanScope.SINGLETON, Character.toLowerCase(el.getName().charAt(0)) + el.getName().substring(1), method);
                }
            }
        ))));
        for (var el : beanDefinitions.values()) {
            beanDefinitionsByClass.put(el.clazz(), el);
        }
    }

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
        this(new ArrayList<Class<?>>(Arrays.asList(beanClasses)));
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
    @Override
    public void start() {
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

        for (var el : singletonBeans.values()) {
            singletonBeansByClass.put(el.getClass(), el);
        }

        for (String name : singletonBeans.keySet()) {
            // fields init
            for (Field field : singletonBeans.get(name).getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    if (!beanDefinitionsByClass.containsKey(field.getType())) {
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
            // post construct call
            if (beanDefinitions.get(name).init().isPresent()) {
                try {
                    Method method = beanDefinitions.get(name).init().get();
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
        return getBean(this.beanDefinitions.get(name).clazz());
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        return getBean(clazz, new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    protected <T> T getBean(Class<T> clazz, Set<Class<?>> rec_deps) {
        if (!beanDefinitionsByClass.containsKey(clazz)) {
            throw new NoSuchBeanDefinitionException();
        }
        DefnRecord<T> record = (DefnRecord<T>) beanDefinitionsByClass.get(clazz);
        if (record.scope().equals(BeanScope.SINGLETON)) {
            return (T) singletonBeansByClass.get(clazz);
        }

        if (rec_deps.contains(clazz)) {
            throw new CircularDependencyFoundException(clazz.getName());
        }

        Set<Class<?>> new_rec_deps = new HashSet<>(rec_deps);
        new_rec_deps.add(clazz);

        Object newInstance = null;
        try {
            newInstance = record.clazz().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }

        //fields init
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

        //PostConstruct
        if (record.init().isPresent()) {
            try {
                Method method = record.init().get();
                method.setAccessible(true);
                method.invoke(newInstance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return (T) newInstance;
    }
}
