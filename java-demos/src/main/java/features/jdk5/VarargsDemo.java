package features.jdk5;

/**
 * 可变参数（Varargs）示例
 * Java 5 引入的特性，允许方法接受可变数量的参数
 */
public class VarargsDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 可变参数演示 ===\n");
        
        // 1. 基本可变参数使用
        System.out.println("1. 基本可变参数使用:");
        printNumbers(1, 2, 3, 4, 5);
        printNumbers(10, 20);
        printNumbers(); // 可以不传参数
        System.out.println();
        
        // 2. 字符串可变参数
        System.out.println("2. 字符串可变参数:");
        concatenateStrings("Hello", "World", "Java");
        concatenateStrings("单个字符串");
        concatenateStrings(); // 空参数
        System.out.println();
        
        // 3. 计算平均值
        System.out.println("3. 计算平均值:");
        double avg1 = calculateAverage(1, 2, 3, 4, 5);
        System.out.println("平均值: " + avg1);
        
        double avg2 = calculateAverage(10.5, 20.3, 30.7);
        System.out.println("平均值: " + avg2);
        System.out.println();
        
        // 4. 混合参数类型
        System.out.println("4. 混合参数类型:");
        processStudent("张三", 85, 90, 78, 92);
        processStudent("李四", 88, 86);
        System.out.println();
        
        // 5. 数组与可变参数
        System.out.println("5. 数组与可变参数:");
        int[] array = {1, 2, 3, 4, 5};
        printNumbers(array); // 可以直接传数组
        
        // 也可以展开数组
        System.out.println();
        
        // 6. 方法重载与可变参数
        System.out.println("6. 方法重载:");
        demonstrateOverloading();
        System.out.println();
        
        // 7. 实际应用示例
        System.out.println("7. 实际应用示例:");
        demonstratePracticalUsage();
    }
    
    /**
     * 打印数字序列
     */
    public static void printNumbers(int... numbers) {
        System.out.print("接收到的数字: ");
        if (numbers.length == 0) {
            System.out.println("无参数");
            return;
        }
        
        for (int i = 0; i < numbers.length; i++) {
            System.out.print(numbers[i]);
            if (i < numbers.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }
    
    /**
     * 连接字符串
     */
    public static String concatenateStrings(String... strings) {
        if (strings.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (String str : strings) {
            sb.append(str).append(" ");
        }
        String result = sb.toString().trim();
        System.out.println("连接结果: \"" + result + "\"");
        return result;
    }
    
    /**
     * 计算平均值
     */
    public static double calculateAverage(double... numbers) {
        if (numbers.length == 0) {
            return 0.0;
        }
        
        double sum = 0;
        for (double num : numbers) {
            sum += num;
        }
        double average = sum / numbers.length;
        System.out.printf("计算 %d 个数的平均值: %.2f\n", numbers.length, average);
        return average;
    }
    
    /**
     * 处理学生成绩
     */
    public static void processStudent(String name, int... scores) {
        System.out.println("学生: " + name);
        System.out.print("成绩: ");
        
        int total = 0;
        for (int score : scores) {
            System.out.print(score + " ");
            total += score;
        }
        System.out.println();
        
        double average = scores.length > 0 ? (double) total / scores.length : 0;
        System.out.printf("总分: %d, 平均分: %.1f%n", total, average);
        System.out.println();
    }
    
    /**
     * 演示方法重载
     */
    public static void demonstrateOverloading() {
        // 这些调用会匹配不同的重载方法
        overloadedMethod(1, 2);           // 匹配两个int参数的方法
        overloadedMethod(1, 2, 3);        // 匹配三个int参数的方法
        overloadedMethod(1, 2, 3, 4);     // 匹配可变参数方法
        overloadedMethod(new int[]{1, 2}); // 匹配数组参数方法
    }
    
    public static void overloadedMethod(int a, int b) {
        System.out.println("调用了: overloadedMethod(int a, int b)");
    }
    
    public static void overloadedMethod(int a, int b, int c) {
        System.out.println("调用了: overloadedMethod(int a, int b, int c)");
    }
    
    public static void overloadedMethod(int... numbers) {
        System.out.println("调用了: overloadedMethod(int... numbers)，参数个数: " + numbers.length);
    }
    
    /**
     * 演示实际应用场景
     */
    public static void demonstratePracticalUsage() {
        // 1. 日志记录
        log("INFO", "用户登录成功", "用户名: admin", "IP: 192.168.1.1");
        
        // 2. 数据库查询
        queryUsers("SELECT * FROM users WHERE", "age > 18", "AND status = 'active'");
        
        // 3. 配置设置
        configureApp("database.url=jdbc:mysql://localhost:3306/test",
                    "database.username=root",
                    "database.password=password");
        
        // 4. 数学计算
        int max = findMax(5, 2, 8, 1, 9, 3);
        System.out.println("最大值: " + max);
        
        int min = findMin(5, 2, 8, 1, 9, 3);
        System.out.println("最小值: " + min);
    }
    
    /**
     * 简单的日志记录方法
     */
    public static void log(String level, String message, String... details) {
        System.out.print("[" + level + "] " + message);
        if (details.length > 0) {
            System.out.print(" - Details: ");
            for (String detail : details) {
                System.out.print(detail + "; ");
            }
        }
        System.out.println();
    }
    
    /**
     * 模拟数据库查询
     */
    public static void queryUsers(String baseQuery, String... conditions) {
        System.out.print(baseQuery + " ");
        for (int i = 0; i < conditions.length; i++) {
            System.out.print(conditions[i]);
            if (i < conditions.length - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
    
    /**
     * 应用配置方法
     */
    public static void configureApp(String... configs) {
        System.out.println("应用配置:");
        for (String config : configs) {
            System.out.println("  " + config);
        }
    }
    
    /**
     * 查找最大值
     */
    public static int findMax(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("至少需要一个参数");
        }
        
        int max = numbers[0];
        for (int num : numbers) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }
    
    /**
     * 查找最小值
     */
    public static int findMin(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("至少需要一个参数");
        }
        
        int min = numbers[0];
        for (int num : numbers) {
            if (num < min) {
                min = num;
            }
        }
        return min;
    }
}
