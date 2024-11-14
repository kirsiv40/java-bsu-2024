package by.bsu.dependency.context;

import by.bsu.dependency.context.SimpleApplicationContextTestClasses.FirstBeanEx;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.FirstBeanExSingleton;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.InjectionBean1;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.InjectionBean2;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.InjectionBean3;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.InjectionBean4;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.InjectionBean5;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.OtherBeanEx;
import by.bsu.dependency.exceptions.CircularDependencyFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple Test of Injections
 * Also checks that the framework injects dependencies by field type, not by name,
 * and verifies that an exception is thrown when a circular dependency is found
 */
class SimpleInjectionTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBeanEx.class, OtherBeanEx.class, FirstBeanExSingleton.class,
                                                          InjectionBean1.class, InjectionBean2.class, InjectionBean3.class, InjectionBean4.class);
        applicationContext.start();
    }

    @Test
    void testInjectionSuccessful() {
        assertThat(applicationContext.getBean("otherBeanInj")).isNotNull().isInstanceOf(OtherBeanEx.class);
        assertThat(applicationContext.getBean(OtherBeanEx.class).getFirstBean()).isNotNull().isInstanceOf(FirstBeanEx.class);
        assertThat(((OtherBeanEx)applicationContext.getBean("otherBeanInj")).getFirstBean()).isNotNull().isInstanceOf(FirstBeanEx.class);
    }

    @Test
    void testInjectionOfPrototypeCorrect() {
        applicationContext.getBean(FirstBeanEx.class).zeroCounter();
        @SuppressWarnings("unused")
        OtherBeanEx first = applicationContext.getBean(OtherBeanEx.class);
        @SuppressWarnings("unused")
        OtherBeanEx second = applicationContext.getBean(OtherBeanEx.class);
        OtherBeanEx third = applicationContext.getBean(OtherBeanEx.class);
        assertThat(third.getFirstBean().getCounter() == 3).isTrue();
        assertThat(applicationContext.getBean(FirstBeanEx.class).getCounter() == 4).isTrue();
    }

    @Test
    void testInjectionOfSingletonCorrect() {
        applicationContext.getBean(FirstBeanExSingleton.class).zeroCounter();
        @SuppressWarnings("unused")
        OtherBeanEx first = applicationContext.getBean(OtherBeanEx.class);
        @SuppressWarnings("unused")
        OtherBeanEx second = applicationContext.getBean(OtherBeanEx.class);
        OtherBeanEx third = applicationContext.getBean(OtherBeanEx.class);

        // Check that no new instances of FirstBeanInjSingleton were created
        assertThat(third.getFirstSingletonBean().getCounter() == 0).isTrue();
        assertThat(applicationContext.getBean(FirstBeanExSingleton.class).getCounter() == 0).isTrue();
    }

    @Test
    void testContextContainsBeans() {
        assertThat(applicationContext.containsBean("firstBeanInj")).isTrue();
        assertThat(applicationContext.containsBean("otherBeanInj")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testInjectionSingletonBeanIntoItself() {
        assertThat(applicationContext.getBean(InjectionBean1.class).equals(
            applicationContext.getBean(InjectionBean1.class).getThisBean()
        )).isTrue();
    }

    @Test
    void testInjectionOfPrototypeBeanInSingletonBean() {
        assertThat(applicationContext.getBean(InjectionBean1.class).equals(
            applicationContext.getBean(InjectionBean2.class).getOtherBean()
        )).isTrue();
        assertThat(applicationContext.getBean(InjectionBean1.class).getOtherBean()).isNotNull().isInstanceOf(InjectionBean2.class);
    }

    @Test
    void testCircularDependencyInPrototypeInjectionsThrows() {
        assertThrows(CircularDependencyFoundException.class,
                     () -> applicationContext.getBean(InjectionBean3.class));
    }

    @Test
    void testInjectionOfPrototypeBeanWithCircularDependencyIntoSingletonBeanThrows() {
        SimpleApplicationContext applicationContext2 = new SimpleApplicationContext(InjectionBean3.class, InjectionBean4.class, InjectionBean5.class);
        assertThrows(CircularDependencyFoundException.class,
                     () -> applicationContext2.start());
    }
}
