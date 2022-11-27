package com.boole.network;

import java.util.ArrayList;

public class NeuralNetwork {

    public Layer[] layers;

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

class Node {

    private int weight;
    private int bias;
    private int activation;

    public Node(int weight, int bias, int activation) {
        this.weight = weight;
        this.bias = bias;
        this.activation = activation;
    }

    public Node(int weight, int bias) {
        this.weight = weight;
        this.bias = bias;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getBias() {
        return bias;
    }

    public void setBias(int bias) {
        this.bias = bias;
    }

    public int getActivation() {
        return activation;
    }

    public void setActivation(int activation) {
        this.activation = activation;
    }

}