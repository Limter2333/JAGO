package features.jdk5;

/**
 * Java 5 特性综合演示主程序
 */
public class Java5FeaturesMain {
    
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("    Java 5 特性综合演示程序");
        System.out.println("=====================================\n");
        
        try {
            // 运行自动装箱/拆箱演示
            System.out.println("▶ 运行自动装箱/拆箱演示...");
            AutoboxingDemo.main(args);
            waitForUser();
            
            // 运行可变参数演示
            System.out.println("▶ 运行可变参数演示...");
            VarargsDemo.main(args);
            waitForUser();
            
            // 运行并发包演示
            System.out.println("▶ 运行并发包演示...");
            ConcurrentDemo.main(args);
            waitForUser();
            
            System.out.println("✅ 所有演示完成！");
            
        } catch (Exception e) {
            System.err.println("❌ 演示过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 等待用户按键继续
     */
    private static void waitForUser() {
        System.out.println("\n--- 按回车键继续下一个演示 ---");
        try {
            System.in.read();
            // 清空输入缓冲区
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (Exception e) {
            // 忽略异常
        }
        System.out.println();
    }
}
