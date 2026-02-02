package codeCracker.service.impl;

import codeCracker.service.PasswordCrackService;

import java.util.List;
import java.util.concurrent.*;

public class ThreadProcessPasswordCrackServiceImpl implements PasswordCrackService {

    private ThreadPoolExecutor threadPoolExecutor;

    public ThreadProcessPasswordCrackServiceImpl() {
        this.threadPoolExecutor = new ThreadPoolExecutor(4, 20, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public String run(String source, String dest) {
//        List<String> numberStr = StringUtil.getNumberStr(6);
//        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
//        numberStr.forEach(num -> queue.offer(num));
//        long startTime = System.currentTimeMillis();
//        while (!queue.isEmpty()) {
//            threadPoolExecutor.execute(() -> {
//                String name = Thread.currentThread().getName();
//                String key = queue.poll();
//                if (StringUtils.isNotBlank(key)) {
//                    boolean result = UnZipUtils.unZip(source, dest, key);
//                    if (result) {
//                        try (FileWriter fileWriter = new FileWriter(dest + "\\password.txt")) {
//                            fileWriter.write(key);
//                            System.out.println("线程：" + name + ",密码是：" + key);
//                            queue.clear();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        System.out.println("线程：" + name + "," + key + "密码错误");
//                    }
//                }
//            });
//        }
//
//        final long endTime = System.currentTimeMillis();
//        System.out.println("共花费：" + (endTime - startTime) / 1000 + "秒");
        return null;

    }

    @Override
    public List<List<String>> getShardingList(List<String> codes) {
        return PasswordCrackService.super.getShardingList(codes);
    }

}
