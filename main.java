import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class Main {

    // 1. Власний функціональний інтерфейс з дженериками
    @FunctionalInterface
    interface MyFunctionalInterface<T> {
        T process(T input); // головний метод

        // метод за замовчуванням
        default void printResult(T input) {
            System.out.println("Результат: " + input);
        }

        // статичний метод
        static <T> T identity(T value) {
            return value;
        }
    }

    // метод для демонстрації посилань на методи
    static Integer multiplyByTwo(Integer x) {
        return x * 2;
    }

    public static void main(String[] args) {

        // ========================
        // 2. Використання інтерфейсу через лямбду
        // ========================
        MyFunctionalInterface<Integer> lambda = x -> x + 5;
        System.out.println("Лямбда: " + lambda.process(10));
        lambda.printResult(10);

        // Посилання на статичний метод
        MyFunctionalInterface<Integer> methodRefStatic = Main::multiplyByTwo;
        System.out.println("Метод-референс (статичний): " + methodRefStatic.process(7));

        // Посилання на екземплярний метод
        String sample = "hello";
        MyFunctionalInterface<String> methodRefInstance = sample::toUpperCase;
        System.out.println("Метод-референс (екземплярний): " + methodRefInstance.process("world"));

        // ========================
        // 3. Конвеєр обробки колекції
        // ========================
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Predicate<Integer> filterEven = x -> x % 2 == 0; // фільтрація
        Function<Integer, Integer> square = x -> x * x;   // перетворення
        Consumer<Integer> printConsumer = x -> System.out.print(x + " "); // споживач

        // конвеєр
        int sum = numbers.stream()
                .filter(filterEven)
                .map(square)
                .peek(printConsumer)
                .reduce(0, Integer::sum); // агрегування
        System.out.println("\nСума квадратів парних: " + sum);

        // ========================
        // 4. Композиція функцій
        // ========================
        Function<Integer, Integer> f1 = x -> x + 2;
        Function<Integer, Integer> f2 = x -> x * 3;

        // f1 після f2
        int result1 = f1.compose(f2).apply(5); // спочатку f2, потім f1
        System.out.println("compose (f1.compose(f2)): " + result1);

        // f1 після f2 через andThen
        int result2 = f1.andThen(f2).apply(5); // спочатку f1, потім f2
        System.out.println("andThen (f1.andThen(f2)): " + result2);

        // ========================
        // 5. Прості модульні тести
        // ========================
        System.out.println("\n=== Тести ===");
        assert lambda.process(0) == 5 : "Тест 1 не пройдено";
        assert methodRefStatic.process(3) == 6 : "Тест 2 не пройдено";
        assert methodRefInstance.process("abc").equals("ABC") : "Тест 3 не пройдено";
        assert f1.compose(f2).apply(2) == 8 : "Тест 4 не пройдено"; // (2*3)+2
        assert f1.andThen(f2).apply(2) == 12 : "Тест 5 не пройдено"; // (2+2)*3
        System.out.println("Всі 5 тестів пройдено ✅");

        // ========================
        // 6. Порівняння продуктивності
        // ========================
        int size = 10_000;
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < size; i++) largeList.add(i);

        // Варіант 1: звичайний цикл
        long start1 = System.nanoTime();
        long sum1 = 0;
        for (int n : largeList) {
            if (n % 2 == 0) sum1 += n * n;
        }
        long end1 = System.nanoTime();

        // Варіант 2: stream
        long start2 = System.nanoTime();
        long sum2 = largeList.stream().filter(x -> x % 2 == 0).mapToLong(x -> x * x).sum();
        long end2 = System.nanoTime();

        System.out.println("\nПродуктивність:");
        System.out.println("Цикл for: " + (end1 - start1) / 1_000_000.0 + " ms, сума=" + sum1);
        System.out.println("Stream:    " + (end2 - start2) / 1_000_000.0 + " ms, сума=" + sum2);
        System.out.println("Коментар: Stream зазвичай трохи повільніший за цикл на малих масивах, " +
                "але дає чистий конвеєрний код та можливість паралелізації.");
    }
}
