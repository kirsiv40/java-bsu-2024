package by.bsu.dependency.context;

import org.reflections.*;

import by.bsu.dependency.annotation.Bean;

public class AutoScanApplicationContext extends SimpleApplicationContext {
    /**
     * Создает контекст, содержащий классы из пакета {@code packageName}, помеченные аннотацией {@code @Bean}.
     * <br/>
     * Если имя бина в анноации не указано ({@code name} пустой), оно берется из названия класса.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param packageName имя сканируемого пакета
     */
    public AutoScanApplicationContext(String packageName) {
        super((new Reflections(packageName)).getTypesAnnotatedWith(Bean.class));
    }
}
