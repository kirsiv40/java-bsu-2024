package by.bsu.dependency.context.ClassesForSimpleApplicationContextTest;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.context.PostConstructTest;

@Bean(name = "postConstr2")
public class PostConstructBean2 {
    private static Integer id = 2;
    
    @Inject
    private PostConstructBean1 unusedBean;

    public PostConstructBean2() {
        PostConstructTest.ids.add("CTR" + String.valueOf(id));
    }

    @PostConstruct
    void init() {
        PostConstructTest.ids.add("PC" + String.valueOf(id));
    }
}
