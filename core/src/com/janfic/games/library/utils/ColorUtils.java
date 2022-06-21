package com.janfic.games.library.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Conversion Algorithms from <a href="http://www.easyrgb.com/en/math.php">Easy RGB</a>
 */
public class ColorUtils {

    public static Map<String, Vector3> XYZ_REFERENCE_VALUES = new HashMap<>();

    public final static Vector3 colorToRGBVector(Color color) {
        Vector3 vector3 = new Vector3(color.r, color.g, color.b);
        return vector3;
    }

    public final static ArrayList<Vector3> convertRGBPaletteToHSL(ArrayList<Color> colors) {
        ArrayList<Vector3> rgbs = new ArrayList<>();
        for (Color color : colors) {
            rgbs.add(colorToRGBVector(color));
        }

        ArrayList<Vector3> hsls = new ArrayList<>();
        for (Vector3 rgb : rgbs) {
            Vector3 hsl = rgbToHSL(rgb);
            hsls.add(hsl);
        }
        return hsls;
    }

    public final static Vector3 rgbToXYZ(Vector3 vector3) {
        float r = vector3.x;
        float g = vector3.y;
        float b = vector3.z;

        if(r > 0.04045) r = (float) Math.pow((r + 0.055f) / 1.055f, 2.4f);
        else            r = r / 12.92f;
        if(g > 0.04045) g = (float) Math.pow((g + 0.055f) / 1.055f, 2.4f);
        else            g = g / 12.92f;
        if(b > 0.04045) b = (float) Math.pow((b + 0.055f) / 1.055f, 2.4f);
        else            b = b / 12.92f;

        r = r * 100f;
        g = g * 100f;
        b = b * 100f;

        float x = r * 0.4124f + g * 0.3576f + b * 0.1805f;
        float y = r * 0.2126f + g * 0.7152f + b * 0.0722f;
        float z = r * 0.0193f + g * 0.1192f + b * 0.9505f;

        return new Vector3(x,y,z);
    }

    public final static Vector3 xyzToCIELab(Vector3 vector) {
        float x = vector.x / 100f;
        float y = vector.y / 100f;
        float z = vector.z / 100f;

        if ( x > 0.008856f ) x = (float) Math.pow(x, 1d/3d);
        else                    x = ( 7.787f * x ) + ( 16f / 116f );
        if ( y > 0.008856f ) y = (float) Math.pow(y, 1d/3d);
        else                    y = ( 7.787f * y ) + ( 16f / 116f );
        if ( z > 0.008856f ) z = (float) Math.pow(z, 1d/3d);
        else                    z = ( 7.787f * z ) + ( 16f / 116f );

        float l = (116f * y) - 16;
        float a = 500f * ( x - y);
        float b = 200f * ( y - z);

        return new Vector3(l, a, b);
    }

    public final static Vector3 rgbToHSL(Vector3 rgbVector) {
        float var_R = rgbVector.x;
        float var_G = rgbVector.y;
        float var_B = rgbVector.z;
        float var_Min = Math.min( Math.min(var_R, var_G), var_B );      //Min. value of RGB
        float var_Max = Math.max( Math.max(var_R, var_G), var_B );     //Max. value of RGB
        float del_Max = var_Max - var_Min   ;          //Delta RGB value

        float L = ( var_Max + var_Min )/2f;
        float H = 0;
        float S = 0;

        if ( del_Max == 0 )                     //This is a gray, no chroma...
        {
            H = 0;
            S = 0;
        }
        else                                    //Chromatic data...
        {
            if ( L < 0.5 ) S = del_Max / ( var_Max + var_Min );
            else           S = del_Max / ( 2 - var_Max - var_Min );

            float del_R = ( ( ( var_Max - var_R ) / 6f ) + ( del_Max / 2f ) ) / del_Max;
            float del_G = ( ( ( var_Max - var_G ) / 6f ) + ( del_Max / 2f ) ) / del_Max;
            float del_B = ( ( ( var_Max - var_B ) / 6f ) + ( del_Max / 2f ) ) / del_Max;

            if      ( var_R == var_Max ) H = del_B - del_G;
            else if ( var_G == var_Max ) H = ( 1 / 3 ) + del_R - del_B;
            else if ( var_B == var_Max ) H = ( 2 / 3 ) + del_G - del_R;

            if ( H < 0 ) H += 1;
            if ( H > 1 ) H -= 1;
        }

        return new Vector3(H, S, L);
    }
}
