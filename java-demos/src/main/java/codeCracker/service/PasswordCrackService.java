package codeCracker.service;

import java.util.ArrayList;
import java.util.List;

public interface PasswordCrackService {

    // 线程数
    Integer THREAD_NUM = 5;

    String run(String source, String dest);

    default public List<List<String>> getShardingList(List<String> codes) {
        int codesNum = codes.size();

        List<List<String>> dataList = new ArrayList<>();

        int index = 0;
        int temp = 0;

        if (temp < codesNum) {
            temp = (index + 1) * codesNum;
            dataList.add(codes.subList(index * codesNum, temp > codesNum ? codesNum : temp));
        }

        return dataList;
    }
}
