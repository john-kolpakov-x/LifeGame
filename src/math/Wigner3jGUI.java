package math;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * An interactive interface to the Wigner3j class.
 * The GUI allows to preselect one of the symbols if the number of j-terms is small
 * (6j up to 15j), or to enter any other connectivity for the triads of j-values.
 * The actual j-values are entered as integers (2j+1) and the computation of one
 * value (in exact square root representation) is started manually.
 *
 * @since 2011-02-15
 */
public class Wigner3jGUI implements ActionListener, ListSelectionListener {
  /**
   * The master window of the session
   */
  JFrame frame;

  /* global labels
  */
  Label Lbl0;
  Label Lbl1;

  JButton sear;
  JList<String> searJ;
  String[] searOpt = {"6j", "9j", "12j 1st", "12j 2nd (not sym)", "15j 1st", "15j 2nd", "15j 3rd", "15j 4th", "15j 5th"};

  /**
   * Field with the triads inputs
   */
  TextArea inpGtRia;

  /**
   * Field with the J-value inputs
   */
  TextArea inpGjVal;

  /**
   * Field of the outputs.
   */
  TextArea outG;

  GridBagLayout gridBag;
  GridBagConstraints gridConstrains;

  /**
   * @author Richard J. Mathar
   * @since 2011-02-15
   */
  public void init() {
    frame = new JFrame("Wigner3jGUI");

    Lbl0 = new Label("Input: (Triads upper area, values 2J+1 second area");
    Lbl1 = new Label("Output:");

    sear = new JButton("Compute");
    sear.setActionCommand("compute");
    sear.addActionListener(this);
    sear.setToolTipText("Compute a general 3jn  value");

    searJ = new JList<>(searOpt);
    searJ.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    searJ.addListSelectionListener(this);

    Font defFont = new Font("Monospaced", Font.PLAIN, 11);

    frame.setBackground(new Color(250, 250, 250));
    frame.setForeground(new Color(0, 0, 0));
    Color fg = new Color(0, 200, 0);
    Color bg = new Color(10, 10, 10);

    gridBag = new GridBagLayout();
    frame.setLayout(gridBag);

    gridConstrains = new GridBagConstraints();
    gridConstrains.gridx = 0;
    gridConstrains.gridy = GridBagConstraints.RELATIVE;

    inpGtRia = new TextArea("", 4, 80);
    inpGtRia.setFont(defFont);
    inpGtRia.setForeground(fg);
    inpGtRia.setBackground(bg);

    inpGjVal = new TextArea("", 10, 80);
    inpGjVal.setFont(defFont);
    inpGjVal.setForeground(fg);
    inpGjVal.setBackground(bg);

    outG = new TextArea("", 12, 80);
    outG.setEditable(false);
    outG.setFont(defFont);
    outG.setForeground(fg);
    outG.setBackground(bg);

    frame.add(Lbl0);
    gridBag.setConstraints(Lbl0, gridConstrains);

    frame.add(inpGtRia);
    gridBag.setConstraints(inpGtRia, gridConstrains);

    frame.add(inpGjVal);
    gridBag.setConstraints(inpGjVal, gridConstrains);

    frame.add(sear);
    gridBag.setConstraints(sear, gridConstrains);
    frame.add(searJ);
    gridBag.setConstraints(searJ, gridConstrains);

    frame.add(Lbl1);
    gridBag.setConstraints(Lbl1, gridConstrains);

    frame.add(outG);
    gridBag.setConstraints(outG, gridConstrains);

    frame.pack();
    frame.setVisible(true);
  } /* init */

  /**
   * @author Richard J. Mathar
   * @since 2010-08-27
   */
  public void compute() {
    String tr = inpGtRia.getText();
    String[] trias = new String[4];

                /* Read the trias configuration from inpGtRia into trias[0..2], skipping lines
                * that start with a hash mark.
                */
    Scanner s = new Scanner(tr);
    for (int l = 0; l < 3; ) {
      try {
        trias[l] = s.nextLine().trim();
        if (!trias[l].startsWith("#"))
          l++;
      } catch (Exception e) {
        outG.setText("ERROR: less than 3 lines in the triad definition");
        return;
      }
    }
                /* Read the J values from inpGjVal into triangles[3] in a loop
                */
    String j = inpGjVal.getText();
    s = new Scanner(j);
    while (true) {
      try {
        trias[3] = s.nextLine().trim();
      } catch (Exception e) {
        return;
      }
      if (!trias[3].startsWith("#")) {
        try {
          BigSurdVec w = Wigner3j.wigner3j(trias[0], trias[1], trias[2], trias[3]);
          outG.append(w.toString() + " = " + w.doubleValue());
        } catch (Exception e) {
          outG.append(e.toString());
          e.printStackTrace();
        }
        outG.append(" # J = ");
        Scanner num = new Scanner(trias[3]);
        while (num.hasNextInt()) {
          int twoj1 = num.nextInt();
          Rational jfrac = new Rational(twoj1 - 1, 2);
          outG.append(jfrac.toString() + " ");
        }
        outG.append("\n");
      }
    }
  } /* compute */

  /**
   * Interpreter parser loop.
   *
   * @param e the information on which button had been pressed in the GUI
   * @author Richard J. Mathar
   * @since 2011-02-15
   */
  public void actionPerformed(ActionEvent e) {
    String lin = e.getActionCommand();
                /* debugging
                System.out.println("Ac"+e.paramString()) ;
                System.out.println(lin) ;
                */
    if ("compute".equals(lin)) {
      outG.setText("");
      compute();
    }
  } /* actionPerformed */

  /**
   * Interpreter parser loop.
   *
   * @param e the information on which of the 3jn templates had been selected in the Menu
   * @author Richard J. Mathar
   * @since 2011-02-18
   */
  public void valueChanged(ListSelectionEvent e) {
    switch (searJ.getMinSelectionIndex()) {
      case 0:
        inpGtRia.setText("6\n");
        inpGtRia.append("1 2 -3 -1 5 6\n");
        inpGtRia.append("4 -5 3 -4 -2 -6");
        outG.setText("");
        break;
      case 1:
                        /* Figure 18.1 index map.
                        * j1=1, j2=2, j3=3
                        * k1=4, k2=5, k3=6
                        * l1=7, l2=8, l3=9
                        */
        inpGtRia.setText("9\n");
        inpGtRia.append("1 3 2 4 6 5 7 9 8 # (j1 j3 j2) (k1 k3 k2) (l1 l3 l2)\n");
        inpGtRia.append("-2 -8 -5 -6 -3 -9 -7 -4 -1 # (j2 l2 k2) (k3 j3 l3) (l1 k1 j1)");
        outG.setText("");
        break;
      case 2:
                        /* Figure 19.1 and 19.2, index map, including the sign reveal of the l.
                        * Assume input order j1..j4, l1..l4, k1..k4.
                        * j1=1, j2=2, j3=3, j4=4
                        * l1=5, l2=6, l3=7, l4=8
                        * k1=9, k2=10, k3=11, k4=12
                        */
        inpGtRia.setText("12\n");
        inpGtRia.append("1 12 -8 -1 5 -2 2 6 -3 3 7 -4 # (j1 k4 l4) (j1 l1 j2) (j2 l2 j3) (j3 l3 j4)\n");
        inpGtRia.append("4 8 -9 9 -5 -10 10 -6 -11 11 -7 -12 # (j4 l4 k1) (k1 l1 k2) (k2 l2 k3) (k3 l3 k4)");
        outG.setText("");
        break;
      case 3:
        inpGtRia.setText("12\n");
        inpGtRia.append("1 5 9 -9 -2 -7 2 11 8 -8 -12 -4 # (j1 l1 k1) (k1 j2 l3 ) (j2 k3 l4) (l4 k4 j4)\n");
        inpGtRia.append("4 7 10 -10 -3 -5 3 6 12 -6 -11 -1 # (j4 l3 k2) (k2 j3 l1) (j3 l2 k4) (l2 k3 j1)");
        outG.setText("");
        break;
      case 4:
                        /* Figure 20.2 to 20.3, index map.
                        * j1=1, j2=2, j3=3, j4=4, j5=5
                        * l1=6, l2=7, l3=8, l4=9, l5=10
                        * k1=11, k2=12, k3=13, k4=14, k5=15
                        */
        inpGtRia.setText("15\n");
        inpGtRia.append("1 -6 2 -2 -7 3 -3 -8 4 -4 -9 5 -5 -10 11 # (j1 l1 j2)(j2 l2 j3)(j3 l3 j4)(j4 l4 j5)(j5 l5 k1)\n");
        inpGtRia.append("-11 6 12 -12 7 13 -13 8 14 -14 9 15 -15 10 -1 # (k1 l1 k2)(k2 l2 k3)(k3 l3 k4)(k4 l4 k5)(k5 l5 j1)");
        outG.setText("");
        break;
      case 5:
        inpGtRia.setText("15\n");
        inpGtRia.append("-1 -6 2 -2 -7 3 -3 -8 4 -4 -9 5 1 -5 -10 # (j1 l1 j2)(j2 l2 j3)(j3 l3 j4)(j4 l4 j5)(j1 j5 l5)\n");
        inpGtRia.append("11 -15 10 9 15 -14 8 14 -13 7 13 -12 6 12 -11 # (k1 k5 l5)(l4 k5 k4)(l3 k4 k3)(l2 k3 k2)(l1 k2 k1)");
        outG.setText("");
        break;
      case 6:
                        /* Figure 20.4a, index map.
                        * k1=1, k1'=2, k=3, k'=4, k2=5, k2'=6
                        * p1=7, p=8, p2=9,
                        * j1=10, j1'=11 j=12 j'=13 j2=14 j2'=15
                        */
        inpGtRia.setText("15\n");
        inpGtRia.append("-13 -12 -8 12 14 10 -10 -1 7 -7 -11 -2 2 4 6 # (j' j p)(j j2 j1)(j1 k1 p1)(p1 j1' k1')(k1' k' k2')\n");
        inpGtRia.append("-4 -3 8 1 3 5 -14 -5 9 -15 -6 -9 15 11 13 # (k' k p)(k1 k k2)(j2 k2 p2)(j2' k2' p2)(j2' j1' j')");
        outG.setText("");
        break;
      case 7:
                        /* Figure 20.5a, index map.
                        * j1=1, k1=2 s1=3 k1'=4 j1'=5
                        * p=6 l=7 s=8 l'=9 p'=10
                        * j2=11 k2=12 s2=13 k2'=14 j2'=15
                        */
        inpGtRia.setText("15\n");
        inpGtRia.append("-14 -12 -8 12 11 -10 -11 13 -7 7 -1 3 2 1 6 # (k2' k2 s)(k2 j2 p')(j2 s2 l)(l j1 s1)(k1 j1 p)\n");
        inpGtRia.append("-4 -2 8 10 4 5 9 -5 -3 -13 -9 -15 15 -6 14 # (k1' k1 s)(p' k1' j1')(l' j1' s1)(s2 l' j2')(j2' p k2')");
        outG.setText("");
        break;
      case 8:
                        /* Figure 20.6, index map.
                        * k1=1 k1'=2 j1=3 l1=4 l1'=5
                        * k2=6 k2'=7 j2=8 l2=9 l2'=10
                        * k3=11 k3'=12 j3=13 l3=14 l3'=15
                        */
        inpGtRia.setText("15\n");
        inpGtRia.append("-15 1 -7 -4 -11 7 5 4 -3 -12 -5 6 12 -9 -1 # (l3' k1 k2')(l1 k3 k2')(l1' l1 j1)(k3' l1' k2)(k3' l2 k1)\n");
        inpGtRia.append("9 -8 10 -10 11 -2 -14 -6 2 14 -13 15 3 8 13 # (l2 j2 l2')(l2' k3 k1')(l3 k2 k1')(l3 j3 l3')(j1 j2 j3)");
        outG.setText("");
        break;
    }
  } /* valueChanged */

  /**
   * Main entry point.
   * not taking any command line options:<br>
   * java -jar Wigner3jGUI.jar<br>
   *
   * @param args There are no arguments or command line options.
   * @author Richard J. Mathar
   * @since 2012-02-16
   */
  public static void main(String[] args) {
    Wigner3jGUI g = new Wigner3jGUI();
    g.init();
  } /* main */

} /* Wigner3jGUI */
