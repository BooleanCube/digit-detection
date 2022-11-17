package com.boole;

import com.boole.drawing.DrawingBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Home extends JPanel implements ActionListener {

    private final JFrame window;
    private final Graphics2D graphics;

    public static void main(String[] args) {
        new Home();
    }

    public Home() {

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
        JLabel chooseText = new JLabel("Select which action you would like to perform:");
        chooseText.setName("label1");
        chooseText.setFont(style.bigFont);
        chooseText.setForeground(style.lightText);
        chooseText.setVisible(true);
        chooseText.setBounds(80, 30, 800, 200);

        JButton trainButton = new JButton();
        trainButton.setText(centerFormatText("Train the neural network with random MNIST Database samples"));
        trainButton.setHorizontalAlignment(SwingConstants.CENTER);
        trainButton.setVerticalAlignment(SwingConstants.CENTER);
        trainButton.setFont(style.normalFont);
        trainButton.setName("train");
        trainButton.setFocusable(false);
        trainButton.addActionListener(this);
        trainButton.setMargin(new Insets(1, 1, 1, 1));
        trainButton.setBounds(100, 220, 200, 160);
        trainButton.setVisible(true);
        JButton detectButton = new JButton();
        detectButton.setText(centerFormatText("Detect your hand-written digits using the trained or untrained neural network"));
        detectButton.setHorizontalAlignment(SwingConstants.CENTER);
        detectButton.setVerticalAlignment(SwingConstants.CENTER);
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
        if (command.startsWith("train")) {
            // close this window
            this.window.setVisible(false);
            this.window.dispose();

            // create and open train window
        } else if(command.startsWith("detect")) {
            // close this window
            this.window.setVisible(false);
            this.window.dispose();

            // create and open drawing board
            DrawingBoard board = new DrawingBoard();
        }
    }

}