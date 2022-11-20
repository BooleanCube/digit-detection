package com.boole.network;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Random;

public class ParamManager {

    private static final int weightCount = 12960;
    private static final int biasCount = 42;
    private static final int paramCount = weightCount + biasCount;

    private static final File paramDatabase = new File("database/params.json");

    private static final double[] parameters = new double[paramCount];

    public static double[] getParameters() {
        return parameters;
    }

    public static void init() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject)(parser.parse(new FileReader(paramDatabase)));
        JSONArray jsonData = (JSONArray) json.get("params");
        if(jsonData.size() != paramCount) resetTrainingData();
        for(int i=0; i<paramCount; i++) parameters[i] = (double) jsonData.get(i);
    }

    public static void resetTrainingData() throws IOException {
        final int maxBound = 4;
        final int minBound = -maxBound;

        JSONObject json = new JSONObject();
        JSONArray jsonData = new JSONArray();
        for(int i=0; i<paramCount; i++) {
            Random generator = new Random();
            double random = minBound + (maxBound-minBound) * generator.nextDouble();
            jsonData.add(i, random);
        }
        json.put("params", jsonData);

        FileWriter writer = new FileWriter(paramDatabase);
        writer.write(json.toString());
        writer.flush();
        writer.close();
    }

}