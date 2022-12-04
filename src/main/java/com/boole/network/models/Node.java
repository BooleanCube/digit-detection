package com.boole.network.models;

import java.util.ArrayList;

public class Node {

    private double bias;
    private double activation;
    private ArrayList<Edge> edges;

    public Node() {
        this.edges = new ArrayList<>();
    }

    public Node(double bias, double activation) {
        this.bias = bias;
        this.activation = activation;
        this.edges = new ArrayList<>();
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

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public Edge getEdge(int idx) {
        return this.edges.get(idx);
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public void connectToLayer(NeuralNetwork network, int layerIndex) {
        // TODO connect this node and create edges connecting this node to all nodes in the next layer assigning edge weights from the database
    }

}
