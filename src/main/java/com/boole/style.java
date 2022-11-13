package com.boole;

import java.awt.Color;
import java.awt.Font;

public @interface style {
    Font bigFont = new Font("arial", Font.BOLD, 24);
    Font hugeFont = new Font("arial", Font.BOLD, 72);
    Font normalFont = new Font("arial", Font.BOLD, 12);
    Font smallFont = new Font("arial", Font.PLAIN, 11);
    Color greenHighlight = new Color(132, 255, 138);
    Color redHighlight = new Color(253, 90, 90);
    Color blueHighlight = new Color(32, 233, 255);
    Color darkText = new Color(48, 48, 48);
    Color lightText = new Color(232, 232, 232);
}