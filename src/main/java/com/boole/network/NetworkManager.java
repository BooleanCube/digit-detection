package com.boole.network;

import com.boole.Calculator;
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

public class NetworkManager {

    private static NeuralNetwork network;

    public static NeuralNetwork getNetwork() {
        return network;
    }

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
        init();

        // initialize activation values for the first base layer of the neural network
        BufferedImage image = ImageIO.read(imageFile);
        Layer baseLayer = network.getLayer(0);
        for(int i=0; i<baseLayer.getNodeCount(); i+=Constant.imageSize) {
            for(int j=0; j<Constant.imageSize; j++) {
                Color color = new Color(image.getRGB(j, i/Constant.imageSize), true);
                double r = (255-color.getRed())*255/256.0;
                double g = (255-color.getGreen())*255/256.0;
                double b = (255-color.getBlue())*255/256.0;
                int avg = (int)((r+g+b)/3);
                baseLayer.getNode(i+j).setActivation((255-avg)/255.0);
            }
        }

        // calculate activations for all nodes in all layers
        for(int i=1; i<network.getLayerCount(); i++) {
            Layer previousLayer = network.getLayer(i-1);
            Layer currentLayer = network.getLayer(i);
            for(int j=0; j<currentLayer.getNodeCount(); j++) {
                double activation = 0;
                for(int k=0; k<previousLayer.getNodeCount(); k++) {
                    Node node = previousLayer.getNode(k);
                    activation += node.getActivation() * node.getEdge(j).getWeight();
                }
                Node node = currentLayer.getNode(j);
                activation += node.getBias();
                node.setActivation(Calculator.sigmoid(activation));
            }
        }

        return network.getLayer(3).getNodes();
    }

}