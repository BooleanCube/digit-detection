package com.boole.statistics;

import com.boole.Home;
import com.boole.Settings;
import com.boole.network.models.Node;
import com.boole.style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestingDisplay extends JPanel implements ActionListener {

    private final JFrame window;
    private final Graphics2D graphics;
    private double successCount;
    private double totalCount;
    private double successRate;
    private double networkCost;

    public TestingDisplay(double[] results) {
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

        this.successCount = results[0];
        this.totalCount = results[1];
        this.successRate = this.successCount/this.totalCount*100;
        this.networkCost = results[2];

        this.addComponents();
        this.revalidate();
        this.repaint();
    }

    // TODO make the output display look a lot cleaner
    private void addComponents() {
        JLabel successRate = new JLabel(centerFormatHeader("The success rate of the neural network is: " + (int)this.successCount + "/" + (int)this.totalCount + " (" + this.successRate + "%)"));
        successRate.setName("label1");
        successRate.setFont(style.bigFont);
        successRate.setForeground(style.lightText);
        successRate.setVisible(true);
        successRate.setBounds(150, 40, 400, 200);
        JLabel networkCost = new JLabel(centerFormatHeader("The average cost of the network is: " + this.networkCost));
        networkCost.setName("label2");
        networkCost.setFont(style.bigFont);
        networkCost.setForeground(style.lightText);
        networkCost.setVisible(true);
        networkCost.setBounds(150, 210, 400, 200);

        JButton homeButton = new JButton();
        homeButton.setText("Home");
        homeButton.setName("home");
        homeButton.setFocusable(false);
        homeButton.addActionListener(this);
        homeButton.setMargin(new Insets(1, 1, 1, 1));
        homeButton.setBounds(650, 460, 70, 20);
        homeButton.setVisible(true);

        this.window.add(successRate);
        this.window.add(networkCost);
        this.window.add(homeButton);
    }

    private String centerFormatHeader(String text) {
        return "<html><h1 align=center>" + text + "</h1></html>";
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand().toLowerCase();
        if(command.equals("home")) {
            // shut down window
            this.window.setVisible(false);
            this.window.dispose();

            Home home = new Home();
        }
    }

}