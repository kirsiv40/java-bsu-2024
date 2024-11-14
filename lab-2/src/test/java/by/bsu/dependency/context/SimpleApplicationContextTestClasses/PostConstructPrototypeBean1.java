package by.bsu.dependency.context.SimpleApplicationContextTestClasses;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.context.PostConstructTest;

@Bean(name = "postConstr3", scope = BeanScope.PROTOTYPE)
public class PostConstructPrototypeBean1 {
    public boolean post_constr_metka = false;
    public static boolean post_constr_static_metka = false;
    private static Integer id = 3;

    @Inject
    private PostConstructBean2 unusedBean;

    public PostConstructPrototypeBean1() {
        PostConstructTest.ids.add("CTR" + String.valueOf(id));
    }

    @PostConstruct
    void init() {
        post_constr_metka = true;
        PostConstructTest.ids.add("PC" + String.valueOf(id));
    }
}
