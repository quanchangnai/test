package quan.test.swing;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.ArrayList;
import java.util.List;

public class SwingTools {

    static {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
        }
    }

    /**
     * 弹窗
     *
     * @param title
     * @param contents
     */
    public static void showWindow(String title, List<String> contents, boolean closeOnExit) {
        SwingUtilities.invokeLater(() -> doShowWindow(title, contents, closeOnExit));
    }

    /**
     * 执行弹窗
     *
     * @param title
     * @param contents
     * @param closeOnExit
     */
    private static void doShowWindow(String title, List<String> contents, boolean closeOnExit) {
        JFrame jFrame = new JFrame();
        jFrame.setTitle(title);
        jFrame.setSize(700, 450);
        jFrame.setLocationRelativeTo(null);
        if (closeOnExit) {
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setBorder(null);
        jFrame.setContentPane(jScrollPane);

        for (int i = 0; i < contents.size(); i++) {
            String content = contents.get(i);
            jPanel.add(Box.createVerticalStrut(5));
            JLabel jLabel = new JLabel("  " + (i + 1) + "：" + content);
            jPanel.add(jLabel);
            jPanel.add(Box.createVerticalStrut(5));
        }

        jFrame.setVisible(true);

        int fillerHeight = jFrame.getHeight() - jFrame.getPreferredSize().height;
        if (fillerHeight > 0) {
            jPanel.add(Box.createVerticalStrut(fillerHeight));
            jFrame.invalidate();
        }
    }


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("===========测试测试测试测试测试测试===============" + i);
        }
        showWindow("测试", list, true);
    }
}
