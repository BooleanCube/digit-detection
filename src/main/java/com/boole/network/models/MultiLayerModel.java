package com.boole.network.models;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;

public class MultiLayerModel {

    public MultiLayerConfiguration config;
    public MultiLayerNetwork model;
    public MnistDataSetIterator mnistTrain;

    public void init() {
        // Define the input and output sizes
        int inputSize = 784; // 28 x 28 pixels
        int outputSize = 10; // 10 digits

        // Define the network configuration
        this.config = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam.Builder().learningRate(0.01d).build())
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(inputSize)
                        .nOut(100)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new DenseLayer.Builder()
                        .nIn(100)
                        .nOut(50)
                        .activation(Activation.RELU)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(50)
                        .nOut(outputSize)
                        .activation(Activation.SOFTMAX)
                        .build())
                .build();

        // Create the neural network model
        this.model = new MultiLayerNetwork(config);
        model.init();

        // Load the MNIST dataset
        try {
            this.mnistTrain = new MnistDataSetIterator(64, true, 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void trainNetwork() {
        model.setListeners(new ScoreIterationListener(10)); // Print the score every 10 iterations
        for (int i = 0; i < 10; i++) {
            while (mnistTrain.hasNext()) {
                DataSet dataSet = mnistTrain.next();
                model.fit(dataSet);
            }
            mnistTrain.reset();
        }

        // Evaluate the model on some test data
        MnistDataSetIterator mnistTest = null;
        try {
            mnistTest = new MnistDataSetIterator(64, false, 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Accuracy: " + model.evaluate(mnistTest).accuracy());
    }

    public String testNetwork() {
        // Evaluate the model on some test data
        MnistDataSetIterator mnistTest = null;
        try {
            mnistTest = new MnistDataSetIterator(64, false, 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return model.evaluate(mnistTest).stats();
    }

}
