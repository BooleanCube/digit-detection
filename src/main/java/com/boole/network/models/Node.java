package com.boole.network.models;

import com.boole.network.models.Edge;
import com.boole.network.models.NeuralNetwork;

public class Node {

    private double bias;
    private double activation;
    private Edge[] edges;

    public Node() {}

    public Node(double bias, double activation) {
        this.bias = bias;
        this.activation = activation;
    }

    public Node(double activation) {
        this.activation = activation;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double getActivation() {
        return activation;
    }

    public void setActivation(double activation) {
        this.activation = activation;
    }

    public void connectToLayer(NeuralNetwork network, int layerIndex) {
        // TODO connect this node and create edges connecting this node to all nodes in the next layer assigning edge weights from the database
    }

}
