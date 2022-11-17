package com.boole.drawing;

import com.boole.Constant;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DrawingBoard extends JPanel implements MouseMotionListener, MouseListener, ActionListener {

    private final ControlHandler controlHandler;
    private final JFrame window;
    private final Graphics2D graphics;
    private final BufferedImage originalImage;
    private final Graphics2D imageGraphics;
    private int lastX=Integer.MIN_VALUE,lastY;
    private final int downShift = -20;
    private final int leftShift = 6;
    private final int width = 25;

    public DrawingBoard() {
        this.controlHandler = new ControlHandler(this);

        setLayout(null);
        addMouseMotionListener(this);
        addMouseListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        this.window = new JFrame();
        this.window.setContentPane(this);
        this.window.setTitle("Digit Detector");
        this.window.getContentPane().setPreferredSize(new Dimension(Constant.drawingBoardSize, Constant.drawingBoardSize));
        this.window.getContentPane().setBackground(Color.BLACK);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.pack();
        this.window.setLocationRelativeTo(null);
        this.window.setVisible(true);
        this.window.setResizable(false);
        this.graphics = (Graphics2D) this.window.getGraphics();

        this.originalImage = new BufferedImage(Constant.drawingBoardSize, Constant.drawingBoardSize, BufferedImage.TYPE_INT_RGB);
        this.imageGraphics = this.originalImage.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        this.controlHandler.addComponents();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        int x = mouseEvent.getX()-leftShift, y = mouseEvent.getY()-downShift;
        this.graphics.setColor(Color.WHITE);
        this.graphics.fillOval(x, y, width, width);
        this.imageGraphics.setColor(Color.WHITE);
        this.imageGraphics.fillOval(x, y, width, width);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        lastX=Integer.MIN_VALUE;
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        int x = mouseEvent.getX()-leftShift, y = mouseEvent.getY()-downShift;
        this.graphics.setColor(Color.WHITE);
        this.imageGraphics.setColor(Color.WHITE);
        if(lastX!=Integer.MIN_VALUE){
            this.graphics.fillOval(lastX, lastY, width, width);
            this.graphics.setStroke(new BasicStroke(width-6));
            this.graphics.drawLine(lastX+width/2,lastY+width/2,x+width/2, y+width/2);
            this.imageGraphics.fillOval(lastX, lastY, width, width);
            this.imageGraphics.setStroke(new BasicStroke(width-6));
            this.imageGraphics.drawLine(lastX+width/2,lastY+width/2,x+width/2, y+width/2);
        }
        this.imageGraphics.fillOval(x, y, width, width);
        this.graphics.fillOval(x, y, width, width);
        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if(command.equals("submit")) {
            File image = null;
            try {
                image = renderImage();
            } catch (IOException e) { throw new RuntimeException(e); }

            // shut down window
            this.window.setVisible(false);
            this.window.dispose();

            // TODO: send rendered image to neural network which will spit back the output
        }
    }

    private File renderImage() throws IOException {
        BufferedImage image = new BufferedImage(Constant.imageSize, Constant.imageSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(this.originalImage, 0, 0, Constant.imageSize, Constant.imageSize, null);

        File drawing = new File("input/input.jpg");
        if(!drawing.exists()) drawing.createNewFile();
        ImageIO.write(image,"jpg", drawing);

        return drawing;
    }

}