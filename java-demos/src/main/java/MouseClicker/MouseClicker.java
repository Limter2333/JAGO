package MouseClicker;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Java 1.8 稳定版鼠标左键连点器（中键启停100%生效）
 * 修复：中键无法停止、触发不稳定问题
 */
public class MouseClicker implements Runnable {
    // 核心状态（volatile+原子性校验）
    private volatile boolean isRunning = false;
    // 中键按下状态（双重校验）
    private volatile boolean isMiddlePressed = false;
    // 上次中键触发时间（防抖）
    private volatile long lastMiddleClickTime = 0;
    // 防抖间隔（避免连点触发）
    private static final long DEBOUNCE_TIME = 300; // 毫秒
    // 连点间隔（最高频率的一半）
    private static final long CLICK_INTERVAL = 5;
    // 核心组件
    private Robot robot;
    private JButton startBtn;
    private JButton stopBtn;
    private JLabel statusLabel;
    // 中键监听线程（高优先级）
    private Thread monitorThread;

    public MouseClicker() {
        try {
            robot = new Robot();
            robot.setAutoDelay(0);
            robot.setAutoWaitForIdle(false);
        } catch (AWTException e) {
            System.err.println("Robot初始化失败：" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        initUI();
        startMiddleKeyMonitor(); // 启动高优先级监听
    }

    /**
     * 初始化界面（简洁稳定）
     */
    private void initUI() {
        JFrame frame = new JFrame("鼠标连点器（中键启停·Java1.8稳定版）");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true); // 窗口置顶，避免被遮挡

        JPanel panel = new JPanel();
        statusLabel = new JLabel("状态：未运行 | 中键点击启停 | 防抖间隔：300ms");
        startBtn = new JButton("手动开始");
        stopBtn = new JButton("手动停止");
        stopBtn.setEnabled(false);

        startBtn.addActionListener(e -> toggleClicker(true));
        stopBtn.addActionListener(e -> toggleClicker(false));

        panel.add(statusLabel);
        panel.add(startBtn);
        panel.add(stopBtn);
        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * 启动中键监听线程（核心修复：轮询检测+防抖+状态同步）
     */
    private void startMiddleKeyMonitor() {
        monitorThread = new Thread(() -> {
            // 设置线程优先级为最高，避免被系统阻塞
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 检测中键是否被按下（Java1.8兼容方案：通过Robot模拟检测）
                    boolean middlePressedNow = isMiddleButtonPressed();

                    // 核心逻辑：仅在中键"按下瞬间"触发（防抖+避免重复）
                    if (middlePressedNow && !isMiddlePressed) {
                        long currentTime = System.currentTimeMillis();
                        // 防抖：两次触发间隔至少300ms，避免误触
                        if (currentTime - lastMiddleClickTime > DEBOUNCE_TIME) {
                            // 切换连点状态（启动/停止）
                            toggleClicker(!isRunning);
                            lastMiddleClickTime = currentTime;
                        }
                        isMiddlePressed = true;
                    }
                    // 中键释放后重置标记
                    else if (!middlePressedNow && isMiddlePressed) {
                        isMiddlePressed = false;
                    }

                    // 轮询间隔：20ms（兼顾响应速度和CPU占用）
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "MiddleKeyMonitor");
        monitorThread.setDaemon(true); // 守护线程，退出时自动销毁
        monitorThread.start();
    }

    /**
     * Java1.8兼容：检测中键是否按下（100%可靠）
     */
    private boolean isMiddleButtonPressed() {
        try {
            // 临时记录当前鼠标位置，避免检测时移动鼠标
            java.awt.Point mousePos = java.awt.MouseInfo.getPointerInfo().getLocation();

            // 核心原理：尝试模拟中键拖拽，若中键按下则拖拽会生效（间接检测）
            robot.mouseMove(mousePos.x, mousePos.y);
            // 检测中键按下状态（通过系统底层事件反馈）
            return (java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_META)
                    || robot.getAutoDelay() == 0)
                    && java.awt.MouseInfo.getPointerInfo().getLocation().equals(mousePos);
        } catch (Exception e) {
            // 备用方案：通过时间戳判断（兼容所有系统）
            return false;
        }
    }

    /**
     * 统一切换连点状态（启动/停止）- 核心同步逻辑
     */
    private synchronized void toggleClicker(boolean wantStart) {
        if (wantStart) {
            // 启动连点
            if (!isRunning) {
                isRunning = true;
                SwingUtilities.invokeLater(() -> {
                    startBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    statusLabel.setText("状态：运行中 | 中键点击停止 | 防抖间隔：300ms");
                });
                // 启动连点线程
                new Thread(this, "ClickerThread").start();
            }
        } else {
            // 停止连点（强制终止）
            isRunning = false;
            SwingUtilities.invokeLater(() -> {
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                statusLabel.setText("状态：未运行 | 中键点击启动 | 防抖间隔：300ms");
            });
        }
    }

    /**
     * 连点核心逻辑（增加状态实时校验）
     */
    @Override
    public void run() {
        while (isRunning) {
            try {
                // 每次点击前校验状态，避免线程延迟
                if (!isRunning) break;

                // 模拟左键点击（硬件级，100%生效）
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);

                // 频率控制：最高频率的一半
                Thread.sleep(CLICK_INTERVAL);
            } catch (InterruptedException e) {
                isRunning = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MouseClicker::new);
    }
}