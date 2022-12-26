package com.boole.network.models;

import com.boole.math.Calculator;
import com.boole.Constant;
import com.boole.network.NetworkManager;
import com.boole.network.ParamManager;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.DoubleAdder;

public class NeuralNetwork {

    private Layer[] layers;
    private int layerCount;
    private ParamManager params;
    private final double learnRate = -0.01;

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
                    Edge edge = new Edge(start, end, currentIdx, this.params.getParameter(currentIdx));
                    start.addEdge(edge);
                    currentIdx++;
                }
            }
        }
        for(int i=1; i<this.layerCount; i++) {
            Layer layer = this.getLayer(i);
            for(int j=0; j<layer.getNodeCount(); j++) {
                Node node = layer.getNode(j);
                node.setParamIndex(currentIdx++);
                node.setBias(this.params.getParameter(node.getParamIndex()));
            }
        }
    }

    public int getLayerCount() {
        return layerCount;
    }

    public ParamManager getParamManager() {
        return params;
    }

    public double calculateAverageCost() throws IOException, ParseException {
        int amt = Constant.testingData.size();
        double averageCost = 0;
        for(int i=0; i<amt; i++) {
            double[] data = Constant.testingData.get(i);
            Node[] output = NetworkManager.feedForward(data);
            double[] exact = new double[output.length];
            exact[Constant.getSampleLabel(data)] = 1d;
            averageCost += Calculator.calculateCost(output, exact);
        }
        averageCost /= amt;
        return averageCost;
    }

    public double[] runTests() throws IOException, ParseException {
        int amt = 1000;
        double averageCost = 0;
        double successful = 0;
        for(int i=0; i<amt; i++) {
            double[] data = Constant.testingData.get(i*9);
            Node[] output = NetworkManager.feedForward(data);
            double[] exact = new double[output.length];
            int label = Constant.getSampleLabel(data);
            int guess = Calculator.findMaximumActivation(output);
            if(label == guess) successful++;
            exact[label] = 1.0;
            averageCost += Calculator.calculateCost(output, exact);
        }
        averageCost /= amt;
        return new double[]{successful, amt, averageCost};
    }

    public void updateParameters(double[] gradient) {
        ParamManager params = this.params;
        for(int i=0; i<gradient.length; i++)
            params.getParameters()[i] += gradient[i];
    }

    public void updateParameters(double[] gradient, int size) {
        ParamManager params = this.params;
        for(int i=0; i<gradient.length; i++)
            params.getParameters()[i] += gradient[i]/size;
    }

    public void updateParameters(DoubleAdder[] gradient, int size) {
        ParamManager params = this.params;
        for(int i=0; i<gradient.length; i++)
            params.getParameters()[i] += gradient[i].doubleValue()/size;
    }

    public Node[] getInputNodes() {
        return this.getLayer(0).getNodes();
    }

    public int getInputCount() {
        return this.getInputNodes().length;
    }

    public Node[] getOutputNodes() {
        return this.getLayer(this.getLayerCount()-1).getNodes();
    }

    public int getOutputCount() {
        return this.getOutputNodes().length;
    }

}