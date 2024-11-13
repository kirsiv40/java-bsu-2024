# Quizer library
В данной задаче вам предлагается разработать приложение для проверки знаний учеников.

Приложение должно функционировать следующим образом. Учитель врывается в класс и решает провести плановую проверку знаний, большими буквами на доске пишет название теста. Обрадованные прекрасной новостью, ученики, в свою очередь, охотно открывают разработанное вами приложение и вводят в _CLI_ название теста. Затем они предположительно самостоятельно отвечают на все вопросы, и в конце им выводится оценка.

# Архитектура приложения

>Всю работу ведите в пакете `by.<ваш ник>.quizer`
## Базовые элементы

### Result
```java
/**
 * Enum, который описывает результат ответа на задание
 */
enum Result {
    OK, // Получен правильный ответ
    WRONG, // Получен неправильный ответ
    INCORRECT_INPUT // Некорректный ввод. Например, текст, когда ожидалось число
}
```

### Task

```java
/**
 * Interface, который описывает одно задание
 */
interface Task {
    /**
     @return текст задания
     */
    String getText();
    
    /**
     * Проверяет ответ на задание и возвращает результат
     *
     * @param  answer ответ на задание
     * @return        результат ответа
     * @see           Result
     */
    Result validate(String answer);
}
```

### TaskGenerator

```java
/**
 * Interface, который описывает один генератор заданий
 */
interface TaskGenerator {
    /**
     * Возвращает задание. При этом новый объект может не создаваться, если класс задания иммутабельный
     *
     * @return задание
     * @see    Task
     */
    Task generate();
}
```

### Quiz
```java
/**
 * Class, который описывает один тест
 */
class Quiz {
    /**
     * @param generator генератор заданий
     * @param taskCount количество заданий в тесте
     */
    Quiz(TaskGenerator generator, int taskCount) { 
        // ...
    }
    
    /**
     * @return задание, повторный вызов вернет слелующее
     * @see Task
     */
    Task nextTask() {
        // ...
    }
    
    /**
     * Предоставить ответ ученика. Если результат {@link Result#INCORRECT_INPUT}, то счетчик неправильных 
     * ответов не увеличивается, а {@link #nextTask()} в следующий раз вернет тот же самый объект {@link Task}.
     */
    Result provideAnswer(String answer) {
        // ...
    }
    
    /**
     * @return завершен ли тест
     */
    boolean isFinished() {
        // ...
    }
    
    /**
     * @return количество правильных ответов
     */
    int getCorrectAnswerNumber() {
        // ...
    }
    
    /**
     * @return количество неправильных ответов
     */
    int getWrongAnswerNumber() {
        // ...
    }
    
    /**
     * @return количество раз, когда был предоставлен неправильный ввод
     */
    int getIncorrectInputNumber() {
        // ...
    }
    
    /**
     * @return оценка, которая является отношением количества правильных ответов к количеству всех вопросов. 
     *         Оценка выставляется только в конце!
     */
    double getMark() {
        // ...
    }
}
```

## Функция main()

### getQuizMap
Этот метод будет использоваться из `main()`, чтобы получить список доступных тестов. Создание всех тестов (`Quiz`) будет захардкожено в этом методе. После реализации разных TaskGenerator’ов (см. ниже), добавьте в этот метод несколько различных тестов.

```java
/**
 * @return тесты в {@link Map}, где
 * ключ     - название теста {@link String}
 * значение - сам тест       {@link Quiz}
 */
static Map<String, Quiz> getQuizMap() {
    // ...
}
```

### public static void main()

```java
public static void main() {
    // ...
}
```

>`public static void main()` - входная точка вашего приложения.
	
#### Описание 

Для начала, получите список всех тестов с помощью статической функции `getQuizMap()` и выведите пользователю сообщение _“Введите название теста...”_. Затем получите объект `Quiz` по этому названию, если он есть, иначе попросите повторить попытку.

Пока тест не завершен, опрашивайте ученика (пользуйтесь методами реализованного вами класса `Quiz`). В конце выведите ему его отметку.

## Реализация Task и TaskGenerator
>Реализации `Task` следует расположить в пакете `tasks`, а реализации `TaskGenerator` в пакете `generators`.
	
`Task` и `TaskGenerator` - интерфейсы, теперь нужно сделать их реализации.
Все классы `*Task` реализуют интерфейс `Task`, а `*TaskGenerator` - интерфейс `TaskGenerator`.


### ExpressionTaskGenerator 
Генерирует примеры вида `<num1><operator><num2>=<answer>`. Например, `2*5=?`.

```java
class ExpressionTaskGenerator implements TaskGenerator {
    /**
     * @param minNumber              минимальное число
     * @param maxNumber              максимальное число
     * @param generateSum            разрешить генерацию с оператором +
     * @param generateDifference     разрешить генерацию с оператором -
     * @param generateMultiplication разрешить генерацию с оператором *
     * @param generateDivision       разрешить генерацию с оператором /
     */
    ExpressionTaskGenerator(
        int minNumber,
        int maxNumber,
        boolean generateSum,
        boolean generateDifference,
        boolean generateMultiplication,
        boolean generateDivision
    ) {
        // ...
    }
    
    /**
     * return задание типа {@link ExpressionTask}
     */
    ExpressionTask generate() {
        // ...
    }
}
```

### EquationTaskGenerator 
Генерирует уравнения вида `<num1><operator>x=<answer>` и `x<operator><num2>=<answer>`. Например, `x/2=6`.

```java
class EquationTaskGenerator implements TaskGenerator {
    /**
     * @param minNumber              минимальное число
     * @param maxNumber              максимальное число
     * @param generateSum            разрешить генерацию с оператором +
     * @param generateDifference     разрешить генерацию с оператором -
     * @param generateMultiplication разрешить генерацию с оператором *
     * @param generateDivision       разрешить генерацию с оператором /
     */
    EquationTaskGenerator(
        int minNumber,
        int maxNumber,
        boolean generateSum,
        boolean generateDifference,
        boolean generateMultiplication,
        boolean generateDivision
    ) {
        // ...
    }
    
    /**
     * return задание типа {@link EquationTask}
     */
    EquationTask generate() {
        // ...
    }
}
```

### GroupTaskGenerator 
`TaskGenerator`, который позволяет объединить несколько других `TaskGenerator`.

```java
class GroupTaskGenerator implements TaskGenerator {
    /**
     * Конструктор с переменным числом аргументов
     *
     * @param generators генераторы, которые в конструктор передаются через запятую
     */
    GroupTaskGenerator(TaskGenerator... generators) {
        // ...
    }

    /**
     * Конструктор, который принимает коллекцию генераторов
     *
     * @param generators генераторы, которые передаются в конструктор в Collection (например, {@link ArrayList})
     */
    GroupTaskGenerator(Collection<TaskGenerator> generators) {
        // ...
    }

    /**
     * @return результат метода generate() случайного генератора из списка.
     *         Если этот генератор выбросил исключение в методе generate(), выбирается другой.
     *         Если все генераторы выбрасывают исключение, то и тут выбрасывается исключение.
     */
    Task generate() {
        // ...
    }
}
```

### PoolTaskGenerator 
`TaskGenerator`, который отдает задания из заранее заготовленного набора.

```java
class PoolTaskGenerator implements TaskGenerator {
    /**
     * Конструктор с переменным числом аргументов
     *
     * @param allowDuplicate разрешить повторения
     * @param tasks          задания, которые в конструктор передаются через запятую
     */
    PoolTaskGenerator(
        boolean allowDuplicate,
        Task... tasks
    ) {
        // ...
    }

    /**
     * Конструктор, который принимает коллекцию заданий
     *
     * @param allowDuplicate разрешить повторения
     * @param tasks          задания, которые передаются в конструктор в Collection (например, {@link LinkedList})
     */
    PoolTaskGenerator(
        boolean allowDuplicate,
        Collection<Task> tasks
    ) {
        // ...
    }

    /**
     * @return случайная задача из списка
     */
    Task generate() {
        // ...
    }
}
```

### TextTask

```java
/**
 * Задание с заранее заготовленным текстом. 
 * Можно использовать {@link PoolTaskGenerator}, чтобы задавать задания такого типа.
 */
class TextTask implements Task {
    /**
     * @param text   текст задания
     * @param answer ответ на задание
     */
    TextTask(
        String text,
        String answer
    ) {
        // ...
    }
    
    // ...
}
```

## Добавляем абстракций
Для всего в этом пункте следует сделать пакет `math` в пакетах `tasks` и `genertors`.

Сейчас `ExpressionTask` и `EquationTask` наследуются напрямую от `Task`. Введем несколько абстракций, чтобы вынести общую логику из этих двух классов.

>Описывывается иерархия для `Task`, такую же нужно повторить для `TaskGenerator`

Сделайте интерфейс `MathTask`, который расширяет интерфейс `Task`. Сделайте абстрактный класс `AbstractMathTask`, который реализует интерфейс `MathTask`. Далее сделайте, чтобы классы `ExpressionMathTask` и `EquationMathTask` наследовались от `AbstractMathTask`.

Перенесите обшую логику из `ExpressionMathTask` и `EquationMathTask` в `AbstractMathTask`.

Добавьте в интерфейс `MathTaskGenerator` методы:
```java
int getMinNumber(); // получить минимальное число
int getMaxNumber(); // получить максимальное число
```

Добавьте в интерфейс `MathTaskGenerator` **default** метод:
```java
/**
 * @return разница между максимальным и минимальным возможным числом
 */
default int getDiffNumber();
```

## ★ EnumSet

Сейчас сигнатуры `ExpressionTaskGenerator` и `EquationTaskGenerator` выглядят не очень красиво, приходится передавать туда 4 була для каждого оператора. Сделайте `enum Operation` внутри интерфейса `MathTask` и передавайте в `ExpressionTaskGenerator` и `EquationTaskGenerator` вместо булов `EnumSet`.

## ★★ Generic Task Generators

Добавьте параметризацию в интерфейс `TaskGenerator` таким образом, чтобы он генерировал определенный тип задач. Метод `generate` теперь должен возвращать не просто `Task`, а параметр `T`, который этот интерфейс реализует. Понятно, что при этом классы `ExpressionTaskGenerator` и `EquationTaskGenerator` не должны быть дженериками, они всегда возвращают `ExpressionTask` и `EquationTask`, соответственно. А вот над тем, добавлять ли параметризацию в `GroupTaskGenerator` и `PoolTaskGenerator`, предлагается подумать самостоятельно. 

## ★ UML
В каком-нибудь онлайн UML-редакторе (если очень хочется, можно и в пеинте) сделайте схему с участием всех интерфейсов и классов. Это схему нужно приложить в AnyTask к ссылке на github.

## Добавляем исключения
Добавьте исключения везде, где это необходимо. Например, когда у `Quiz` вызывается метод `getMark()`, пока тест не завершен или в конструктор `*MathTask` `maxNumber` передается меньше, чем `minNumber` и т.д. В случае некорректных `minNumber`, `maxNumber` уместно использовать `IllegalArgumentException`, для раннего вызова `getMark()` стоит сделать свое исключение, назвать `QuizNotFinishedException`. Все свои исключения стоит создавать в пакете `exceptions`.

## Добавляем тесты (Quiz) и проверяем
Теперь добавьте в метод `getQuizMap()` тестов (минимум 5) и тщательно протестируйте приложение. Обязательно используйте все созданные классы, постарайтесь придумать свои `TaskGenerator`. Например, который генерирует задания вида _"У **A** было X яблок, он(она) подарил(а) **B** Y яблок. Сколько яблок осталось у **A**?"_.
