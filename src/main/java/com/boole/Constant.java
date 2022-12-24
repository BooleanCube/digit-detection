package com.boole;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Constant {

    private static BufferedImage image;

    public final static int imageSize = 28;
    public final static int drawingBoardSize = 280;

    public static ArrayList<double[]> trainingData = new ArrayList<>();
    public static ArrayList<double[]> testingData = new ArrayList<>();

    public static int getSampleLabel(double[] data) {
        return (int) data[Constant.imageSize * Constant.imageSize];
    }

    public static double[] parseImageFile(File imageFile) {
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) { throw new RuntimeException(e); }
        double[] data = new double[Constant.imageSize * Constant.imageSize + 1];
        for(int i=0; i<data.length-1; i+=Constant.imageSize) {
            int finalI = i;
            int[] row = IntStream.range(0, Constant.imageSize).map(j -> image.getRGB(j,  finalI / Constant.imageSize)).map(pixel -> (pixel >> 16) & 0xff).toArray();
            double[] normalized = Arrays.stream(row).mapToDouble(x -> 1.0 - x / 255.0).toArray();
            System.arraycopy(normalized, 0, data, i, normalized.length);
        }
        if(!imageFile.getName().equals("input.jpg"))
            data[Constant.imageSize * Constant.imageSize] = Integer.parseInt(imageFile.getName().split("_")[0]);
        return data;
    }

}
