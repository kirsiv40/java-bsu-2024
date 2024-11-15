package by.bsu.dependency.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.PostConstructBean1;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.PostConstructBean2;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.PostConstructPrototypeBean1;
import by.bsu.dependency.context.SimpleApplicationContextTestClasses.PostConstructPrototypeBean2;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

/**
 * Проверяет, что функция с {@link PostConstruct} вызывается 
 * и то, что вызовы конструкторов и {@link PostConstruct}
 * выполняются в правильной последовательности
 */
public class PostConstructTest {
    private ApplicationContext applicationContext;

    public static ArrayList<String> ids = new ArrayList<>();

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(PostConstructBean1.class, PostConstructBean2.class,
                                                          PostConstructPrototypeBean1.class, PostConstructPrototypeBean2.class);
        ids = new ArrayList<>();
    }

    @Test
    void testPostConstructWasntCalledAfterContextCreation() {
        assertThat(PostConstructBean1.post_constr_metka == false);
    }

    @Test
    void testPostConstructWorksCorrectForSingleton() {
        applicationContext.start();
        assertThat(PostConstructBean1.post_constr_metka == true);
    }

    @Test
    void testSequenceOfPostConstructCallsIsCorrect() {
        applicationContext.start();
        assertThat(ids.equals(List.of("CTR1", "CTR2", "PC1", "PC2")));
        assertThat(PostConstructPrototypeBean1.post_constr_static_metka == false);
        ids.clear();
        
        @SuppressWarnings("unused")
        PostConstructPrototypeBean1 unusedProtoBean = applicationContext.getBean(PostConstructPrototypeBean1.class);
        assertThat(PostConstructPrototypeBean1.post_constr_static_metka == true);
        assertThat(ids.equals(List.of("CTR3", "PC3")));
        ids.clear();
        
        PostConstructPrototypeBean1.post_constr_static_metka = false;
        unusedProtoBean = (PostConstructPrototypeBean1) applicationContext.getBean("postConstr3");
        assertThat(ids.equals(List.of("CTR3", "PC3")));
        ids.clear();
        
        @SuppressWarnings("unused")
        PostConstructPrototypeBean2 unusedProtoBean2 = applicationContext.getBean(PostConstructPrototypeBean2.class);
        assertThat(ids.equals(List.of("CTR3", "PC3", "CTR4", "PC4")));
        ids.clear();
    }
}
