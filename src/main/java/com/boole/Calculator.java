package com.boole;

import com.boole.network.models.Node;

public class Calculator {

    private final static double sigmoidStretch = 1.5;

    public static double sigmoid(double x) {
        return 1/(1+Math.pow(Math.E-sigmoidStretch, -x));
    }

    public static double calculateCost(Node[] output, double[] exact) {
        double diff = 0.0;
        for(int i=0; i<output.length; i++)
            diff += Math.pow(output[i].getActivation()-exact[i], 2);
        return diff;
    }

    public static int findMaximumActivation(Node[] arr) {
        if(arr==null || arr.length==0) return -1;
        int largest = 0;
        for(int i=1; i<arr.length; i++)
            if(arr[i].getActivation()>arr[largest].getActivation()) largest = i;
        return largest;
    }

}
