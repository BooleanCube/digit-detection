package com.boole.network;

import com.boole.math.Calculator;
import com.boole.Constant;
import com.boole.network.models.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.DoubleAdder;

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

    /**
     * Feeds forward the input data through the network, returning the output of the final layer.
     *
     * @param data an array of input data
     * @return an array of output values from the final layer of the network
     */
    public static Node[] feedForward(double[] data) {

        if(data.length != network.getInputCount()+1) {
            throw new IllegalArgumentException("The input data must have the same number of elements as the input layer of the network.");
        }

        double[] parameters = network.getParamManager().getParameters();

        Node[] input = network.getInputNodes();
        for(int i=0; i<data.length-1; i++) {
            input[i].setActivation(data[i]);
        }

        for(int i=1; i<network.getLayerCount(); i++) {
            Layer prevLayer = network.getLayer(i-1);
            Layer currentLayer = network.getLayer(i);

            for(int j=0; j<currentLayer.getNodeCount(); j++) {
                Node currentNode = currentLayer.getNode(j);
                currentNode.setBias(parameters[currentNode.getParamIndex()]);
                double sum = 0;
                for(int k=0; k<prevLayer.getNodeCount(); k++) {
                    Node prevNode = prevLayer.getNode(k);
                    Edge weight = prevNode.getEdge(j);
                    weight.setWeight(parameters[weight.getParamIndex()]);
                    sum += weight.getWeight() * prevNode.getActivation();
                }
                currentNode.setActivation(Calculator.sigmoid(sum + currentNode.getBias()));
            }
        }

        return network.getOutputNodes();
    }

    public static Node[] forwardPropagation(double[] data) throws IOException, ParseException {
        // reset the neural network before implementing forward propagation
        init();

        // initialize activation values for the first base layer of the neural network
        Layer baseLayer = network.getLayer(0);
        for(int i=0; i<baseLayer.getNodeCount(); i++)
            baseLayer.getNode(i).setActivation(data[i]);

        // calculate activations for all nodes in all layers
        for(int i=1; i<network.getLayerCount(); i++) {
            Layer previousLayer = network.getLayer(i-1);
            Layer currentLayer = network.getLayer(i);
            for(int j=0; j<currentLayer.getNodeCount(); j++) {
                double activation = 0;
                for(int k=0; k<previousLayer.getNodeCount(); k++) {
                    Node node = previousLayer.getNode(k);
                    activation += node.getActivation() * (node.getEdge(j).getWeight());
                }
                Node node = currentLayer.getNode(j);
                activation += node.getBias();
                node.setActivation(Calculator.sigmoid(activation));
            }
        }

        return network.getLayer(3).getNodes();
    }

    /**
     * Trains a neural network using the mini-batch training method and the gradient descent algorithm.
     *
     * @param batchSize an int representing the size of each mini-batch
     * @param epochs an int representing the number of epochs to train the network for
     * @param learningRate a double representing the learning rate to use for weight updates
     */
    public static void parallelBatchTraining(int batchSize, int epochs, double learningRate) throws IOException, ParseException {
        double startTime = System.currentTimeMillis();
        for(int epoch=0; epoch<epochs; epoch++) {
            // Shuffle the training data
            Collections.shuffle(Constant.trainingData);

            // Calculate the gradients for the batch
            DoubleAdder[] gradients = new DoubleAdder[network.getParamManager().getParamCount()];
            for(int i=0; i<gradients.length; i++) gradients[i] = new DoubleAdder();

            for(int i=0; i<Constant.trainingData.size(); i+=batchSize) {
                int end = Math.min(i + batchSize, Constant.trainingData.size());
                int currentBatchSize = end-i;
                List<double[]> batch = Constant.trainingData.subList(i, end);

                // Use parallel stream to process mini-batches in parallel
                batch.parallelStream().forEach(data -> {
                    // Feed forward through the network
                    Node[] output = feedForward(data);

                    // Calculate the target values for the output layer
                    double[] target = new double[output.length];
                    target[Constant.getSampleLabel(data)] = 1d;

                    // Calculate the gradients for the output layer
                    Layer outputLayer = network.getLayer(network.getLayerCount() - 1);
                    for(int k=0; k<outputLayer.getNodeCount(); k++) {
                        Node currentNode = outputLayer.getNode(k);
                        double delta = currentNode.getActivation() - target[k];
                        currentNode.setActivation(delta);
                    }

                    // Calculate the gradients for the hidden layers
                    for(int k=network.getLayerCount()-2; k>=0; k--) {
                        Layer prevLayer = network.getLayer(k + 1);
                        Layer currentLayer = network.getLayer(k);

                        // Calculate the result values for the current layer
                        double[] result = new double[currentLayer.getNodeCount()];
                        for(int l=0; l<prevLayer.getNodeCount(); l++) {
                            Node currentNode = prevLayer.getNode(l);
                            double activation = currentNode.getActivation();
                            for(int m=0; m<currentLayer.getNodeCount(); m++) {
                                Edge weight = currentLayer.getNode(m).getEdge(l);
                                result[m] += weight.getWeight() * activation;
                            }
                        }
                        for(int l=0; l<result.length; l++) {
                            // Calculate the derivative of the activation function applied to the activation of the current node
                            result[l] *= Calculator.sigmoidPrime(currentLayer.getNode(l).getActivation());
                        }

                        // Update the gradients for the biases and weights
                        for(int l=0; l<prevLayer.getNodeCount(); l++) {
                            Node currentNode = prevLayer.getNode(l);
                            double delta = result[l];
                            currentNode.setActivation(delta);
                            int biasIndex = currentNode.getParamIndex();
                            gradients[biasIndex].add(-learningRate * delta);
                            for(int m=0; m<currentLayer.getNodeCount(); m++) {
                                Node hiddenNode = currentLayer.getNode(m);
                                Edge weight = hiddenNode.getEdge(l);
                                int weightIndex = weight.getParamIndex();
                                gradients[weightIndex].add(-learningRate * delta * hiddenNode.getActivation());
                            }
                        }
                    }
                });

                System.out.println(Arrays.toString(gradients));

                // Update the biases and weights using the gradients
                network.updateParameters(gradients, currentBatchSize);
            }
        }

        network.getParamManager().updateTrainingData();

        double endTime = System.currentTimeMillis();
        System.out.println("Gradient Descent Training Runtime: " + (endTime - startTime) / 1000.0 + " seconds");
    }

    /**
     * Trains a neural network using the parallel training method and the gradient descent algorithm.
     *
     * @param epochs an int representing the number of epochs to train the network for
     * @param learningRate a double representing the learning rate to use for weight updates
     */
    public static void parallelDataTraining(int epochs, double learningRate) throws IOException, ParseException {
        double startTime = System.currentTimeMillis();
        for (int epoch = 0; epoch < epochs; epoch++) {
            // Shuffle the training data
            Collections.shuffle(Constant.trainingData);

            // Use parallel stream to process mini-batches in parallel
            Constant.trainingData.parallelStream().forEach(data -> {
                // Feed forward through the network
                Node[] output = feedForward(data);

                // Calculate the target values for the output layer
                double[] target = new double[output.length];
                target[Constant.getSampleLabel(data)] = 1d;

                // Calculate the gradients for the output layer
                Layer outputLayer = network.getLayer(network.getLayerCount() - 1);
                for (int k = 0; k < outputLayer.getNodeCount(); k++) {
                    Node currentNode = outputLayer.getNode(k);
                    double delta = currentNode.getActivation() - target[k];
                    currentNode.setActivation(delta);
                }

                // Calculate the gradients for the hidden layers
                for (int k = network.getLayerCount() - 2; k >= 0; k--) {
                    Layer prevLayer = network.getLayer(k + 1);
                    Layer currentLayer = network.getLayer(k);

                    // Calculate the result values for the current layer
                    double[] result = new double[currentLayer.getNodeCount()];
                    for (int l = 0; l < prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double activation = currentNode.getActivation();
                        for (int m = 0; m < currentLayer.getNodeCount(); m++) {
                            Edge weight = currentLayer.getNode(m).getEdge(l);
                            result[m] += weight.getWeight() * activation;
                        }
                    }
                    for (int l = 0; l < result.length; l++) {
                        // Calculate the derivative of the activation function applied to the activation of the current node
                        result[l] *= Calculator.sigmoidPrime(currentLayer.getNode(l).getActivation());
                    }

                    // Update the gradients for the biases and weights
                    for (int l = 0; l < prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double delta = result[l];
                        currentNode.setActivation(delta);
                        int biasIndex = currentNode.getParamIndex();
                        network.getParamManager().updateParam(biasIndex, -learningRate * delta);
                        for (int m = 0; m < currentLayer.getNodeCount(); m++) {
                            Node hiddenNode = currentLayer.getNode(m);
                            int weightIndex = hiddenNode.getEdge(l).getParamIndex();
                            double gradient = delta * hiddenNode.getActivation();
                            network.getParamManager().updateParam(weightIndex, -learningRate * gradient);
                        }
                    }
                }
            });
        }

        // Update parameter database from the training data
        network.getParamManager().updateTrainingData();

        double endTime = System.currentTimeMillis();
        System.out.println("Training time: " + (endTime - startTime) / 1000 + "s");
    }

    /**
     * Trains a neural network using the mini-batch training method and the gradient descent algorithm.
     *
     * @param batchSize an int representing the size of each mini-batch
     * @param epochs an int representing the number of epochs to train the network for
     * @param learningRate a double representing the learning rate to use for weight updates
     */
    public static void miniBatchTrainingOptimized(int batchSize, int epochs, double learningRate) throws IOException, ParseException {
        double startTime = System.currentTimeMillis();
        for (int epoch = 0; epoch < epochs; epoch++) {
            // Shuffle the training data
            Collections.shuffle(Constant.trainingData);

            for (int i = 0; i < Constant.trainingData.size(); i += batchSize) {
                int end = Math.min(i + batchSize, Constant.trainingData.size());
                int currentBatchSize = end - i;

                // Calculate the gradients for the batch
                double[] gradients = new double[network.getParamManager().getParamCount()];
                for (int j = 0; j < currentBatchSize; j++) {
                    // Feed forward through the network
                    double[] data = Constant.trainingData.get(i + j);
                    Node[] output = feedForward(data);
                    double[] target = new double[output.length];
                    target[Constant.getSampleLabel(data)] = 1d;

                    // Calculate the error for the output layer
                    Layer outputLayer = network.getLayer(network.getLayerCount() - 1);
                    for (int k = 0; k < outputLayer.getNodeCount(); k++) {
                        Node currentNode = outputLayer.getNode(k);
                        double delta = currentNode.getActivation() - target[k];
                        currentNode.setActivation(delta);
                    }

                    // Calculate the gradients for the hidden layers
                    for (int k = network.getLayerCount() - 2; k >= 0; k--) {
                        Layer prevLayer = network.getLayer(k + 1);
                        Layer currentLayer = network.getLayer(k);

                        double[] result = new double[currentLayer.getNodeCount()];
                        for (int l = 0; l < prevLayer.getNodeCount(); l++) {
                            Node currentNode = prevLayer.getNode(l);
                            double activation = currentNode.getActivation();
                            for (int m = 0; m < currentLayer.getNodeCount(); m++) {
                                Edge weight = currentLayer.getNode(m).getEdge(l);
                                result[m] += weight.getWeight() * activation;
                            }
                        }
                        for (int l = 0; l < result.length; l++) {
                            // Calculate the derivative of the activation function applied to the activation of the current node
                            result[l] *= Calculator.sigmoidPrime(currentLayer.getNode(l).getActivation());
                        }

                        // Update the gradients for the biases and weights
                        for (int l = 0; l < prevLayer.getNodeCount(); l++) {
                            Node currentNode = prevLayer.getNode(l);
                            double delta = result[l];
                            currentNode.setActivation(delta);
                            int biasIndex = currentNode.getParamIndex();
                            gradients[biasIndex] += -learningRate * delta;
                            for (int m = 0; m < currentLayer.getNodeCount(); m++) {
                                Node hiddenNode = currentLayer.getNode(m);
                                Edge weight = hiddenNode.getEdge(l);
                                int weightIndex = weight.getParamIndex();
                                gradients[weightIndex] += -learningRate * delta * hiddenNode.getActivation();
                            }
                        }
                    }

                    // Update the weights and biases using the gradients for the batch
                    for(int k=0; k<gradients.length; k++) {
                        double gradient = gradients[j] / currentBatchSize;
                        network.getParamManager().updateParam(j, -learningRate * gradient);
                    }
                }
            }

            // Update parameter database from the training data
            network.getParamManager().updateTrainingData();

            // Calculate the elapsed time
            double elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Epoch " + epoch + " completed in " + elapsedTime + " milliseconds");
        }
    }

    /**
     * Trains a neural network using the stochastic gradient descent algorithm.
     *
     * @param epochs an int representing the number of epochs to train the network for
     * @param learningRate a double representing the learning rate to use for weight updates
     */
    public static void stochasticGradientDescent(int epochs, double learningRate) throws IOException, ParseException {
        double startTime = System.currentTimeMillis();
        for(int epoch=0; epoch<epochs; epoch++) {
            // Shuffle the training data
            Collections.shuffle(Constant.trainingData);

            for(int i=0; i<Constant.trainingData.size(); i++) {
                // Get the current training sample
                double[] data = Constant.trainingData.get(i);
                Node[] output = feedForward(data);
                double[] target = new double[output.length];
                target[Constant.getSampleLabel(data)] = 1d;

                // Calculate the gradients for the sample
                double[] gradients = new double[network.getParamManager().getParamCount()];
                // Calculate the error for the output layer
                Layer outputLayer = network.getLayer(network.getLayerCount() - 1);
                for(int k=0; k<outputLayer.getNodeCount(); k++) {
                    Node currentNode = outputLayer.getNode(k);
                    double delta = currentNode.getActivation() - target[k];
                    currentNode.setActivation(delta);
                }
                // Calculate the gradients for the hidden layers
                for(int k=network.getLayerCount()-2; k>=0; k--) {
                    Layer prevLayer = network.getLayer(k + 1);
                    Layer currentLayer = network.getLayer(k);

                    double[] result = new double[currentLayer.getNodeCount()];
                    for(int l=0; l<prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double activation = currentNode.getActivation();
                        for(int m=0; m<currentLayer.getNodeCount(); m++) {
                            Edge weight = currentLayer.getNode(m).getEdge(l);
                            result[m] += weight.getWeight() * activation;
                        }
                    }
                    for(int l=0; l<result.length; l++) {
                        // Calculate the derivative of the activation function applied to the activation of the current node
                        result[l] *= Calculator.sigmoidPrime(currentLayer.getNode(l).getActivation());
                    }

                    // Update the gradients for the biases and weights
                    for(int l=0; l<prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double delta = result[l];
                        currentNode.setActivation(delta);
                        int biasIndex = currentNode.getParamIndex();
                        gradients[biasIndex] += -learningRate * delta;
                        for (int m = 0; m < currentLayer.getNodeCount(); m++) {
                            Node hiddenNode = currentLayer.getNode(m);
                            Edge weight = hiddenNode.getEdge(l);
                            int weightIndex = weight.getParamIndex();
                            gradients[weightIndex] += -learningRate * delta * hiddenNode.getActivation();
                        }
                    }
                }

                // Update the weights and biases using the gradients
                network.updateParameters(gradients);
            }

            // Update parameter database from the training data
            network.getParamManager().updateTrainingData();

            double endTime = System.currentTimeMillis();
            System.out.println("Time taken for epoch " + (epoch + 1) + ": " + (endTime - startTime)/1000.0 + " seconds");
            startTime = endTime;
        }
    }

    /**
     * Trains a neural network using the mini-batch training method and the gradient descent algorithm.
     *
     * @param batchSize an int representing the size of each mini-batch
     * @param epochs an int representing the number of epochs to train the network for
     * @param learningRate a double representing the learning rate to use for weight updates
     */
    public static void miniBatchTraining(int batchSize, int epochs, double learningRate) throws IOException, ParseException {
        double startTime = System.currentTimeMillis();
        for(int epoch=0; epoch<epochs; epoch++) {
            // Shuffle the training data
            Collections.shuffle(Constant.trainingData);

            for(int i=0; i<Constant.trainingData.size(); i+=batchSize) {
                int end = Math.min(i + batchSize, Constant.trainingData.size());
                int currentBatchSize = end - i;

                // Calculate the gradients for the batch
                double[] gradients = new double[network.getParamManager().getParamCount()];
                for(int j=0; j<currentBatchSize; j++) {
                    // Feed forward through the network
                    double[] data = Constant.trainingData.get(i+j);
                    Node[] output = feedForward(data);
                    double[] target = new double[output.length];
                    target[Constant.getSampleLabel(data)] = 1d;

                    // Calculate the error for the output layer
                    Layer outputLayer = network.getLayer(network.getLayerCount() - 1);
                    for(int k=0; k<outputLayer.getNodeCount(); k++) {
                        Node currentNode = outputLayer.getNode(k);
                        double delta = currentNode.getActivation() - target[k];
                        currentNode.setActivation(delta);
                    }

                    // Calculate the gradients for the hidden layers
                    for(int k=network.getLayerCount()-2; k>=0; k--) {
                        Layer prevLayer = network.getLayer(k + 1);
                        Layer currentLayer = network.getLayer(k);

                        double[] result = new double[currentLayer.getNodeCount()];
                        for(int l=0; l<prevLayer.getNodeCount(); l++) {
                            Node currentNode = prevLayer.getNode(l);
                            double activation = currentNode.getActivation();
                            for(int m=0; m<currentLayer.getNodeCount(); m++) {
                                Edge weight = currentLayer.getNode(m).getEdge(l);
                                result[m] += weight.getWeight() * activation;
                            }
                        }
                        for(int l=0; l<result.length; l++) {
                            // Calculate the derivative of the activation function applied to the activation of the current node
                            result[l] *= Calculator.sigmoidPrime(currentLayer.getNode(l).getActivation());
                        }

                        // Update the gradients for the biases and weights
                        for(int l=0; l<prevLayer.getNodeCount(); l++) {
                            Node currentNode = prevLayer.getNode(l);
                            double delta = result[l];
                            currentNode.setActivation(delta);
                            int biasIndex = currentNode.getParamIndex();
                            gradients[biasIndex] += -learningRate * delta;
                            for(int m=0; m<currentLayer.getNodeCount(); m++) {
                                Node hiddenNode = currentLayer.getNode(m);
                                Edge weight = hiddenNode.getEdge(l);
                                int weightIndex = weight.getParamIndex();
                                gradients[weightIndex] += -learningRate * delta * hiddenNode.getActivation();
                            }
                        }
                    }
                }

                // Update the biases and weights using the gradients
                network.updateParameters(gradients);
            }
        }

        // Update parameter database from the training data
        network.getParamManager().updateTrainingData();

        double endTime = System.currentTimeMillis();
        System.out.println("Gradient Descent Mini-Batch Training Runtime: " + (endTime - startTime) / 1000.0 + " seconds");
    }

    /**
     * Gradient Descent with mini-batch samples for training.
     */
    public static void miniBatchTraining(int batchSize) throws IOException, ParseException {
        double[] gradientVector = new double[network.getParamManager().getParamCount()];

        int epochs = 1; //Constant.trainingData.length/(int)(baseAmt*4.5);
        for(int epoch=0; epoch<epochs; epoch++) {
            Random rand = new Random();
            for(int j=0; j<batchSize; j++) {
                double[] data = Constant.trainingData.get(rand.nextInt(Constant.trainingData.size()));
                Node[] output = NetworkManager.feedForward(data);
                double[] target = new double[output.length];
                target[Constant.getSampleLabel(data)] = 1;

                // output to hidden layer
                Layer outputLayer = network.getLayer(network.getLayerCount()-1);
                Layer hiddenLayer = network.getLayer(network.getLayerCount()-2);
                for(int k=0; k<outputLayer.getNodeCount(); k++) {
                    Node currentNode = outputLayer.getNode(k);
                    double delta = currentNode.getActivation() - target[k];
                    currentNode.setActivation(delta);
                    int biasIndex = currentNode.getParamIndex();
                    double shiftBias = -Calculator.biasLearnRate * delta;
                    gradientVector[biasIndex] += shiftBias;
                    for(int l=0; l<hiddenLayer.getNodeCount(); l++) {
                        Node hiddenNode = hiddenLayer.getNode(l);
                        Edge weight = hiddenNode.getEdge(k);
                        int weightIndex = weight.getParamIndex();

                        double shiftWeight = -Calculator.weightLearnRate * delta * hiddenNode.getActivation();

                        gradientVector[weightIndex] += shiftWeight;
                    }
                }

                // hidden to hidden/input layers
                for(int k=network.getLayerCount()-3; k>=0; k--) {
                    Layer prevLayer = network.getLayer(k+1);
                    Layer currentLayer = network.getLayer(k);

                    double[] result = new double[currentLayer.getNodeCount()];
                    for(int l=0; l<prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double activation = currentNode.getActivation();
                        for(int m=0; m<currentLayer.getNodeCount(); m++) {
                            Edge weight = currentLayer.getNode(m).getEdge(l);
                            result[m] += weight.getWeight() * activation;
                        }
                    }
                    for(int l=0; l<result.length; l++) result[l] *= Calculator.sigmoidPrime(currentLayer.getNode(l).getActivation());

                    for(int l=0; l<prevLayer.getNodeCount(); l++) {
                        Node currentNode = prevLayer.getNode(l);
                        double delta = result[l];
                        currentNode.setActivation(delta);
                        int biasIndex = currentNode.getParamIndex();
                        double shiftBias = -Calculator.biasLearnRate * delta;
                        gradientVector[biasIndex] += shiftBias;
                        for(int m=0; m<currentLayer.getNodeCount(); m++) {
                            Node hiddenNode = currentLayer.getNode(m);
                            Edge weight = hiddenNode.getEdge(l);
                            int weightIndex = weight.getParamIndex();

                            double shiftWeight = -Calculator.weightLearnRate * delta * hiddenNode.getActivation();

                            gradientVector[weightIndex] += shiftWeight;
                        }
                    }
                }
            }
        }

        network.updateParameters(gradientVector);

        // Update parameter database from the training data
        network.getParamManager().updateTrainingData();
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
        int batches = Constant.trainingData.size()/(int)(baseAmt*1.25);
        for(int i=0; i<batches; i++) {
            Random rand = new Random();
            int amt = baseAmt+rand.nextInt(2*sizeError+1);
            totalCases += amt;
            for(int j=0; j<amt; j++) {
                double[] data = Constant.trainingData.get(rand.nextInt(Constant.trainingData.size()));
                Node[] output = NetworkManager.feedForward(data);
                double[] target = new double[output.length];
                target[Constant.getSampleLabel(data)] = 1;

                int currWeightIndex = network.getParamManager().getWeightCount()-1;
                int currBiasIndex = network.getParamManager().getParamCount()-1;
                for(int k=network.getLayerCount()-1; k>=0; k++) {
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
                            gradientVector[currBiasIndex--] += Calculator.biasPartialDiff(current.getActivation(), sum, k == network.getLayerCount() -1 ? target[l] : network.getLayer(k+1).getNode(l).getActivation());
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