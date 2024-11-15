package by.bsu.dependency.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import by.bsu.dependency.context.AutoScanApplicationContextClasses.AutoScanBeanCircular1;
import by.bsu.dependency.context.AutoScanApplicationContextClasses.AutoScanBeanPrototype;
import by.bsu.dependency.context.AutoScanApplicationContextClasses.AutoScanBeanSingleton;
import by.bsu.dependency.exceptions.CircularDependencyFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Проверяет, что {@link AutoScanApplicationContext} работает так же, как
 * {@link SimpleApplicationContext} и не трогает классы
 * без аннотации {@link Bean}.
 */
class AutoScanApplicationContextTest {
    private AutoScanApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new AutoScanApplicationContext("by.bsu.dependency.context.AutoScanApplicationContextClasses");
        applicationContext.start();
    }

    @Test
    void testContainsOnlyBeans() {
        assertThat(applicationContext.containsBean("autoScanBeanPrototype")).isTrue();
        assertThat(applicationContext.isPrototype("autoScanBeanPrototype")).isTrue();
        assertThat(applicationContext.isSingleton("autoScanBeanPrototype")).isFalse();

        assertThat(applicationContext.containsBean("autoScanBeanSingleton")).isTrue();
        assertThat(applicationContext.isPrototype("autoScanBeanSingleton")).isFalse();
        assertThat(applicationContext.isSingleton("autoScanBeanSingleton")).isTrue();

        assertThat(applicationContext.containsBean("randomName")).isFalse();
        assertThat(applicationContext.containsBean("injectionBean5")).isFalse();
    }

    @Test
    void testAutoScanSimpleInjections() {
        assertThat(applicationContext.isRunning()).isTrue();
        assertThat(applicationContext.getBean(AutoScanBeanPrototype.class)).isNotNull().isInstanceOf(AutoScanBeanPrototype.class);
        assertThat(applicationContext.getBean("autoScanBeanPrototype")).isNotNull().isInstanceOf(AutoScanBeanPrototype.class);

        assertThat(applicationContext.getBean(AutoScanBeanSingleton.class)).isNotNull().isInstanceOf(AutoScanBeanSingleton.class);
        assertThat(applicationContext.getBean("autoScanBeanSingleton")).isNotNull().isInstanceOf(AutoScanBeanSingleton.class);

        assertThat(applicationContext.getBean(AutoScanBeanSingleton.class).equals(
            applicationContext.getBean("autoScanBeanSingleton")
        )).isTrue();

        assertThat(applicationContext.getBean(AutoScanBeanPrototype.class).equals(
            applicationContext.getBean("autoScanBeanPrototype")
        )).isFalse();
    }

    @Test
    void testCircularDepsInSingletonInjectionsThrows() {
        AutoScanApplicationContext ac = new AutoScanApplicationContext("by.bsu.dependency.context.SimpleApplicationContextTestClasses");
        assertThrows(CircularDependencyFoundException.class,
                     () -> ac.start());
    }

    @Test
    void testCircularDepsInPrototypeObtainingThrows() {
        assertThrows(CircularDependencyFoundException.class,
                     () -> applicationContext.getBean(AutoScanBeanCircular1.class));
    }
}
