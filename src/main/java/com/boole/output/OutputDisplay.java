package com.boole.output;

import com.boole.Settings;
import com.boole.network.models.Node;
import com.boole.style;

import javax.swing.*;
import java.awt.*;

public class OutputDisplay extends JPanel {

    private final JFrame window;
    private final Graphics2D graphics;
    private final Node[] output;
    private String guess = "";

    public OutputDisplay(Node[] guess) {
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

        this.output = guess;
        this.formatOutput();

        this.addComponents();
        this.revalidate();
        this.repaint();
    }

    // TODO make the output display look a lot cleaner
    private void addComponents() {
        JLabel guessLabel = new JLabel(centerFormatHeader("If the guess is incorrect, <br>try training the neural network!<br><br><br>The neural network guesses:"));
        guessLabel.setName("label1");
        guessLabel.setFont(style.normalFont);
        guessLabel.setForeground(style.lightText);
        guessLabel.setVisible(true);
        guessLabel.setBounds(88, 26, 800, 300);
        JLabel outputLabel = new JLabel(newLineText(this.guess));
        outputLabel.setName("label2");
        outputLabel.setFont(style.bigFont);
        outputLabel.setForeground(style.lightText);
        outputLabel.setVisible(true);
        outputLabel.setBounds(120, 240, 800, 200);

        this.window.add(guessLabel);
        this.window.add(outputLabel);
    }

    private String centerFormatHeader(String text) {
        return "<html><h1 align=center>" + text + "</h1></html>";
    }
    private String newLineText(String text) {
        return "<html><p>" + text + "</p></html>";
    }

    private void formatOutput() {
        int length = output.length;
        for(int i=0; i<length/2; i++)
            guess += i + "(" + (int)(output[i].getActivation()*100) + "%) - ";
        guess = guess.substring(0, guess.length()-3);
        guess += "<br>";
        for(int i=length/2; i<length; i++)
            guess += i + "(" + (int)(output[i].getActivation()*100) + "%) - ";
        guess = guess.substring(0, guess.length()-3);
        System.out.println(this.guess);
    }

}