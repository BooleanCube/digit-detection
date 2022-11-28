package com.boole.network;

import java.util.ArrayList;

public class NeuralNetwork {

    public Layer[] layers;
    private int edgeCount;
    private int biasCount;

    public NeuralNetwork(int layerCount) {
        this.layers = new Layer[layerCount];
    }

    public void setLayers(Layer... layers) {
        this.layers = layers;
    }

    public Layer getLayer(int index) {
        return this.layers[index];
    }

}

enum LayerType {
    BASE, HIDDEN, OUTPUT;
}

class Layer {

    private Node[] nodes;
    private LayerType type;

    public Layer(LayerType type) {
        this.type = type;
    }

    public Layer(LayerType type, Node[] nodes) {
        this.type = type;
        this.nodes = nodes;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    public LayerType getType() {
        return type;
    }

    public void setType(LayerType type) {
        this.type = type;
    }

}

class Edge {

    private Node startNode;
    private Node endNode;
    private double weight;

    public Edge(Node startNode, Node endNode, double weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight = weight;
    }

}

class Node {

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