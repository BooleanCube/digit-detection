package com.boole.math;

import com.boole.network.models.Node;

/**
 * Static calculator class with all the relevant calculus equations and models used.
 * ----
 * Table of Elements =>
 * C = Cost
 * L = Layer
 * w = weight
 * b = bias
 * a = activation (previous/current)
 * z = sum (wa+b)
 * y = goal
 * σ = sigmoid function symbol
 * ∂ = partial differential symbol
 * ----
 * Models =>
 * C = (aL-y)^2
 * zL = wL * aL + bL
 * aL = σ(zL)
 */
public class Calculator {

    public static final double learnRate = 0.01d;

    private final static double sigmoidStretch = 1;

    // 1/(1+(e-stretch)^(-x))^(-1)
    public static double sigmoid(double x) {
        return 1/(1+Math.pow(Math.E-sigmoidStretch, -x));
    }

    // σ(x)(1-σ(x))
    // https://towardsdatascience.com/derivative-of-the-sigmoid-function-536880cf918e
    public static double sigmoidDerivative(double x) {
        return sigmoid(x)*(1-sigmoid(x));
    }

    /**
     * Calculates the derivative of the sigmoid function.
     *
     * @param x a double representing the input to the sigmoid function
     * @return the derivative of the sigmoid function applied to x
     */
    public static double sigmoidPrime(double x) {
        return x * (1 - x);
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

    /**
     * ∂C/∂wL => direction of the steepest descent with respect to the weight
     */
    public static double weightPartialDiff(double prevActivation, double currActivation, double sum, double goal) {
        // Chain Rule ===>
        // ∂C/∂wL = ∂C/∂aL * ∂aL/∂zL * ∂zL/∂wL
        // Derivative of cost with respect to weight =
        // Derivative of cost with respect to activation * Derivative of activation with respect to sum * Derivative of sum with respect to weight
        return costDiffActivation(currActivation, goal) * activationDiffSum(sum) * sumDiffWeight(prevActivation);
    }

    /**
     * ∂C/∂bL => direction of the steepest descent with respect to the bias
     */
    public static double biasPartialDiff(double currActivation, double sum, double goal) {
        // Chain Rule ===>
        // ∂C/∂bL = ∂C/∂aL * ∂aL/∂zL * ∂zL/∂bL
        // Derivative of cost with respect to weight =
        // Derivative of cost with respect to activation * Derivative of activation with respect to sum * Derivative of sum with respect to bias
        return costDiffActivation(currActivation, goal) * activationDiffSum(sum) * sumDiffBias();
    }

    /**
     * ∂C/∂aL => direction of the steepest descent with respect to the previous activation
     */
    public static double activationPartialDiff(double currActivation, double weight, double sum, double goal) {
        // Chain Rule ===>
        // ∂C/∂aL = ∂C/∂aL * ∂aL/∂zL * ∂zL/∂aL
        // Derivative of cost with respect to previous activation =
        // Derivative of cost with respect to activation * Derivative of activation with respect to sum * Derivative of sum with respect to previous activation
        return costDiffActivation(currActivation, goal) * activationDiffSum(sum) * sumDiffActivation(weight);
    }

    // ∂C/∂aL (Derivative of cost with respect to activation)
    public static double costDiffActivation(double activation, double goal) {
        return 2*(activation-goal);
    }

    // ∂aL/∂zL (Derivative of activation with respect to sum)
    public static double activationDiffSum(double sum) {
        return sigmoidPrime(sum);
    }

    // ∂zL/∂wL (Derivative of sum with respect to weight)
    public static double sumDiffWeight(double activation) {
        return activation;
    }

    // ∂zL/∂bL (Derivative of sum with respect to bias)
    public static double sumDiffBias() {
        return 1;
    }

    // ∂zL/∂aL (Derivative of sum with respect to previous activation)
    public static double sumDiffActivation(double weight) {
        return weight;
    }

}
