package com.janfic.games.library.utils;

import com.badlogic.gdx.graphics.Color;

import java.util.Comparator;

public class HLSColorComparator implements Comparator<Color> {
    @Override
    public int compare(Color a, Color b) {
        return 0;
    }

    private float[] rgbToHSL(Color color) {
        float[] hls = new float[3];
        float max = Math.max(color.r, Math.max(color.g, color.b));
        float min = Math.min(color.r, Math.min(color.g, color.b));
        float h = 0, s, l = (max + min) / 2f;

        if(max == min) {
            h = 0;
            s = 0;
        }
        else {
            float d = max - min;
            s = l > 0.5 ? d / ( 2 - max - min) : d / (max + min);
            if( max == color.r ) {
                h = (color.g - color.b) / d + (color.g < color.b ? 6 : 0);
            }
            else  if (max == color.g) {
                h = (color.b - color.r) / d + 2;
            }
            else if ( max == color.r) {
                h = (color.r - color.g) / d + 4;
            }
            h = h / 6f;
        }

        hls[0] = h;
        hls[1] = s;
        hls[2] = l;

        return hls;
    }
}
