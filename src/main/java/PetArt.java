import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

@SuppressWarnings("serial")
public final class PetArt extends JComponent {
    private final String species;
    private final Color background;

    public PetArt(String species, int size, Color background) {
        this.species = species;
        this.background = background;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        double size = Math.min(getWidth(), getHeight());
        g.translate(
                (getWidth() - size) / 2,
                (getHeight() - size) / 2
        );
        g.scale(size / 160, size / 160);

        g.setColor(background);
        g.fillOval(0, 0, 160, 160);
        g.setColor(new Color(255, 255, 255, 100));
        g.fillOval(16, 22, 18, 18);
        g.fillOval(123, 105, 14, 14);

        if (species.equals("Bird")) drawBird(g);
        else if (species.equals("Cat")) drawCat(g);
        else drawDog(g);

        g.dispose();
    }

    private void drawDog(Graphics2D g) {
        g.setColor(new Color(0xAA6E43));
        g.fill(new Ellipse2D.Double(30, 48, 34, 77));
        g.fill(new Ellipse2D.Double(97, 48, 34, 77));
        g.setColor(new Color(0xDCA86F));
        g.fillRoundRect(48, 35, 65, 98, 65, 65);
        g.setColor(new Color(0xF3D6A9));
        g.fillOval(55, 83, 51, 44);

        face(g, 66, 81, 96, 81, 81, 101);

        g.setColor(new Color(0xD3837C));
        g.fillRoundRect(76, 112, 10, 14, 8, 8);
        g.setColor(new Color(0x8D583A));
        g.fillRoundRect(55, 128, 52, 8, 6, 6);
        g.setColor(new Color(0xE4B964));
        g.fillOval(76, 129, 10, 10);
    }

    private void drawCat(Graphics2D g) {
        g.setColor(new Color(0xAEA7B7));
        g.fill(new Polygon(
                new int[]{42, 43, 76}, new int[]{78, 29, 54}, 3
        ));
        g.fill(new Polygon(
                new int[]{84, 119, 117}, new int[]{54, 29, 80}, 3
        ));

        g.setColor(new Color(0xD7AFB2));
        g.fill(new Polygon(
                new int[]{48, 48, 65}, new int[]{65, 41, 57}, 3
        ));
        g.fill(new Polygon(
                new int[]{96, 113, 111}, new int[]{57, 41, 65}, 3
        ));

        g.setColor(new Color(0xC6BECF));
        g.fillOval(37, 49, 87, 84);
        g.setColor(new Color(0xE4DDE9));
        g.fillOval(55, 87, 50, 38);

        face(g, 61, 83, 100, 83, 81, 102);

        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(0x8F8396));

        for (int i = 0; i < 3; i++) {
            g.drawLine(48, 101 + i * 7, 26, 98 + i * 10);
            g.drawLine(113, 101 + i * 7, 135, 98 + i * 10);
        }
    }

    private void drawBird(Graphics2D g) {
        g.setColor(new Color(0x97B58C));
        g.fillOval(46, 57, 70, 80);
        g.setColor(new Color(0xDDB772));
        g.fillOval(44, 35, 73, 72);
        g.setColor(new Color(0xF5E0AB));
        g.fillOval(49, 48, 62, 54);
        g.setColor(new Color(0x6E9466));
        g.fillOval(43, 94, 32, 33);
        g.fillOval(87, 94, 29, 33);
        g.setColor(new Color(0x392F28));
        g.fillOval(62, 65, 5, 7);
        g.fillOval(94, 65, 5, 7);
        g.setColor(new Color(0xBF794A));
        g.fill(new Polygon(
                new int[]{74, 88, 81}, new int[]{77, 77, 90}, 3
        ));

        g.setStroke(new BasicStroke(
                3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
        ));
        g.setColor(new Color(0xAA814D));
        g.drawLine(69, 133, 65, 142);
        g.drawLine(94, 133, 98, 142);
    }

    private void face(
            Graphics2D g, int x1, int y1,
            int x2, int y2, int nx, int ny) {
        g.setColor(new Color(0x3B2D27));
        g.fillOval(x1 - 3, y1 - 4, 6, 8);
        g.fillOval(x2 - 3, y2 - 4, 6, 8);
        g.fillRoundRect(nx - 6, ny - 3, 12, 8, 7, 7);
        g.setStroke(new BasicStroke(
                1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
        ));
        g.draw(new Arc2D.Double(
                nx - 11, ny + 2, 12, 10, 190, 150, Arc2D.OPEN
        ));
        g.draw(new Arc2D.Double(
                nx, ny + 2, 12, 10, 200, 150, Arc2D.OPEN
        ));
    }
}