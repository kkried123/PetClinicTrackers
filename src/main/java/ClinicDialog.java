import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class ClinicDialog {
    private ClinicDialog() {}

    public static boolean form(
            Component parent, String title,
            Object[] fields, Runnable submit) {
        return open(
                parent,
                title,
                "Update the details below, then save your changes.",
                fields(fields),
                true,
                "Save changes",
                submit
        );
    }

    public static boolean confirm(
            Component parent, String title,
            String message, String action) {
        return open(
                parent,
                title,
                "Please review before continuing.",
                Style.text(message, 4),
                true,
                action,
                null
        );
    }

    public static void message(
            Component parent, String title, String message) {
        show(
                parent, title, "Pet Clinic Tracker",
                Style.text(message, 5)
        );
    }

    public static void show(
            Component parent, String title,
            String subtitle, JComponent body) {
        open(
                parent, title, subtitle,
                body, false, "Done", null
        );
    }

    private static boolean open(
            Component parent,
            String title,
            String subtitle,
            JComponent body,
            boolean canCancel,
            String action,
            Runnable submit) {

        Window owner = parent instanceof Window window
                ? window
                : parent == null
                ? null
                : SwingUtilities.getWindowAncestor(parent);

        JDialog dialog = new JDialog(
                owner, title, Dialog.ModalityType.DOCUMENT_MODAL
        );

        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        boolean[] accepted = {false};

        JTextArea error = Style.text("", 2);
        error.setForeground(new Color(0xA3443D));

        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setOpaque(false);

        if (submit != null) {
            footer.add(error, BorderLayout.NORTH);
        }

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        buttons.setOpaque(false);

        if (canCancel) {
            buttons.add(
                    Style.button("Cancel", false, dialog::dispose)
            );
        }

        JButton primary = Style.button(action, true, () -> {
            try {
                if (submit != null) {
                    submit.run();
                }

                accepted[0] = true;
                dialog.dispose();

            } catch (NumberFormatException ex) {
                error.setText(
                        "Enter valid numbers. "
                                + "Age must be a whole number of months."
                );
            } catch (IllegalArgumentException ex) {
                error.setText(ex.getMessage());
            }
        });

        buttons.add(primary);
        footer.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(
                panel(
                        title, subtitle, body,
                        footer, dialog::dispose
                )
        );

        dialog.getRootPane().setDefaultButton(primary);

        dialog.getRootPane().registerKeyboardAction(
                event -> dialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        GraphicsDevice device =
                dialog.getGraphicsConfiguration().getDevice();

        if (device.isWindowTranslucencySupported(
                GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
            dialog.setShape(
                    new RoundRectangle2D.Double(
                            0, 0,
                            dialog.getWidth(), dialog.getHeight(),
                            24, 24
                    )
            );
        }

        RootPaneContainer root =
                owner instanceof RootPaneContainer container
                        ? container
                        : null;

        Component previous =
                root == null ? null : root.getGlassPane();

        boolean wasVisible =
                previous != null && previous.isVisible();

        if (root != null) {
            JPanel shade = new JPanel() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    graphics.setColor(
                            new Color(53, 45, 38, 65)
                    );
                    graphics.fillRect(
                            0, 0, getWidth(), getHeight()
                    );
                }
            };

            shade.setOpaque(false);
            root.setGlassPane(shade);
            shade.setVisible(true);
        }

        try {
            dialog.setVisible(true);
        } finally {
            if (root != null) {
                root.setGlassPane(previous);
                previous.setVisible(wasVisible);
            }
        }

        return accepted[0];
    }

    // Shared appearance for every popup.
    static JPanel panel(
            String title,
            String subtitle,
            JComponent body,
            JComponent footer,
            Runnable close) {

        JPanel panel = Style.card(Style.BG, 24);
        panel.setLayout(new BorderLayout(0, 18));

        JPanel header = new JPanel(
                new BorderLayout(12, 0)
        );
        header.setOpaque(false);

        JPanel words = Style.column();

        words.add(
                Style.label("PET CLINIC TRACKER", 10, false)
        );
        words.add(Box.createVerticalStrut(8));

        JLabel heading = Style.label(title, 22, true);
        heading.setToolTipText(title);
        words.add(heading);

        words.add(Box.createVerticalStrut(6));
        words.add(Style.text(subtitle, 2));

        header.add(words, BorderLayout.CENTER);

        JButton x = Style.button("×", false, close);
        x.setFont(new Font("SansSerif", Font.PLAIN, 20));
        x.setBorder(new EmptyBorder(2, 10, 2, 10));
        x.setToolTipText("Close");

        JPanel closeArea = new JPanel(new BorderLayout());
        closeArea.setOpaque(false);
        closeArea.add(x, BorderLayout.NORTH);

        header.add(closeArea, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        JPanel content = Style.card(Color.WHITE, 16);
        content.setLayout(new BorderLayout());
        content.add(body, BorderLayout.CENTER);

        JScrollPane scroll = Style.scroll(content);
        scroll.setPreferredSize(
                new Dimension(
                        412,
                        Math.min(
                                390,
                                Math.max(
                                        100,
                                        content.getPreferredSize().height
                                )
                        )
                )
        );

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        panel.setPreferredSize(
                new Dimension(
                        460, panel.getPreferredSize().height
                )
        );

        return panel;
    }

    static JPanel fields(Object[] inputs) {
        JPanel form = Style.column();

        for (Object input : inputs) {
            if (input instanceof String label) {
                form.add(
                        Style.label(label, 12, true)
                );
                form.add(Box.createVerticalStrut(6));

            } else if (input instanceof JComponent field) {
                styleInput(field);

                int height =
                        field instanceof JTextField
                                || field instanceof JComboBox
                                ? 38
                                : Math.max(
                                100,
                                Math.min(
                                        340,
                                        field.getPreferredSize().height
                                )
                        );

                field.setPreferredSize(
                        new Dimension(360, height)
                );
                field.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, height)
                );

                form.add(field);
                form.add(Box.createVerticalStrut(12));
            }
        }

        return form;
    }

    private static void styleInput(JComponent field) {
        if (field instanceof JTextField
                || field instanceof JComboBox
                || field instanceof JScrollPane) {
            field.setBackground(Color.WHITE);
            field.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    new Color(0xE2D8CE)
                            ),
                            new EmptyBorder(8, 10, 8, 10)
                    )
            );
        }

        if (field instanceof JScrollPane scroll
                && scroll.getViewport().getView()
                instanceof JTextArea area) {
            area.setBackground(Color.WHITE);
            area.setMargin(new Insets(4, 4, 4, 4));
        }
    }

    public static JCheckBox choice(
            String text, boolean selected) {

        JCheckBox box = new JCheckBox(text, selected) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g =
                        (Graphics2D) graphics.create();

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.setColor(
                        isSelected()
                                ? Style.SOFT
                                : Color.WHITE
                );

                g.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 12, 12
                );
                g.dispose();

                super.paintComponent(graphics);
            }
        };

        box.setOpaque(false);
        box.setContentAreaFilled(false);
        box.setFocusPainted(false);
        box.setForeground(Style.INK);
        box.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        box.setBorder(
                new EmptyBorder(10, 10, 10, 10)
        );
        box.setIconTextGap(10);
        box.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );
        box.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        box.setIcon(new Icon() {
            public int getIconWidth() {
                return 18;
            }

            public int getIconHeight() {
                return 18;
            }

            public void paintIcon(
                    Component component,
                    Graphics graphics,
                    int x,
                    int y) {

                Graphics2D g =
                        (Graphics2D) graphics.create();

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.setColor(
                        box.isSelected()
                                ? Style.BROWN
                                : new Color(0xCDB9A8)
                );

                if (box.isSelected()) {
                    g.fillRoundRect(
                            x, y, 17, 17, 5, 5
                    );

                    g.setColor(Color.WHITE);
                    g.setStroke(
                            new BasicStroke(
                                    2,
                                    BasicStroke.CAP_ROUND,
                                    BasicStroke.JOIN_ROUND
                            )
                    );

                    g.drawLine(
                            x + 4, y + 8,
                            x + 7, y + 12
                    );
                    g.drawLine(
                            x + 7, y + 12,
                            x + 13, y + 5
                    );
                } else {
                    g.drawRoundRect(
                            x, y, 16, 16, 5, 5
                    );
                }

                g.dispose();
            }
        });

        return box;
    }
}