package by.bsu.dependency.context.ClassesForSimpleApplicationContextTest;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.context.PostConstructTest;

@Bean(name = "postConstr1")
public class PostConstructBean1 {
    public static boolean post_constr_metka = false;
    private static Integer id = 1;

    public PostConstructBean1() {
        PostConstructTest.ids.add("CTR" + String.valueOf(id));
    }

    @PostConstruct
    void init() {
        post_constr_metka = true;
        PostConstructTest.ids.add("PC" + String.valueOf(id));
    }
}
