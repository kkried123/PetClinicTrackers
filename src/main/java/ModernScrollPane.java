import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

public final class ModernScrollPane extends JScrollPane {
    private final Timer animation;
    private int target;

    public ModernScrollPane(Component content) {
        super(content);

        setBorder(null);
        getViewport().setBackground(Style.BG);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar bar = getVerticalScrollBar();
        bar.setUnitIncrement(20);
        bar.setPreferredSize(new Dimension(10, 0));

        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return hiddenButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return hiddenButton();
            }

            private JButton hiddenButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintTrack(
                    Graphics g, JComponent c, Rectangle bounds) {
                g.setColor(Style.BG);
                g.fillRect(
                        bounds.x, bounds.y,
                        bounds.width, bounds.height
                );
            }

            @Override
            protected void paintThumb(
                    Graphics graphics, JComponent c,
                    Rectangle bounds) {
                if (bounds.isEmpty() || !bar.isEnabled()) {
                    return;
                }

                Graphics2D g = (Graphics2D) graphics.create();

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.setColor(
                        isDragging || isThumbRollover()
                                ? Style.BROWN
                                : new Color(0xCDB9A8)
                );

                g.fillRoundRect(
                        bounds.x + 2,
                        bounds.y + 2,
                        Math.max(2, bounds.width - 4),
                        Math.max(2, bounds.height - 4),
                        8, 8
                );

                g.dispose();
            }
        });

        animation = new Timer(15, event -> {
            target = clamp(target);
            int difference = target - bar.getValue();

            if (Math.abs(difference) <= 1) {
                bar.setValue(target);
                ((Timer) event.getSource()).stop();
            } else {
                int step = (int) Math.ceil(
                        Math.abs(difference) * 0.25
                );

                bar.setValue(
                        bar.getValue()
                                + (difference > 0 ? step : -step)
                );
            }
        });

        bar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                animation.stop();
            }
        });

        setWheelScrollingEnabled(false);

        addMouseWheelListener(event -> {
            if (event.isConsumed()) return;

            int base = animation.isRunning()
                    ? target
                    : bar.getValue();

            double rotation = event.getPreciseWheelRotation();
            int amount = (int) Math.round(rotation * 60);

            if (amount == 0 && rotation != 0) {
                amount = rotation > 0 ? 1 : -1;
            }

            int next = clamp(base + amount);

            if (next == base) {
                forwardWheel(event);
                return;
            }

            target = next;
            animation.start();
            event.consume();
        });
    }

    private int clamp(int value) {
        JScrollBar bar = getVerticalScrollBar();

        return Math.max(
                bar.getMinimum(),
                Math.min(
                        value,
                        bar.getMaximum() - bar.getVisibleAmount()
                )
        );
    }

    private void forwardWheel(MouseWheelEvent event) {
        Container parent = getParent();

        while (parent != null && !(parent instanceof JScrollPane)) {
            parent = parent.getParent();
        }

        if (parent == null) return;

        Point point = SwingUtilities.convertPoint(
                this, event.getPoint(), parent
        );

        parent.dispatchEvent(new MouseWheelEvent(
                parent,
                event.getID(),
                event.getWhen(),
                event.getModifiersEx(),
                point.x,
                point.y,
                event.getXOnScreen(),
                event.getYOnScreen(),
                event.getClickCount(),
                event.isPopupTrigger(),
                event.getScrollType(),
                event.getScrollAmount(),
                event.getWheelRotation(),
                event.getPreciseWheelRotation()
        ));

        event.consume();
    }

    @Override
    public void removeNotify() {
        animation.stop();
        super.removeNotify();
    }
}