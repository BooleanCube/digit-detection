package com.boole.math;

public class MatrixOperations {

    public static double[][] matrixMultiplication(double[][] a, double[][] b) {
        /* Create another 2d array to store the result using the original arrays' lengths on row and column respectively. */
        double [][] result = new double[a.length][b[0].length];

        /* Loop through each and get product, then sum up and store the value */
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }

    public static double[][] matrixTranspose(double[][] matrix){
        int m = matrix.length;
        int n = matrix[0].length;

        double[][] transposedMatrix = new double[n][m];

        for(int x=0; x<n; x++) {
            for(int y=0; y<m; y++) {
                transposedMatrix[x][y] = matrix[y][x];
            }
        }

        return transposedMatrix;
    }

    public static double[][] matrixAddition(double[][] a, double[][] b) {
        if ((a.length != b.length) ||  (a[0].length != b[0].length)  ) {
            System.out.println("Error: arrays are not of the same size");
            return null;
        }

        int rows = a.length;
        int cols = a[0].length;

        double[][] result = new double[rows][cols];

        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                result[i][j] = b[i][j] + a[i][j];
            }
        }

        return result;
    }

}
