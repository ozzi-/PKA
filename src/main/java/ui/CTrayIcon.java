package main.java.ui;

import java.awt.Image;
import java.awt.TrayIcon;
import java.net.URL;

import javax.swing.ImageIcon;

import main.java.util.Output;


public class CTrayIcon extends TrayIcon {

  public CTrayIcon(Image image) {
    super(image);
  }

  public static Image getTrayIcon() {
    URL imageURL = CTrayIcon.class.getClassLoader().getResource("images/icon.gif");

    if (imageURL == null) {
      Output.outputError("Resource not found: " + "images/icon.gif");
      return null;
    } else {
      return (new ImageIcon(imageURL, "tray icon")).getImage();
    }
  }

}
