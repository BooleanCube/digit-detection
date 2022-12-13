package com.boole.network.models;

public class Edge {

    private Node startNode;
    private Node endNode;
    private double weight;
    private int paramIndex;

    public Edge(Node startNode, Node endNode, int paramIndex, double weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.paramIndex = paramIndex;
        this.weight = weight;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    public String toString() {
        return this.startNode + " -> " + this.endNode + "(" + this.weight + ")";
    }

}