package com.boole.mnist;

import com.boole.Constant;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MnistParser {

    public static void main(String[] args) throws IOException {
        MnistMatrix[] mnistMatrix = new MnistDataReader().readData("samples/data/train-images.idx3-ubyte", "samples/data/train-labels.idx1-ubyte");
        parseMnistData(mnistMatrix, "samples/training/");
        printMnistMatrix(mnistMatrix[0]);
        mnistMatrix = new MnistDataReader().readData("samples/data/t10k-images.idx3-ubyte", "samples/data/t10k-labels.idx1-ubyte");
        parseMnistData(mnistMatrix, "samples/testing/");
        printMnistMatrix(mnistMatrix[0]);
    }

    private static void parseMnistData(MnistMatrix[] data, String outputPath) throws IOException {
        int[] freq = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for(MnistMatrix matrix : data) {
            BufferedImage image = new BufferedImage(Constant.imageSize, Constant.imageSize, BufferedImage.TYPE_INT_RGB);
            Graphics gfx = image.createGraphics();
            for(int r = 0; r < matrix.getNumberOfRows(); r++ ) {
                for(int c = 0; c < matrix.getNumberOfColumns(); c++) {
                    int rgb = matrix.getValue(r, c);
                    gfx.setColor(new Color(rgb, rgb, rgb));
                    gfx.fillRect(r, c, 1, 1);
                }
            }
            int label = matrix.getLabel();
            String fileName = label + "_" + freq[label] + ".jpg";
            freq[label]++;
            File imageFile = new File(outputPath+fileName);
            if(!imageFile.exists()) imageFile.createNewFile();
            ImageIO.write(image, "jpg", imageFile);
        }
    }

    // prints matrix for debugging
    private static void printMnistMatrix(final MnistMatrix matrix) {
        System.out.println(matrix.getLabel());
        for(int r = 0; r < matrix.getNumberOfRows(); r++ ) {
            for(int c = 0; c < matrix.getNumberOfColumns(); c++) {
                System.out.print(matrix.getValue(r, c) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}