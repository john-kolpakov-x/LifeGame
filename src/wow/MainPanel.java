package wow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class MainPanel extends JPanel {

  public MainPanel(Field field) {

    setBorder(new LineBorder(Color.BLACK, 3));

    setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    topPanel.setBorder(new LineBorder(Color.GREEN, 3));
    add(topPanel, BorderLayout.PAGE_START);

    FieldPanel contentPanel = new FieldPanel(field);
    contentPanel.setBorder(new LineBorder(Color.BLUE, 3));
    add(contentPanel, BorderLayout.CENTER);

    topPanel.setLayout(new FlowLayout());

    JButton topButton1 = new JButton("Step");
    topPanel.add(topButton1);

    topButton1.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        contentPanel.step();
      }
    });

    JButton topButton2 = new JButton("▶");
    topPanel.add(topButton2);

    topButton2.addActionListener(new AbstractAction() {
      boolean playing = false;

      T thread = null;

      class T extends Thread {
        @Override
        public void run() {
          while (this == thread) {
            contentPanel.step();
            try {
              Thread.sleep(10);
            } catch (InterruptedException ignore) {
            }
          }
        }
      }

      @Override
      public void actionPerformed(ActionEvent e) {
        playing = !playing;
        topButton2.setText(playing ? "▮▮" : "▶");
        topButton1.setEnabled(!playing);

        if (!playing) {
          thread = null;
          return;
        }

        thread = new T();
        thread.start();
      }
    });

    topPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
  }
}
