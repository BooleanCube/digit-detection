package com.boole.network.models;

public class Layer {

    private Node[] nodes;
    private int nodeCount;
    private LayerType type;

    public Layer(LayerType type) {
        this.type = type;
    }

    public Layer(LayerType type, Node[] nodes) {
        this.type = type;
        this.nodes = nodes;
        this.nodeCount = this.nodes.length;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
        this.nodeCount = this.nodes.length;
    }

    public LayerType getType() {
        return type;
    }

    public void setType(LayerType type) {
        this.type = type;
    }

    public int getNodeCount() {
        return this.nodeCount;
    }

}
