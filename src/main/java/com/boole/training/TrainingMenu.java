package com.boole.training;

import com.boole.Home;
import com.boole.network.ParamManager;
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

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        this.addComponents();
        this.revalidate();
        this.repaint();
    }

    private void addComponents() {
        JLabel chooseText = new JLabel("<html><h1 align=\"center\">Select which training action you would like <br>to perform:</h1></html>");
        chooseText.setName("label1");
        chooseText.setFont(style.bigFont);
        chooseText.setForeground(style.lightText);
        chooseText.setVisible(true);
        chooseText.setBounds(88, 26, 800, 200);

        JButton trainButton = new JButton();
        trainButton.setText(centerFormatText("Train the neural network with randomly parsed samples"));
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

        this.window.add(chooseText);
        this.window.add(trainButton);
        this.window.add(detectButton);
    }

    private String centerFormatText(String text) {
        return "<html><p align=center>" + text + "</p></html>";
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

            // close this window
            this.window.setVisible(false);
            this.window.dispose();

            try {
                ParamManager.resetTrainingData();
            } catch(IOException e) { throw new RuntimeException(e); }

            // restart application after resetting all training data
            Home home = new Home();
        } else if(command.startsWith("train")) {
            // close this window
            this.window.setVisible(false);
            this.window.dispose();

            // TODO: Run the Neural Network through all parsed training sample data
        }
    }

}