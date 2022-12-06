package com.boole.network.models;

import com.boole.Calculator;
import com.boole.Constant;
import com.boole.network.NetworkManager;
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

    public double calculateAverageCost() throws IOException, ParseException {
        int amt = Constant.testingData.length;
        double averageCost = 0;
        for(int i=0; i<amt; i++) {
            File data = Constant.testingData[i];
            Node[] output = NetworkManager.detectDigit(data);
            double[] exact = new double[output.length];
            exact[Integer.parseInt(data.getName().split("_")[0])] = 1.0;
            averageCost += Calculator.calculateCost(output, exact);
        }
        averageCost /= amt;
        return averageCost;
    }

    public double[] runTests() throws IOException, ParseException {
        int amt = Constant.testingData.length;
        double averageCost = 0;
        double successful = 0;
        for(int i=0; i<amt; i++) {
            File data = Constant.testingData[i];
            Node[] output = NetworkManager.detectDigit(data);
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
    public void miniBatchTraining(int batchSize, int sizeError) throws IOException, ParseException {
        double[] gradientVector = new double[this.getParamManager().getParamCount()];

        int baseAmt = batchSize-sizeError;
        int batches = Constant.trainingData.length/(int)(baseAmt*1.25);
        for(int i=0; i<batches; i++) {
            Random rand = new Random();
            int amt = baseAmt+rand.nextInt(2*sizeError+1);
            for(int j=0; j<amt; j++) {
                File data = Constant.trainingData[rand.nextInt(Constant.trainingData.length)];
                Node[] output = NetworkManager.detectDigit(data);
                double[] expected = new double[output.length];
                int answer = Integer.parseInt(data.getName().split("_")[0]);
                expected[answer] = 1;

                int currWeightIndex = this.getParamManager().getWeightCount()-1;
                int currBiasIndex = this.getParamManager().getParamCount()-1;
                for(int k=this.layerCount-1; k>=0; k++) {
                    Layer currentLayer = this.getLayer(k);
                    if(k < this.layerCount-1) {
                        Layer next = this.getLayer(k+1);

                    }
                    if(k > 0) {
                        Layer previous = this.getLayer(k-1);
                        for(int l=this.getLayer(k).getNodeCount()-1; l>=0; l--) {
                            Node current = this.getLayer(k).getNode(l);
                            double sum = 0;
                            for(Node node : this.getLayer(k-1).getNodes()) sum += node.getEdge(l).getWeight() * node.getActivation();
                            sum += current.getBias();
                            gradientVector[currBiasIndex--] += Calculator.biasPartialDiff(current.getActivation(), sum, k == this.layerCount-1 ? expected[l] : this.getLayer(k+1).getNode(l).getActivation());
                        }
                    }
                    Layer next = this.getLayer(k+1);
                    Layer previous = this.getLayer(k-1);
                }


            }
        }
    }

}