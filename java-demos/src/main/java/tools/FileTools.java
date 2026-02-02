package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTools {

    /**
     * 递归文件夹内文件名
     * @param dir 主目录
     * @return 文件名列表
     */
    public static List<String> getFileNamesByPath(File dir){
        // 初始化参数
        List<String> resultList = new ArrayList<>();
        String fileName = dir.getName();

        // 文件
        if (dir.isFile()) {
            if (!fileName.contains(".torrent") && !fileName.contains(".torrent") && !fileName.contains(".bt")) {
                int startIndex = 0;
                int lastIndex = fileName.lastIndexOf(".");
                resultList.add(fileName.substring(startIndex, lastIndex));
            }
            return resultList;
        }
        // 不是文件不是文件夹
        else if (dir.isDirectory()){
            File[] files = dir.listFiles();
            for (File file : files) {
                resultList.addAll(getFileNamesByPath(file));
            }
            return resultList;
        }

        System.out.println("error!!! " + dir.getPath());
        return resultList;
    }

    /**
     * 分类目录下所有文件夹 并输出名字
     * @param path
     */
    public static void showFileNames(String path){
        // 初始化参数
        Map<String, List<String>> filenameMap = new HashMap<>();

        // 根目录
        File root = new File(path);

        // 遍历文件夹下文件夹
        File[] dirs = root.listFiles();
        for (File dir : dirs) {
            // 跳过文件
            if (dir.isDirectory()) {
                String dirName = dir.getName();
                List<String> fileNames = getFileNamesByPath(dir);
                // 输出
                System.out.println(dirName + ":");
                for (String fileName : fileNames) {
                    System.out.println(" " + fileName);
                }
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {
        String path = "H:\\迅雷下载\\已鉴定";
        showFileNames(path);
    }

}
