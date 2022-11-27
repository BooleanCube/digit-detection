package com.boole.board;

import javax.swing.*;
import java.awt.*;

public class ControlHandler {

    private final DrawingBoard board;
    private final JButton submitButton;

    public ControlHandler(DrawingBoard board) {
        this.board = board;

        this.submitButton = new JButton();
        this.submitButton.setText("submit");
        this.submitButton.setName("submit");
        this.submitButton.setFocusable(false);
        this.submitButton.addActionListener(board);
        this.submitButton.setMargin(new Insets(1, 1, 1, 1));
        this.submitButton.setBounds(210, 260, 70, 20);
        this.submitButton.setVisible(true);
    }

    public JButton getSubmitButton() {
        return this.submitButton;
    }

    public void addComponents() {
        this.board.add(this.submitButton);
    }

}
