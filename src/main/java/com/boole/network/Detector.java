package com.boole.network;

import com.boole.Constant;
import com.boole.network.models.Layer;
import com.boole.network.models.LayerType;
import com.boole.network.models.NeuralNetwork;
import com.boole.network.models.Node;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Detector {

    public static NeuralNetwork network;

    public static void init() throws IOException, ParseException {
        // setup neural network model
        network = new NeuralNetwork(4);
        network.setLayers(
                new Layer(LayerType.BASE, new Node[Constant.imageSize*Constant.imageSize]),
                new Layer(LayerType.HIDDEN, new Node[32]),
                new Layer(LayerType.HIDDEN, new Node[16]),
                new Layer(LayerType.OUTPUT, new Node[10])
        );
        network.setParams();
        network.setupLayers();
    }

    public static Node[] detectDigit(File imageFile) throws IOException, ParseException {
        // initialize activation values for the first base layer of the neural network
        BufferedImage image = ImageIO.read(imageFile);
        Node[] activationMatrix = network.getLayer(0).getNodes();
        for(int i=0; i<activationMatrix.length; i+=Constant.imageSize) {
            for(int j=0; j<Constant.imageSize; j++) {
                Color color = new Color(image.getRGB(i/Constant.imageSize, j), true);
                int avg = (color.getRed() + color.getBlue() + color.getGreen())/3;
                activationMatrix[i+j] = new Node(avg/255.0);
            }
        }

        return network.getLayer(3).getNodes();
    }

}