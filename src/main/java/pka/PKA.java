package main.java.pka;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.Objects;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main.java.ui.CTrayIcon;
import main.java.ui.KillProcessView;
import main.java.util.Output;

public class PKA {

  public static final String APP_NAME = "Port Killer App";

  private static CTrayIcon trayIcon;
  private static KillProcessView addJobFrame;

  public static void main(String[] args) {
    // TODO admin check https://stackoverflow.com/questions/19037339/run-java-file-as-administrator-with-full-privileges
    // TODO support for other OS than windows?
    Output.output(APP_NAME + " starting");
    loadTrayIcon();
    setLookAndFeel();
    UIManager.put("swing.boldMetal", Boolean.FALSE);
    initView();
    SwingUtilities.invokeLater(PKA::createAndShowTray);
  }

  private static void loadTrayIcon() {
    Image trayIcon = CTrayIcon.getTrayIcon();
    int trayIconWidth = new TrayIcon(Objects.requireNonNull(trayIcon)).getSize().width;
    PKA.trayIcon = new CTrayIcon(trayIcon.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
  }

  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
             ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }


  private static void createAndShowTray() {
    String sysTraySupportMsg = "SystemTray is not supported";
    if (!SystemTray.isSupported()) {
      JOptionPane.showMessageDialog(null, sysTraySupportMsg, "Error", JOptionPane.ERROR_MESSAGE);
      Output.outputError(sysTraySupportMsg);
      return;
    }
    final PopupMenu popup = new PopupMenu();
    final SystemTray tray = SystemTray.getSystemTray();
    trayIcon.setToolTip(APP_NAME);
    MenuItem schedules = new MenuItem("Enter port");
    MenuItem exitItem = new MenuItem("Exit");

    popup.add(schedules);
    popup.addSeparator();
    popup.add(exitItem);

    trayIcon.setPopupMenu(popup);
    trayIcon.setImageAutoSize(true);
    trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {

      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
          addJobFrame.setVisible(true);
        }
      }
    });

    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      Output.outputError("TrayIcon could not be added.");
      return;
    }

    exitItem.addActionListener(e -> {
      tray.remove(trayIcon);
      System.exit(0);
    });

    schedules.addActionListener(e -> addJobFrame.setVisible(true));

  }

  private static void initView() {
    addJobFrame = new KillProcessView(APP_NAME);
    addJobFrame.setLocationRelativeTo(null);
    addJobFrame.setResizable(false);
    addJobFrame.setIconImage(CTrayIcon.getTrayIcon());
    addJobFrame.setMinimumSize(new Dimension(300, 180));
    addJobFrame.setVisible(false);
  }

  public static void toastError(String message) {
    trayIcon.displayMessage(APP_NAME, message, TrayIcon.MessageType.ERROR);
  }

  public static void toastSuccess(String message) {
    trayIcon.displayMessage(APP_NAME, message, TrayIcon.MessageType.INFO);
  }

}
