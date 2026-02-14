package features.jdk5;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * java.util.concurrent 并发包示例
 * Java 5 引入的高级并发工具包
 */
public class ConcurrentDemo {

    public static void main(String[] args) {
        System.out.println("=== java.util.concurrent 并发包演示 ===\n");

        try {
            // 1. ExecutorService 线程池示例
            System.out.println("1. ExecutorService 线程池示例:");
            demonstrateExecutorService();
            System.out.println();

            // 2. Callable 和 Future 示例
            System.out.println("2. Callable 和 Future 示例:");
            demonstrateCallableFuture();
            System.out.println();

            // 3. CountDownLatch 示例
            System.out.println("3. CountDownLatch 示例:");
            demonstrateCountDownLatch();
            System.out.println();

            // 4. Semaphore 示例
            System.out.println("4. Semaphore 示例:");
            demonstrateSemaphore();
            System.out.println();

            // 5. ConcurrentHashMap 示例
            System.out.println("5. ConcurrentHashMap 示例:");
            demonstrateConcurrentHashMap();
            System.out.println();

            // 6. BlockingQueue 示例
            System.out.println("6. BlockingQueue 示例:");
            demonstrateBlockingQueue();
            System.out.println();

            // 7. AtomicInteger 原子操作示例
            System.out.println("7. AtomicInteger 原子操作示例:");
            demonstrateAtomicInteger();
            System.out.println();

            // 8. ReentrantLock 示例
            System.out.println("8. ReentrantLock 示例:");
            demonstrateReentrantLock();
            System.out.println();

        } catch (Exception e) {
            System.err.println("演示过程中出现异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ExecutorService 线程池演示
     */
    private static void demonstrateExecutorService() throws InterruptedException {
        // 创建固定大小的线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.println("提交5个任务到线程池:");

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("任务 " + taskId + " 开始执行，线程: " +
                        Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // 模拟工作
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("任务 " + taskId + " 执行完成");
            });
        }

        // 关闭线程池
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("线程池已关闭");
    }

    /**
     * Callable 和 Future 演示
     */
    private static void demonstrateCallableFuture() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 创建 Callable 任务
        Callable<String> task = () -> {
            System.out.println("Callable 任务开始执行...");
            Thread.sleep(2000); // 模拟耗时操作
            return "任务执行结果";
        };

        // 提交任务并获取 Future
        Future<String> future = executor.submit(task);

        System.out.println("任务已提交，等待结果...");

        // 获取结果（会阻塞直到任务完成）
        String result = future.get(3, TimeUnit.SECONDS);
        System.out.println("获得结果: " + result);

        executor.shutdown();
    }

    /**
     * CountDownLatch 演示
     */
    private static void demonstrateCountDownLatch() throws InterruptedException {
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);

        System.out.println("启动 " + threadCount + " 个线程，等待全部完成...");

        for (int i = 1; i <= threadCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("工作线程 " + workerId + " 开始工作");
                    Thread.sleep((workerId * 1000)); // 不同的处理时间
                    System.out.println("工作线程 " + workerId + " 工作完成");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown(); // 计数减一
                }
            }).start();
        }

        System.out.println("主线程等待所有工作线程完成...");
        latch.await(); // 阻塞直到计数为0
        System.out.println("所有工作线程已完成，主线程继续执行");
    }

    /**
     * Semaphore 演示
     */
    private static void demonstrateSemaphore() throws InterruptedException {
        // 创建只有2个许可的信号量（模拟2个资源）
        Semaphore semaphore = new Semaphore(2);

        System.out.println("模拟5个线程竞争2个资源:");

        for (int i = 1; i <= 5; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("线程 " + threadId + " 尝试获取资源...");
                    semaphore.acquire(); // 获取许可

                    System.out.println("线程 " + threadId + " 获得资源，开始工作");
                    Thread.sleep(2000); // 模拟使用资源

                    System.out.println("线程 " + threadId + " 释放资源");
                    semaphore.release(); // 释放许可
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        Thread.sleep(12000); // 等待所有线程完成
    }

    /**
     * ConcurrentHashMap 演示
     */
    private static void demonstrateConcurrentHashMap() throws InterruptedException {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        System.out.println("多线程环境下安全操作 ConcurrentHashMap:");

        // 启动多个线程同时操作map
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                String key = "key" + (taskId % 3); // 使某些线程操作相同key

                // 原子性操作：如果key不存在则put，否则增加value
                map.compute(key, (k, v) -> (v == null) ? 1 : v + 1);

                System.out.println("线程 " + taskId + " 操作 " + key +
                        ", 当前值: " + map.get(key));
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("最终结果:");
        map.forEach((key, value) ->
                System.out.println(key + " = " + value));
    }

    /**
     * BlockingQueue 演示
     */
    private static void demonstrateBlockingQueue() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

        System.out.println("生产者-消费者模式演示:");

        // 生产者线程
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String item = "产品-" + i;
                    queue.put(item); // 如果队列满会阻塞
                    System.out.println("生产者生产: " + item + " [队列大小: " + queue.size() + "]");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 消费者线程
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String item = queue.take(); // 如果队列空会阻塞
                    System.out.println("消费者消费: " + item + " [队列大小: " + queue.size() + "]");
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();
    }

    /**
     * AtomicInteger 原子操作演示
     */
    private static void demonstrateAtomicInteger() throws InterruptedException {
        AtomicInteger atomicCounter = new AtomicInteger(0);
        int threadCount = 10;
        int incrementsPerThread = 1000;

        System.out.println("使用 AtomicInteger 进行线程安全计数:");
        System.out.println("线程数: " + threadCount + ", 每个线程递增次数: " + incrementsPerThread);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    atomicCounter.incrementAndGet(); // 原子递增
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        int expected = threadCount * incrementsPerThread;
        System.out.println("期望值: " + expected);
        System.out.println("实际值: " + atomicCounter.get());
        System.out.println("结果" + (atomicCounter.get() == expected ? "正确" : "错误"));
    }

    /**
     * ReentrantLock 演示
     */
    private static void demonstrateReentrantLock() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        AtomicInteger sharedResource = new AtomicInteger(0); // 改为AtomicInteger

        System.out.println("使用 ReentrantLock 保护共享资源:");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 3; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    lock.lock(); // 获取锁
                    try {
                        int temp = sharedResource.get(); // 使用get()方法
                        System.out.println("线程 " + threadId + " 读取值: " + temp);

                        // 模拟一些处理时间
                        Thread.sleep(100);

                        sharedResource.incrementAndGet(); // 使用incrementAndGet()方法
                        System.out.println("线程 " + threadId + " 写入值: " + sharedResource.get());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock(); // 释放锁
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("最终共享资源值: " + sharedResource);
    }
}
