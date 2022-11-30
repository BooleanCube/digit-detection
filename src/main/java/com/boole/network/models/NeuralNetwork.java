package com.boole.network.models;

import com.boole.network.ParamManager;
import org.json.simple.parser.ParseException;

import java.io.IOException;

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

    public int getLayerCount() {
        return layerCount;
    }

    public ParamManager getParamManager() {
        return params;
    }
}