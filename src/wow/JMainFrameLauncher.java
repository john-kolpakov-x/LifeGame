package wow;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class JMainFrameLauncher {

  public static void main(String[] args) throws Exception {
    JMainFrameLauncher launcher = new JMainFrameLauncher();
    launcher.start();
  }

  private void start() throws Exception {
    JFrame frame = new JFrame();
    frame.setTitle("Life game");
    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    frame.setSize(800, 600);

    frame.setContentPane(new MainPanel(new Field()));

    SwingUtilities.invokeAndWait(() -> frame.setVisible(true));
  }

}
