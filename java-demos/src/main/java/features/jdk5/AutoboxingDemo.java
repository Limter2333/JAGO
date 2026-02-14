package features.jdk5;

/**
 * 自动装箱/拆箱示例
 * Java 5 引入的特性，自动在基本数据类型和对应的包装类之间转换
 */
public class AutoboxingDemo {

    public static void main(String[] args) {
        System.out.println("=== 自动装箱/拆箱演示 ===\n");

        // 1. 自动装箱示例
        System.out.println("1. 自动装箱示例:");
        Integer integerObj = 100;  // 基本类型int自动装箱为Integer对象
        Double doubleObj = 3.14;   // 基本类型double自动装箱为Double对象
        Boolean booleanObj = true; // 基本类型boolean自动装箱为Boolean对象

        System.out.println("Integer对象: " + integerObj);
        System.out.println("Double对象: " + doubleObj);
        System.out.println("Boolean对象: " + booleanObj);
        System.out.println();

        // 2. 自动拆箱示例
        System.out.println("2. 自动拆箱示例:");
        int intValue = integerObj;     // Integer对象自动拆箱为基本类型int
        double doubleValue = doubleObj; // Double对象自动拆箱为基本类型double
        boolean boolValue = booleanObj; // Boolean对象自动拆箱为基本类型boolean

        System.out.println("拆箱后的int值: " + intValue);
        System.out.println("拆箱后的double值: " + doubleValue);
        System.out.println("拆箱后的boolean值: " + boolValue);
        System.out.println();

        // 3. 集合中使用自动装箱
        System.out.println("3. 集合中的自动装箱:");
        java.util.List<Integer> numbers = new java.util.ArrayList<>();
        numbers.add(1);    // 自动装箱：int -> Integer
        numbers.add(2);    // 自动装箱：int -> Integer
        numbers.add(3);    // 自动装箱：int -> Integer

        int sum = 0;
        for (Integer num : numbers) {
            sum += num;    // 自动拆箱：Integer -> int
        }
        System.out.println("集合元素之和: " + sum);
        System.out.println();

        // 4. 缓存机制演示
        System.out.println("4. Integer缓存机制:");
        Integer a1 = 127;
        Integer a2 = 127;
        Integer b1 = 128;
        Integer b2 = 128;

        System.out.println("127 == 127: " + (a1 == a2));  // true，-128到127有缓存
        System.out.println("128 == 128: " + (b1 == b2));  // false，超出缓存范围
        System.out.println("128 equals 128: " + (b1.equals(b2))); // true，值相等
        System.out.println();

        // 5. 运算中的自动装箱/拆箱
        System.out.println("5. 运算中的自动装箱/拆箱:");
        Integer x = 10;
        Integer y = 20;
        Integer result = x + y;  // 自动拆箱运算后再装箱
        System.out.println("10 + 20 = " + result);
        System.out.println();

        // 6. 注意事项演示
        System.out.println("6. 注意事项:");
        demonstrateNullPointerIssue();
        demonstratePerformanceConsideration();
    }

    /**
     * 演示空指针异常问题
     */
    private static void demonstrateNullPointerIssue() {
        System.out.println("空指针异常演示:");
        Integer nullInteger = null;
        try {
            int primitive = nullInteger;  // 这里会抛出NullPointerException
            System.out.println("这行不会执行");
        } catch (NullPointerException e) {
            System.out.println("捕获到空指针异常: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * 演示性能考虑
     */
    private static void demonstratePerformanceConsideration() {
        System.out.println("性能对比演示:");

        // 使用基本类型
        long startTime = System.nanoTime();
        int sum1 = 0;
        for (int i = 0; i < 1000000; i++) {
            sum1 += i;
        }
        long endTime = System.nanoTime();
        System.out.println("基本类型耗时: " + (endTime - startTime) + " 纳秒");

        // 使用包装类型
        startTime = System.nanoTime();
        Integer sum2 = 0;
        for (int i = 0; i < 1000000; i++) {
            sum2 += i;  // 涉及多次装箱/拆箱操作
        }
        endTime = System.nanoTime();
        System.out.println("包装类型耗时: " + (endTime - startTime) + " 纳秒");
        System.out.println("(包装类型由于频繁装箱拆箱会更慢)");
    }
}
