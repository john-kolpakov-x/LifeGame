package wow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;

public class FieldPanel extends JPanel {
  private final Field field;

  private Pos leftTop = Pos.get(0, 0);
  private Pos deltaLeftTop = null;
  private int scale = 1;

  private Pos leftTop() {
    Pos d = this.deltaLeftTop;
    if (d == null) return leftTop;
    return d.add(leftTop);
  }

  public FieldPanel(Field field) {
    this.field = field;

    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        scale -= e.getWheelRotation();

        if (scale < 1) scale = 1;
        if (scale > 20) scale = 20;

        repaint();
      }

      int pressedAtX, pressedAtY;

      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          Pos leftTop = leftTop();
          pressedAtX = e.getX() / scale - leftTop.x;
          pressedAtY = (getHeight() - e.getY()) / scale - leftTop.y;
          return;
        }

        if (e.getButton() == MouseEvent.BUTTON2) {
          deltaLeftTop = null;
          return;
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (deltaLeftTop != null) {
          leftTop = leftTop.add(deltaLeftTop);
          deltaLeftTop = null;
        }
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        int x = e.getX() / scale - leftTop.x;
        int y = (getHeight() - e.getY()) / scale - leftTop.y;
        deltaLeftTop = Pos.get(x - pressedAtX, y - pressedAtY);
        repaint();
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        int x = e.getX() / scale - leftTop.x, y = (getHeight() - e.getY()) / scale - leftTop.y + 1;
        field.set(x, y, !field.get(x, y));
        repaint();
      }
    };

    addMouseMotionListener(mouseAdapter);
    addMouseWheelListener(mouseAdapter);
    addMouseListener(mouseAdapter);
  }

  public void step() {
    field.step();
    repaint();
  }

  @Override
  public void paint(Graphics g) {
    Pos leftTop = leftTop();
    int scale = this.scale;

    int screenHeight = getHeight(), screenWidth = getWidth();

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, screenWidth, screenHeight);

    Rect area = field.area();

    g.setColor(Color.GREEN);
    {
      int x1 = area.from.x + leftTop.x;
      int y1 = area.from.y + leftTop.y;
      int x2 = area.to.x + leftTop.x;
      int y2 = area.to.y + leftTop.y;

      x1 *= scale;
      x2 *= scale;
      y1 = screenHeight - y1 * scale;
      y2 = screenHeight - y2 * scale;

      x2 += scale;
      y1 += scale;

      g.drawLine(x1, y1, x1, y2);
      g.drawLine(x1, y2, x2, y2);
      g.drawLine(x2, y2, x2, y1);
      g.drawLine(x2, y1, x1, y1);
    }


    g.setColor(Color.BLACK);

    for (Pos pos : field.getLives()) {
      int x = pos.x + leftTop.x;
      int y = pos.y + leftTop.y;

      g.fillRect(x * scale, screenHeight - y * scale, scale, scale);
    }
  }
}
