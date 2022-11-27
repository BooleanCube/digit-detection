package com.boole.network;

import com.boole.Constant;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Detector {

    public static Node[] detectDigit(File imageFile) throws IOException {
        NeuralNetwork network = new NeuralNetwork(4);
        network.setLayers(
                new Layer(LayerType.BASE, new Node[724]),
                new Layer(LayerType.HIDDEN, new Node[16]),
                new Layer(LayerType.HIDDEN, new Node[16]),
                new Layer(LayerType.OUTPUT, new Node[10])
        );

        BufferedImage image = ImageIO.read(imageFile);
        double[][] activationMatrix = new double[Constant.imageSize][Constant.imageSize];

        for(int i=0; i<activationMatrix.length; i++) {
            for(int j=0; j<activationMatrix[0].length; j++) {
                Color color = new Color(image.getRGB(i, j), true);
                int avg = (color.getRed() + color.getBlue() + color.getGreen())/3;
                activationMatrix[i][j] = avg/255.0;
            }
        }

        return network.getLayer(3).getNodes();
    }

}