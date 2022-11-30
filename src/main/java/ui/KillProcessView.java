package main.java.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import main.java.pka.PKA;
import main.java.util.CLIUtil;
import main.java.util.Output;


public class KillProcessView extends JFrame {

  private JLabel portLabel; // NOSONAR
  private JTextField portField;
  private JPanel mainPanel;
  private JButton killPortBtn;
  private final Pattern pattern = Pattern.compile("^[0-9]*$", Pattern.CASE_INSENSITIVE);

  public KillProcessView(String title) {
    super(title);
    Output.output("MainPanel: " + mainPanel);
    this.setContentPane(mainPanel);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.pack();
    killPortBtn.setEnabled(false);
    initPortListener();
    initKillAction();
  }

  private void initKillAction() {
    killPortBtn.addActionListener(e -> {
      if (portField.getText().isEmpty()) {
        portField.setBackground(Color.RED);
      } else {
        findAndKill();
        resetView();
        this.dispose();
      }
    });
  }

  private void findAndKill() {
    try {
      Optional<Integer> pid = CLIUtil.runNetStat(portField.getText());
      if (pid.isEmpty()) {
        PKA.toastError("Could not find any application listening on port " + portField.getText());
      } else {
        Optional<String> killError = CLIUtil.killProcess(pid.get());
        Optional<Integer> pidRetry = CLIUtil.runNetStat(portField.getText());
        if (killError.isEmpty()) {
          if (pidRetry.isPresent()) {
            PKA.toastError(
                "Could not kill application with pid " + pidRetry.get() + " - netstat says its still running :(");
          } else {
            PKA.toastSuccess("Killed application!");
          }
        } else {
          PKA.toastError("Could not kill application: " + killError.get());
        }
      }
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

  private void initPortListener() {
    portField.addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {
        // ignored
      }

      @Override
      public void keyPressed(KeyEvent e) {
        // ignored
      }

      @Override
      public void keyReleased(KeyEvent e) {
        String portText = portField.getText().trim();
        Matcher matcher = pattern.matcher(portText);
        boolean isNumber = matcher.find();
        if (portText.isEmpty() || !isNumber || portText.length() > 5) {
          portField.setBackground(Color.ORANGE);
          killPortBtn.setEnabled(false);
        } else {
          portField.setBackground(Color.white);
          killPortBtn.setEnabled(true);
        }
      }
    });
  }

  public void resetView() {
    this.portField.setText("");
    this.portField.setBackground(Color.WHITE);
  }
}
