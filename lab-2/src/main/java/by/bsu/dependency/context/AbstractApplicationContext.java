package by.bsu.dependency.context;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

public abstract class AbstractApplicationContext implements ApplicationContext {
    protected record DefnRecord<T>(Class<T> clazz, BeanScope scope, String name, Optional<Method> init) {
    }    
    protected final Map<String, DefnRecord<?>> beanDefinitions;

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }
    
    AbstractApplicationContext(Map<String, DefnRecord<?>> beanMap) {
        beanDefinitions = beanMap;
    } 

    protected ContextStatus is_started = ContextStatus.NOT_STARTED;

    @Override
    public boolean isRunning() {
        return is_started.equals(ContextStatus.STARTED);
    }

    @Override
    public boolean containsBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException();
        }
        return beanDefinitions.containsKey(name);
    }

    @Override
    public boolean isPrototype(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return (beanDefinitions.get(name).scope().equals(BeanScope.PROTOTYPE));
    }

    @Override
    public boolean isSingleton(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException();
        }
        return (beanDefinitions.get(name).scope().equals(BeanScope.SINGLETON));
    }
}
