package com.boole.network;

import com.boole.network.models.Layer;
import com.boole.network.models.LayerType;
import com.boole.network.models.NeuralNetwork;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Random;

public class ParamManager {

    private int weightCount = 0;
    private int biasCount = 0;
    private int paramCount = 0;

    private File paramDatabase;

    private double[] parameters;

    public double[] getParameters() {
        return parameters;
    }

    public int getParamCount() {
        return paramCount;
    }

    public int getWeightCount() {
        return weightCount;
    }

    public int getBiasCount() {
        return biasCount;
    }

    public double getParameter(int idx) {
        return this.parameters[idx];
    }

    public ParamManager(NeuralNetwork network) throws IOException, ParseException {
        for(int i=1; i<network.getLayerCount(); i++) {
            Layer layer = network.getLayer(i);
            if(layer.getType() != LayerType.BASE) this.biasCount += layer.getNodeCount();
            this.weightCount += network.getLayer(i-1).getNodeCount() * layer.getNodeCount();
        }
        this.paramCount = this.weightCount + this.biasCount;
        this.parameters = new double[this.paramCount];
        this.paramDatabase = new File("database/params" + this.paramCount + ".json");
        if(!this.paramDatabase.exists()) {
            this.paramDatabase.createNewFile();
            this.resetTrainingData();
        }
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject)(parser.parse(new FileReader(paramDatabase)));
        JSONArray jsonData = (JSONArray) json.get("params");
        if(jsonData.size() != paramCount) resetTrainingData();
        for(int i=0; i<paramCount; i++) parameters[i] = (double) jsonData.get(i);
    }

    public void resetTrainingData() throws IOException {
        final int maxBound = 3;
        final int minBound = -maxBound;

        JSONObject json = new JSONObject();
        JSONArray jsonData = new JSONArray();
        for(int i=0; i<paramCount; i++) {
            Random generator = new Random();
            double random = minBound + (maxBound-minBound) * generator.nextDouble();
            parameters[i] = random;
            jsonData.add(i, random);
        }
        json.put("params", jsonData);

        FileWriter writer = new FileWriter(paramDatabase);
        writer.write(json.toString());
        writer.flush();
        writer.close();
    }

    public void updateTrainingData() throws IOException {
        JSONObject json = new JSONObject();
        JSONArray jsonData = new JSONArray();
        for(int i=0; i<paramCount; i++)
            jsonData.add(i, parameters[i]);
        json.put("params", jsonData);

        FileWriter writer = new FileWriter(paramDatabase);
        writer.write(json.toString());
        writer.flush();
        writer.close();
    }

}