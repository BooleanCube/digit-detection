package com.boole;

import com.boole.board.DrawingBoard;
import com.boole.network.Detector;
import com.boole.network.ParamManager;
import com.boole.training.TrainingMenu;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Home extends JPanel implements ActionListener {

    private final JFrame window;
    private final Graphics2D graphics;

    public static void main(String[] args) {
        try {
            Detector.init();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
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

        Settings.setGraphicsRendering(this.graphics);

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
        trainButton.setFont(style.normalFont);
        trainButton.setName("train");
        trainButton.setFocusable(false);
        trainButton.addActionListener(this);
        trainButton.setMargin(new Insets(1, 1, 1, 1));
        trainButton.setBounds(60, 220, 180, 160);
        trainButton.setVisible(true);
        JButton statsButton = new JButton();
        statsButton.setText(centerFormatText("Run tests to measure the statistics of the currently-trained neural network"));
        statsButton.setFont(style.normalFont);
        statsButton.setName("train");
        statsButton.setFocusable(false);
        statsButton.addActionListener(this);
        statsButton.setMargin(new Insets(1, 1, 1, 1));
        statsButton.setBounds(260, 220, 180, 160);
        statsButton.setVisible(true);
        JButton detectButton = new JButton();
        detectButton.setText(centerFormatText("Detect your hand-written digits using the trained or untrained neural network"));
        detectButton.setFont(style.normalFont);
        detectButton.setName("detect");
        detectButton.setFocusable(false);
        detectButton.addActionListener(this);
        detectButton.setMargin(new Insets(1, 1, 1, 1));
        detectButton.setBounds(460, 220, 180, 160);
        detectButton.setVisible(true);

        this.window.add(chooseText);
        this.window.add(trainButton);
        this.window.add(statsButton);
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
        if(command.startsWith("train")) {
            closeWindow(this);

            // create and open training menu for more selections
            TrainingMenu menu = new TrainingMenu();
        } else if(command.startsWith("detect")) {
            closeWindow(this);

            // create and open drawing board
            DrawingBoard board = new DrawingBoard();
        } else if(command.startsWith("run")) {
            closeWindow(this);

            // TODO start running the tests and open stats screen
        }
    }

    private static void closeWindow(Home home) {
        // close this window
        home.window.setVisible(false);
        home.window.dispose();
    }

}