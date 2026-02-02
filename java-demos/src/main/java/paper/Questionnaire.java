package paper;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import paper.po.QuestionPo;
import paper.po.QuestionnaireInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Questionnaire {
    public static void main(String[] args) {
        int times = 25;
        for (int i = 0; i < times; i++) {
            DoQuestionnaire();
        }
    }

    public static void DoQuestionnaire(){
        // 获取问卷地址
//        String path = "C:\\Users\\Sheven\\Desktop\\微信图片_20230313224422.jpg";
//        String url = getUrlByQrCode(path);
        String url = "https://www.wjx.cn/vm/Y9nITlR.aspx";
        System.out.println("-------------");
        System.out.println(url);

        // 生成问卷
        QuestionnaireInfo questionnaireInfo = getQuestionnaireInfo();
        System.out.println("-------------");
        System.out.println("生成问卷");

        // 得到结果
        Map<String, Integer[]> result = getResult(questionnaireInfo);

        // 进入页面 获取对应标签并选中提交
        operatePage(url, result);
        System.out.println("-------------");
        System.out.println("成功进入页面");
    }

    public static String getUrlByQrCode(String path) {
        String url = "";
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(path));
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            url = result.getText();
        } catch (Exception e) {
            System.out.println(e);
        }

        return url;
    }

    /**
     * 初始化问卷
     *
     * @return questionnaireInfo 问卷信息
     */
    public static QuestionnaireInfo getQuestionnaireInfo() {

        // 初始化题库
        List<QuestionPo> questionList = new ArrayList<QuestionPo>() {{
            add(new QuestionPo("1_1", 2, new Integer[]{30, 70}, 0, 1, 0));
            add(new QuestionPo("1_2", 4, new Integer[]{15, 70, 10, 5}, 0, 1, 0));
            add(new QuestionPo("1_3", 4, new Integer[]{5, 5, 45, 45}, 0, 1, 0));
            add(new QuestionPo("1_4", 6, new Integer[]{10, 40, 25, 15, 5, 5}, 0, 1, 0));
            add(new QuestionPo("1_5", 6, new Integer[]{20, 20, 20, 20, 20, 20}, 1, 6, 0));

            for (int i = 1; i <= 19; i++) {
                    // 第一个一半
                    add(new QuestionPo("2_" + i, 5, new Integer[]{0, 0, 30, 40, 30}, 0, 1, 1));
//                    add(new QuestionPo("2_" + i, 5, new Integer[]{30, 40, 30, 0, 0}, 0, 1, 1));
            }
        }};

        // 权重校验
        for (QuestionPo questionPo : questionList) {
            if (!questionPo.judgeWeightSum()) {
                throw new RuntimeException(questionPo.getQuestSeq() + "权重出错");
            }
        }

        QuestionnaireInfo questionnaireInfo = new QuestionnaireInfo();
        questionnaireInfo.setTitle("短视频植入式广告对消费者购买意愿的影响研究");
        questionnaireInfo.setCreatedBy("Gjx");
        questionnaireInfo.setGroupNum(2);
        questionnaireInfo.setQuestionList(questionList);

        return questionnaireInfo;
    }

    /**
     * 获取结果
     *
     * @param questionPo 题目信息
     * @return
     */
    public static Integer[] getResult(QuestionPo questionPo) {
        // 初始化参数
        Set<Integer> hitSet = new HashSet<>();
        List<Integer> sourceList = new ArrayList<>();
        Integer chooseNum = questionPo.getMaxSelectNum();
        Integer[] optionWeight = questionPo.getOptionWeight();
        int sum = 0; // 统计权重总和

        // 完成取数列表
        for (int i = 0; i < optionWeight.length; i++) {
            int weightCount = optionWeight[i];
            sum += weightCount;
            for (int j = 0; j < weightCount; j++) {
                sourceList.add(i);
            }
        }

        // 取结果
        // 1-100取随机数
        Random ran = new Random();
        // 获取实际选择数
        int roundNum = chooseNum > 1 ? ran.nextInt(chooseNum - 2) + 2 : 1;
        // 获取结果
        for (int i = 0; i < roundNum; i++) {
            int ranNum = ran.nextInt(sum);
            int currentNum = sourceList.get(ranNum);
            if (!hitSet.contains(ranNum)) {
                hitSet.add(currentNum);
            } else {
                i--;
            }
        }

        // 输出结果
        return hitSet.toArray(new Integer[0]);
    }

    /**
     * 获取结果
     *
     * @param questionnaireInfo 问卷
     * @return
     */
    public static Map<String, Integer[]> getResult(QuestionnaireInfo questionnaireInfo){
        // 初始化结果参数
        Map<String, Integer[]> resultMap = new HashMap<>();

        // 获取题目
        List<QuestionPo> questionList = questionnaireInfo.getQuestionList();


        // 遍历题目得出结果并封装
        for (QuestionPo questionPo : questionList) {
            String questSeq = questionPo.getQuestSeq();
            Integer[] result = getResult(questionPo);
            resultMap.put(questSeq, result);
        }

        return resultMap;
    }

    /**
     * @param url
//     * @param questGroup 题目分组 第
     * @param result
     */
    public static void operatePage(String url, Map<String, Integer[]> result) {
        // 设置环境变量
        System.setProperty("webdriver.chrome.driver", "D:\\Program Files (x86)\\GoogleDriver\\chromedriver_win32\\chromedriver.exe");

        // 设置驱动配置
        ChromeOptions option = new ChromeOptions();
        option.addArguments("disable-infobars"); //禁止策略化
//        option.addArguments("--headless");  //浏览器不提供可视化页面（无头模式）.
        option.addArguments("--no-sandbox"); //解决DevToolsActivePort文件不存在的报错
        option.addArguments("--remote-allow-origins=*");
        option.addArguments("--disable-popup-blocking"); // 禁用弹框
        option.addArguments("--proxy-server=http://127.0.0.1:7890");

        // 反反爬虫
        option.addArguments("--disable-blink-features");
        option.addArguments("--disable-blink-features=AutomationControlled");

        option.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        option.setExperimentalOption("useAutomationExtension", false);

        // 打开页面
        ChromeDriver driver = new ChromeDriver(option);
        Map<String, Object> source = new HashMap<String, Object>() {{
            put("source", "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        }};
        driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", source);
        driver.get(url);


        // 根据随机结果遍历出所有题目的结果

        for (Map.Entry<String, Integer[]> entry : result.entrySet()) {
            String questSeq = entry.getKey();
            Integer[] resultArr = entry.getValue();
            String questFirstNum = questSeq.split("_")[0];
            String questSecNum = questSeq.split("_")[1];

            // 统计第一部分题目
            int firstPartCount = 5;

            for (int resultNum : resultArr) {
                resultNum++;

                String Xpath = "";
                // 第一部分
                if (questFirstNum.equals("1")) {
                    String selectId = "q" + questSecNum + "_" + resultNum;
                    Xpath = "//input[@id='" + selectId + "']/../a[1]";
                }
                // 第二部分
                else if (questFirstNum.equals("2")) {
                    String selectId = "div" + (Integer.valueOf(questSecNum) + firstPartCount);
                    Xpath = "//div[@id='" + selectId + "']/div[@class='scale-div']/div[1]/ul/li[" + resultNum + "]";
                }

                System.out.println(questFirstNum + " *_* " + questSecNum + ":::" + Arrays.toString(resultArr) + ":::" + Xpath);

                WebElement targetElement = driver.findElement(By.xpath(Xpath));
                targetElement.click();
            }
        }

        WebElement submitButton = driver.findElement(By.id("ctlNext"));
        submitButton.click();

        try {
            driver.manage().timeouts().implicitlyWait(2500, TimeUnit.SECONDS);

            // 反扒提示
            WebElement antiPythonConfirm = driver.findElement(By.xpath("//a[@class='layui-layer-btn0']"));
            antiPythonConfirm.click();
            WebElement antiPythonButton = driver.findElement(By.xpath("//div[@class='rect-bottom']"));
            antiPythonButton.click();

            submitButton.click();

            Thread.sleep(2500);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        driver.quit();
    }

}
