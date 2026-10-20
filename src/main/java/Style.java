import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class Style {
    static final Color BG = new Color(0xFAF9F6);
    static final Color INK = new Color(0x352D26);
    static final Color MUTED = new Color(0x8A8178);
    static final Color SAND = new Color(0xF0E7DA);
    static final Color BROWN = new Color(0x9D613F);
    static final Color SOFT = new Color(0xF4EBE4);

    static void setup() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        for (String key : new String[]{
                "Label", "Button", "TextField", "ComboBox", "TextArea"}) {
            UIManager.put(
                    key + ".font",
                    new Font("SansSerif", Font.PLAIN, 12)
            );
        }

        UIManager.put("Panel.background", BG);
        UIManager.put("Label.foreground", INK);
    }

    static JLabel label(String text, int size, boolean bold) {
        JLabel label = new JLabel(text);
        label.putClientProperty("html.disable", true);
        label.setFont(new Font(
                "SansSerif", bold ? Font.BOLD : Font.PLAIN, size
        ));
        label.setForeground(bold ? INK : MUTED);
        return label;
    }

    static JPanel column() {
        JPanel panel = new JPanel() {
            @Override
            protected void addImpl(
                    Component child, Object constraints, int index) {
                if (child instanceof JComponent component) {
                    component.setAlignmentX(Component.LEFT_ALIGNMENT);
                }
                super.addImpl(child, constraints, index);
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    static JPanel card(Color color, int padding) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.setColor(color);
                g.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 22, 22
                );
                g.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(
                padding, padding, padding, padding
        ));
        return panel;
    }

    static JButton button(
            String text, boolean primary, Runnable action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.setColor(primary ? BROWN : SOFT);
                g.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 12, 12
                );
                g.dispose();

                super.paintComponent(graphics);
            }
        };

        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(primary ? Color.WHITE : BROWN);
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.addActionListener(event -> action.run());

        return button;
    }

    static JScrollPane scroll(Component component) {
        return new ModernScrollPane(component);
    }

    static JTabbedPane tabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.setForeground(BROWN);

        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabInsets = new Insets(10, 20, 10, 20);
                contentBorderInsets = new Insets(12, 0, 0, 0);
            }

            @Override
            protected void paintTabBackground(
                    Graphics graphics,
                    int placement,
                    int index,
                    int x,
                    int y,
                    int width,
                    int height,
                    boolean selected) {
                Graphics2D g = (Graphics2D) graphics.create();

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.setColor(selected ? SAND : BG);
                g.fillRoundRect(
                        x, y, width - 4, height, 14, 14
                );
                g.dispose();
            }

            @Override
            protected void paintTabBorder(
                    Graphics g, int placement, int index,
                    int x, int y, int width, int height,
                    boolean selected) {}

            @Override
            protected void paintContentBorder(
                    Graphics g, int placement, int selected) {}

            @Override
            protected void paintFocusIndicator(
                    Graphics g, int placement, Rectangle[] rectangles,
                    int index, Rectangle icon, Rectangle text,
                    boolean selected) {}
        });

        return tabs;
    }

    static JTextArea text(String value, int rows) {
        JTextArea area = new JTextArea(value, rows, 18);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setForeground(MUTED);
        return area;
    }
}