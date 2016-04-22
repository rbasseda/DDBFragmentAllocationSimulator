package simulator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Title:        Routing System Protocol
 * Description:  This Project is the course project of "Principles of network design"
 * Copyright:    Copyright (c) 2005
 * Company:      UT
 * @author Reza Basseda, Mostafa Haghir Chehreghani
 * @version 1.0
 */

public class RoutingUI extends JFrame {
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanelMain = new JPanel();
  JButton jButtonStartSim = new JButton();
  JTextArea jTextAreaOut = new JTextArea();
  JLabel jLabelSimL = new JLabel();
  JTextField jTextFieldSimL = new JTextField();
  JLabel jLabelAllocType = new JLabel();
  JRadioButton jRadioJ2Dest = new JRadioButton();
  JRadioButton jRadioJ2Next = new JRadioButton();
  JScrollPane jScrollPaneLinkOut = new JScrollPane();
  JTextArea jTextAreaOutLink = new JTextArea();
  ButtonGroup buttonGroupAllocationType = new ButtonGroup();
  JScrollPane jScrollPaneOutAll = new JScrollPane();
  JTextArea jTextAreaOutAll = new JTextArea();

  /**Construct the frame*/
  public RoutingUI() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception  {
    //setIconImage(Toolkit.getDefaultToolkit().createImage(RoutingUI.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(732, 642));
    this.setTitle("Setting Network");
    jPanelMain.setBackground(UIManager.getColor("Desktop.background"));
    jPanelMain.setLayout(null);
    jButtonStartSim.setText("StartSimulation");
    jButtonStartSim.setBounds(new Rectangle(117, 5, 119, 27));
    jButtonStartSim.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonStartSim_actionPerformed(e);
      }
    });
    jTextAreaOut.setWrapStyleWord(true);
    jTextAreaOut.setText("No Result");
    jTextAreaOut.setEditable(false);
    jTextAreaOut.setBounds(new Rectangle(16, 165, 594, 205));
    jLabelSimL.setText("Simulation Length");
    jLabelSimL.setBounds(new Rectangle(70, 42, 103, 31));
    jTextFieldSimL.setText("800000");
    jTextFieldSimL.setBounds(new Rectangle(191, 41, 161, 34));
    jLabelAllocType.setText("Data Allocation Type Type");
    jLabelAllocType.setBounds(new Rectangle(11, 96, 161, 33));
    jRadioJ2Dest.setText("Send Data to Dest");
    jRadioJ2Dest.setBounds(new Rectangle(192, 101, 177, 22));
    jRadioJ2Next.setText("Send Data to Next Node");
    jRadioJ2Next.setBounds(new Rectangle(192, 131, 175, 25));
    jScrollPaneLinkOut.setBounds(new Rectangle(15, 382, 586, 117));
    jTextAreaOutLink.setText("jTextArea1");
    jScrollPaneOutAll.setBounds(new Rectangle(18, 516, 595, 94));
    jTextAreaOutAll.setText("Total Status");
    contentPane.add(jPanelMain, BorderLayout.CENTER);
    jPanelMain.add(jButtonStartSim, null);
    jPanelMain.add(jLabelSimL, null);
    jPanelMain.add(jTextFieldSimL, null);
    jPanelMain.add(jLabelAllocType, null);
    jPanelMain.add(jRadioJ2Dest, null);
    jPanelMain.add(jRadioJ2Next, null);
    jPanelMain.add(jTextAreaOut, null);
    jPanelMain.add(jScrollPaneLinkOut, null);
    jPanelMain.add(jScrollPaneOutAll, null);
    jScrollPaneOutAll.getViewport().add(jTextAreaOutAll, null);
    jScrollPaneLinkOut.getViewport().add(jTextAreaOutLink, null);
    buttonGroupAllocationType.add(jRadioJ2Dest);
    buttonGroupAllocationType.add(jRadioJ2Next);
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  void jButtonStartSim_actionPerformed(ActionEvent e) {
    StringBuffer Out=new StringBuffer("");
    StringBuffer OutLink=new StringBuffer("");
    StringBuffer OutAll=new StringBuffer("");
    int count=0;
    int LinkCount;
    int RType=0;
    char AT='N';
    if(jRadioJ2Dest.isSelected()==true)AT='N';
    if(jRadioJ2Next.isSelected()==true)AT='R';
    Coordinator coord=new Coordinator(Integer.valueOf(jTextFieldSimL.getText()).intValue(),0,AT);
//    coordinator coord=new coordinator(100000);
    coord.runCoord(this);
    LinkCount=coord.getMaxLinks();
    for(count=0;count<10;++count)
      Out.append("\n"+coord.nodeStatus(count));
    jTextAreaOut.setText(Out.toString());
    for(count=0;count<LinkCount;++count)
      OutLink.append("\n"+coord.LinkStatus(count));
    for(count=0;count<10;++count)
      OutLink.append("\n DataItem "+Integer.toString(count)+" Located "+coord.getDataItemLoc(count));
    jTextAreaOutLink.setText(OutLink.toString());
    OutAll.append(coord.getOverAllRep().toString());
    jTextAreaOutAll.setText(OutAll.toString() );


  }
  public void updateClock(int i){
    jTextFieldSimL.setText(Integer.toString(i));
  }
}