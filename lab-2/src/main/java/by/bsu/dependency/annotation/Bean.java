package by.bsu.dependency.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

    /**
     * @return уникальный идентификатор бина (aka имя)
     */
    String name() default "";

    /**
     * Бин со скоупом {@code SINGLETON} существует в контексте в единственном экземпляре, создается при старте контекста
     * <br/>
     * Бин со скоупом {@code PROTOTYPE} не создается при старте, а генерируется при каждом вызове {@code getBean}
     *
     * @return скоуп бина
     */
    BeanScope scope() default BeanScope.SINGLETON;
}
