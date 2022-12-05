package com.boole.network.models;

import com.boole.Calculator;
import com.boole.Constant;
import com.boole.network.Detector;
import com.boole.network.ParamManager;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class NeuralNetwork {

    private Layer[] layers;
    private int layerCount;
    private ParamManager params;

    public NeuralNetwork(int layerCount) {
        this.layers = new Layer[layerCount];
        this.layerCount = layerCount;
    }

    public Layer[] getLayers() {
        return this.layers;
    }

    public void setLayers(Layer... layers) {
        this.layers = layers;
        this.layerCount = this.layers.length;
        for(int i=0; i<this.layerCount; i++) {
            Layer layer = this.getLayer(i);
            for(int j=0; j<layer.getNodeCount(); j++) layer.setNode(j, new Node());
        }
    }

    public Layer getLayer(int index) {
        return this.layers[index];
    }

    public void setParams() throws IOException, ParseException {
        if(this.layerCount < 1) return;
        for(int i=1; i<this.layerCount; i++) {
            Layer layer = this.layers[i];
            if(layer == null || this.layers[i-1] == null) return;
        }
        this.params = new ParamManager(this);
    }

    public void setupLayers() {
        int currentIdx = 0;
        for(int i=0; i<this.layerCount-1; i++) {
            Layer firstLayer = this.getLayer(i);
            Layer secondLayer = this.getLayer(i+1);
            for(int j=0; j<firstLayer.getNodeCount(); j++) {
                for(int k=0; k<secondLayer.getNodeCount(); k++) {
                    Node start = firstLayer.getNode(j);
                    Node end = secondLayer.getNode(k);
                    Edge edge = new Edge(start, end, this.params.getParameter(currentIdx));
                    start.addEdge(edge);
                    end.addEdge(edge);
                    currentIdx++;
                }
            }
        }
        for(int i=1; i<this.layerCount; i++) {
            Layer layer = this.getLayer(i);
            for(int j=0; j<layer.getNodeCount(); j++) {
                Node node = layer.getNode(j);
                node.setBias(this.params.getParameter(currentIdx));
                currentIdx++;
            }
        }
    }

    public int getLayerCount() {
        return layerCount;
    }

    public ParamManager getParamManager() {
        return params;
    }

    public static double calculateAverageCost() throws IOException, ParseException {
        int amt = Constant.testingData.length;
        double averageCost = 0;
        for(int i=0; i<amt; i++) {
            File data = Constant.testingData[i];
            Node[] output = Detector.detectDigit(data);
            double[] exact = new double[output.length];
            exact[Integer.parseInt(data.getName().split("_")[0])] = 1.0;
            averageCost += Calculator.calculateCost(output, exact);
        }
        averageCost /= amt;
        return averageCost;
    }

    public static double[] runTests() throws IOException, ParseException {
        int amt = Constant.testingData.length;
        double averageCost = 0;
        double successful = 0;
        for(int i=0; i<amt; i++) {
            File data = Constant.testingData[i];
            Node[] output = Detector.detectDigit(data);
            double[] exact = new double[output.length];
            int answer = Integer.parseInt(data.getName().split("_")[0]);
            int guess = Calculator.findMaximumActivation(output);
            if(answer==guess) successful++;
            exact[answer] = 1.0;
            averageCost += Calculator.calculateCost(output, exact);
        }
        averageCost /= amt;
        return new double[]{successful, amt, averageCost};
    }

    /**
     * Stochastic Gradient Descent with mini-batch samples for training.
     * For training run-time optimizations.
     */
    public static void miniBatchTraining() {
        Random rand = new Random();
        int amt = 500+rand.nextInt(101);
        for(int i=0; i<Constant.trainingData.length/500; i++) {
            for(int j=0; j<amt; j++) {

            }
        }
    }

}