# Java 5 特性演示

这个项目包含了Java 5引入的重要特性的完整演示代码。

## 包含的特性

### 1. 自动装箱/拆箱 (Autoboxing/Unboxing)
- **文件**: `AutoboxingDemo.java`
- **特性说明**: 自动在基本数据类型和对应的包装类之间转换
- **主要演示内容**:
  - 基本的装箱/拆箱操作
  - 集合中使用自动装箱
  - Integer缓存机制
  - 性能对比和注意事项

### 2. 可变参数 (Varargs)
- **文件**: `VarargsDemo.java`
- **特性说明**: 允许方法接受可变数量的参数
- **主要演示内容**:
  - 基本可变参数使用
  - 混合参数类型
  - 方法重载与可变参数
  - 实际应用场景（日志、配置等）

### 3. 并发包 (java.util.concurrent)
- **文件**: `ConcurrentDemo.java`
- **特性说明**: Java 5引入的高级并发工具包
- **主要演示内容**:
  - ExecutorService线程池
  - Callable和Future
  - CountDownLatch同步工具
  - Semaphore信号量
  - ConcurrentHashMap线程安全Map
  - BlockingQueue阻塞队列
  - AtomicInteger原子操作
  - ReentrantLock可重入锁

## 如何运行

### 方法1: 使用Maven运行
