package com.boole.training;

import com.boole.Home;
import com.boole.Settings;
import com.boole.network.NetworkManager;
import com.boole.style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class TrainingMenu extends JPanel implements ActionListener {

    private final JFrame window;
    private final Graphics2D graphics;

    public TrainingMenu() {
        setLayout(null);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        this.window = new JFrame();
        this.window.setContentPane(this);
        this.window.setTitle("Digit Detector");
        this.window.getContentPane().setPreferredSize(new Dimension(720, 480));
        this.window.getContentPane().setBackground(Color.BLACK);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.pack();
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
        this.window.setResizable(false);
        this.graphics = (Graphics2D) this.window.getGraphics();

        Settings.setGraphicsRendering(this.graphics);

        this.addComponents();
        this.revalidate();
        this.repaint();
    }

    private void addComponents() {
        JLabel chooseText = new JLabel(centerFormatHeader("Select which training action you would like <br>to perform:"));
        chooseText.setName("label1");
        chooseText.setFont(style.bigFont);
        chooseText.setForeground(style.lightText);
        chooseText.setVisible(true);
        chooseText.setBounds(88, 26, 800, 200);

        JButton trainButton = new JButton();
        trainButton.setText(centerFormatText("Train the neural network with randomly generated mini-batches (500-600 samples)"));
        trainButton.setFont(style.normalFont);
        trainButton.setName("train");
        trainButton.setFocusable(false);
        trainButton.addActionListener(this);
        trainButton.setMargin(new Insets(1, 1, 1, 1));
        trainButton.setBounds(100, 220, 200, 160);
        trainButton.setVisible(true);
        JButton detectButton = new JButton();
        detectButton.setText(centerFormatText("Reset all training progress achieved until this point"));
        detectButton.setFont(style.normalFont);
        detectButton.setName("detect");
        detectButton.setFocusable(false);
        detectButton.addActionListener(this);
        detectButton.setMargin(new Insets(1, 1, 1, 1));
        detectButton.setBounds(400, 220, 200, 160);
        detectButton.setVisible(true);
        JButton homeButton = new JButton();
        homeButton.setText(centerFormatText("Home"));
        homeButton.setName("home");
        homeButton.setFocusable(false);
        homeButton.addActionListener(this);
        homeButton.setMargin(new Insets(1, 1, 1, 1));
        homeButton.setBounds(650, 460, 70, 20);
        homeButton.setVisible(true);

        this.window.add(chooseText);
        this.window.add(trainButton);
        this.window.add(detectButton);
        this.window.add(homeButton);
    }

    private String centerFormatText(String text) {
        return "<html><p align=center>" + text + "</p></html>";
    }
    private String centerFormatHeader(String text) {
        return "<html><h1 align=center>" + text + "</h1></html>";
    }

    private String stripHTMLTags(String html) {
        return html.substring("<html><p align=center>".length(), html.indexOf("</p></html>")).toLowerCase();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = stripHTMLTags(actionEvent.getActionCommand());
        if (command.startsWith("reset")) {
            int response = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to lose all training progress?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if(response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) return;

            this.closeWindow();

            try {
                NetworkManager.getNetwork().getParamManager().resetTrainingData();
            } catch(IOException e) { throw new RuntimeException(e); }

            // restart application after resetting all training data
            Home home = new Home();
        } else if(command.startsWith("train")) {
            this.closeWindow();

            // TODO: Run the Neural Network through all parsed training sample data
        } else if(command.equals("home")) {
            this.closeWindow();

            Home home = new Home();
        }
    }

    private void closeWindow() {
        // close this window
        this.window.setVisible(false);
        this.window.dispose();
    }

}