package com.boole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the number corresponding to the action you would like to perform:\n" +
                "1) Train the Neural Network\n" +
                "2) Detect your hand-written digits with the neural network\n");
    }

}
