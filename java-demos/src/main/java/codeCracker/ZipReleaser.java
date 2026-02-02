package codeCracker;


import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.util.List;


public class ZipReleaser {

    private static String filePath;
    private static String releasePath;

    public static void main(String[] args) {
        // 设置路径
        filePath = "H:\\迅雷下载\\GHS\\GJ-20-311-T7214.zip";
        releasePath = "H:\\迅雷下载\\GHS\\file";

        // 解压方法
        unZip(filePath, releasePath, 2);
    }

    /*
     * 自动尝试所有密码组合
     */
    public static boolean unZip(String source, String dest, int passwordLength){
        try {
            // 准备文件
            File target = new File(source);
            ZipFile zipFile = new ZipFile(target);
            File destDir = new File(dest);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // 准备密码
            List<String> passwords = CodeGenerator.getStr
                    (true, true, false, passwordLength);

            for (String password : passwords) {
                // 设置密码
                if (zipFile.isEncrypted()) {
                    zipFile.setPassword(password.toCharArray());
                }else {
                    System.out.println("没密码");
                    break;
                }

                // 解压
                zipFile.extractAll(dest);

                if (!zipFile.isEncrypted()) {
                    System.out.println(password);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
