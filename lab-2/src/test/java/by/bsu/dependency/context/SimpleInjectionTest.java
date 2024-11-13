package by.bsu.dependency.context;

import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.FirstBeanInj;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.FirstBeanInjSingleton;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.InjectionBean1;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.InjectionBean2;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.InjectionBean3;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.InjectionBean4;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.InjectionBean5;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.OtherBeanInj;
import by.bsu.dependency.exceptions.CircularDependencyFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


// Simple Test of injections
// Also checks that framework injects dependencies by field type, not by name

class SimpleInjectionTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBeanInj.class, OtherBeanInj.class, FirstBeanInjSingleton.class,
                                                          InjectionBean1.class, InjectionBean2.class, InjectionBean3.class, InjectionBean4.class);
        applicationContext.start();
    }

    @Test
    void testInjectionSuccessful() {
        assertThat(applicationContext.getBean("otherBeanInj")).isNotNull().isInstanceOf(OtherBeanInj.class);
        assertThat(applicationContext.getBean(OtherBeanInj.class).getFirstBean()).isNotNull().isInstanceOf(FirstBeanInj.class);
        assertThat(((OtherBeanInj)applicationContext.getBean("otherBeanInj")).getFirstBean()).isNotNull().isInstanceOf(FirstBeanInj.class);
    }

    @Test
    void testInjectionOfPrototypeCorrect() {
        applicationContext.getBean(FirstBeanInj.class).zeroCounter();
        @SuppressWarnings("unused")
        OtherBeanInj first = applicationContext.getBean(OtherBeanInj.class);
        @SuppressWarnings("unused")
        OtherBeanInj second = applicationContext.getBean(OtherBeanInj.class);
        OtherBeanInj third = applicationContext.getBean(OtherBeanInj.class);
        assertThat(third.getFirstBean().getCounter() == 3).isTrue();
        assertThat(applicationContext.getBean(FirstBeanInj.class).getCounter() == 4).isTrue();
    }

    @Test
    void testInjectionOfSingletonCorrect() {
        applicationContext.getBean(FirstBeanInjSingleton.class).zeroCounter();
        @SuppressWarnings("unused")
        OtherBeanInj first = applicationContext.getBean(OtherBeanInj.class);
        @SuppressWarnings("unused")
        OtherBeanInj second = applicationContext.getBean(OtherBeanInj.class);
        OtherBeanInj third = applicationContext.getBean(OtherBeanInj.class);

        // Check that no new instances of FirstBeanInjSingleton were created
        assertThat(third.getFirstSingletonBean().getCounter() == 0).isTrue();
        assertThat(applicationContext.getBean(FirstBeanInjSingleton.class).getCounter() == 0).isTrue();
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
