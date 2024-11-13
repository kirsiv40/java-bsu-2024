package by.bsu.dependency.context;

import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.FirstBeanWithoutAnnotation;
import by.bsu.dependency.context.ClassesForSimpleApplicationContextTest.OtherBeanWithoutAnnotation;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleWorksWithoutAnnotation {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBeanWithoutAnnotation.class, OtherBeanWithoutAnnotation.class);
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBeanWithoutAnnotation")).isTrue();
        assertThat(applicationContext.containsBean("otherBeanWithoutAnnotation")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBeanWithoutAnnotation")).isNotNull().isInstanceOf(FirstBeanWithoutAnnotation.class);
        assertThat(applicationContext.getBean("otherBeanWithoutAnnotation")).isNotNull().isInstanceOf(OtherBeanWithoutAnnotation.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBeanWithoutAnnotation")).isTrue();
        assertThat(applicationContext.isSingleton("otherBeanWithoutAnnotation")).isTrue();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBeanWithoutAnnotation")).isFalse();
        assertThat(applicationContext.isPrototype("otherBeanWithoutAnnotation")).isFalse();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    
}
