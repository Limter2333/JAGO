package tools;

import java.awt.*;
import java.awt.event.InputEvent;

public class ClickTool {
    public static void main(String[] args) {
        try {
            click(1000);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void click(long time) throws InterruptedException, AWTException {
        Robot robot= new Robot();
        while (true){
            //模拟鼠标按下左键
            robot.mousePress(InputEvent.BUTTON1_MASK);
            Thread.sleep(1000);
            //模拟鼠标松开左键
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(time);
        }
    }
}
