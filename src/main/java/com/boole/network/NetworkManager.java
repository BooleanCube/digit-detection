package com.boole.network;

import com.boole.Calculator;
import com.boole.Constant;
import com.boole.network.models.*;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

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

    /**
     * Gradient Descent with mini-batch samples for training.
     */
    public static void miniBatchTraining(int batchSize, int sizeError) throws IOException, ParseException {
        double[] gradientVector = new double[network.getParamManager().getParamCount()];

        int totalCases = 0;
        int baseAmt = batchSize-sizeError;
        int batches = 1; //Constant.trainingData.length/(int)(baseAmt*4.5);
        for(int i=0; i<batches; i++) {
            Random rand = new Random();
            int amt = baseAmt + rand.nextInt(2*sizeError+1);
            totalCases += amt;
            for(int j=0; j<amt; j++) {
                File data = Constant.trainingData[rand.nextInt(Constant.trainingData.length)];
                Node[] output = NetworkManager.detectDigit(data);
                double[] expected = new double[output.length];
                int answer = Integer.parseInt(data.getName().split("_")[0]);
                expected[answer] = 1;

                // output to hidden layer
                Layer outputLayer = network.getLayer(network.getLayerCount() -1);
                Layer hiddenLayer = network.getLayer(network.getLayerCount() -2);
                for(int k=0; k<outputLayer.getNodeCount(); k++) {
                    Node currentNode = outputLayer.getNode(k);
                    double delta = currentNode.getActivation() - expected[k];
                    currentNode.setActivation(delta);
                    for(int l=0; l<hiddenLayer.getNodeCount(); l++) {
                        Node hiddenNode = hiddenLayer.getNode(l);
                        Edge weight = hiddenNode.getEdge(k);
                        int weightIndex = weight.getParamIndex();
                        int biasIndex = currentNode.getParamIndex();

                        double shiftWeight = -Calculator.learnRate * delta * hiddenNode.getActivation();
                        double shiftBias = -Calculator.learnRate * delta;

                        gradientVector[weightIndex] += shiftWeight;
                        gradientVector[biasIndex] += shiftBias;
                        weight.setWeight(weight.getWeight() + shiftWeight);
                        currentNode.setBias(currentNode.getBias() + shiftBias);
                    }
                }

                // hidden to hidden/input layers
                for(int k = network.getLayerCount() -2; k>0; k--) {
                    Layer prevLayer = network.getLayer(k+1);
                    Layer currentLayer = network.getLayer(k);

                    double[] result = new double[currentLayer.getNodeCount()];
                    for(int l=0; l<prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double activation = currentNode.getActivation();
                        for(int m=0; m<currentLayer.getNodeCount(); m++)
                            result[m] += currentLayer.getNode(m).getEdge(l).getWeight() * activation;
                    }
                    for(int l=0; l<result.length; l++) result[l] *= Calculator.sigmoidPrime(currentLayer.getNode(l).getActivation());

                    for(int l=0; l<prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double delta = result[l];
                        currentNode.setActivation(delta);
                        for(int m=0; m<currentLayer.getNodeCount(); m++) {
                            Node hiddenNode = currentLayer.getNode(m);
                            Edge weight = hiddenNode.getEdge(l);
                            int weightIndex = weight.getParamIndex();
                            int biasIndex = currentNode.getParamIndex();

                            double shiftWeight = -Calculator.learnRate * delta * hiddenNode.getActivation();
                            double shiftBias = -Calculator.learnRate * delta;

                            gradientVector[weightIndex] += shiftWeight;
                            gradientVector[biasIndex] += shiftBias;
                            weight.setWeight(weight.getWeight() + shiftWeight);
                            currentNode.setBias(currentNode.getBias() + shiftBias);
                        }
                    }
                }
            }
        }
        System.out.println(Arrays.toString(gradientVector));

        network.updateParameters(gradientVector);
    }

    /**
     * Stochastic Gradient Descent with mini-batch samples for training.
     * For training run-time optimizations.
     * (incomplete right now)
     */
    public static void scholasticMiniBatchTraining(int batchSize, int sizeError) throws IOException, ParseException {
        double[] gradientVector = new double[network.getParamManager().getParamCount()];

        int totalCases = 0;
        int baseAmt = batchSize-sizeError;
        int batches = Constant.trainingData.length/(int)(baseAmt*1.25);
        for(int i=0; i<batches; i++) {
            Random rand = new Random();
            int amt = baseAmt+rand.nextInt(2*sizeError+1);
            totalCases += amt;
            for(int j=0; j<amt; j++) {
                File data = Constant.trainingData[rand.nextInt(Constant.trainingData.length)];
                Node[] output = NetworkManager.detectDigit(data);
                double[] expected = new double[output.length];
                int answer = Integer.parseInt(data.getName().split("_")[0]);
                expected[answer] = 1;

                int currWeightIndex = network.getParamManager().getWeightCount()-1;
                int currBiasIndex = network.getParamManager().getParamCount()-1;
                for(int k = network.getLayerCount() -1; k>=0; k++) {
                    Layer currentLayer = network.getLayer(k);
                    if(k < network.getLayerCount() -1) {
                        Layer next = network.getLayer(k+1);

                    }
                    if(k > 0) {
                        Layer previous = network.getLayer(k-1);
                        for(int l=network.getLayer(k).getNodeCount()-1; l>=0; l--) {
                            Node current = network.getLayer(k).getNode(l);
                            double sum = 0;
                            for(Node node : network.getLayer(k-1).getNodes()) sum += node.getEdge(l).getWeight() * node.getActivation();
                            sum += current.getBias();
                            gradientVector[currBiasIndex--] += Calculator.biasPartialDiff(current.getActivation(), sum, k == network.getLayerCount() -1 ? expected[l] : network.getLayer(k+1).getNode(l).getActivation());
                        }
                    }
                    Layer next = network.getLayer(k+1);
                    Layer previous = network.getLayer(k-1);
                }


            }
        }

        for(int i=0; i<gradientVector.length; i++)
            gradientVector[i] /= totalCases;
        network.updateParameters(gradientVector);
    }

}