package com.boole.detector;

import com.boole.Settings;
import com.boole.style;

import javax.swing.*;
import java.awt.*;

public class OutputDisplay extends JPanel {

    private final JFrame window;
    private final Graphics2D graphics;

    public OutputDisplay() {
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
        JLabel guessLabel = new JLabel(centerFormatHeader("My detector guesses:"));
        guessLabel.setName("label1");
        guessLabel.setFont(style.normalFont);
        guessLabel.setForeground(style.lightText);
        guessLabel.setVisible(true);
        guessLabel.setBounds(88, 26, 800, 200);
        JLabel outputLabel = new JLabel(Integer.toString(0)); // TODO : add the answer instead of just 0
        outputLabel.setName("label2");
        outputLabel.setFont(style.hugeFont);
        outputLabel.setForeground(style.lightText);
        outputLabel.setVisible(true);
        outputLabel.setBounds(88, 26, 800, 200); // TODO : fix positioning later

        this.window.add(guessLabel);
        this.window.add(outputLabel);
    }
    private String centerFormatHeader(String text) {
        return "<html><h1 align=center>" + text + "</h1></html>";
    }

}