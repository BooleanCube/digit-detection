package com.boole;

import java.io.File;

public class Constant {

    public final static int imageSize = 28;
    public final static int drawingBoardSize = 280;

    public final static File[] trainingData = new File("samples/training/").listFiles();
    public final static File[] testingData = new File("samples/testing/").listFiles();

}
