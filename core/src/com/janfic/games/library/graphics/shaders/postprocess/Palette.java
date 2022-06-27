package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Describes a named set of colors.
 * Can be instantiated using a .gpl file ( GIMP Palette File Format ) or through traditional programmatic methods.
 */
public class Palette {
    private String name;
    private ArrayList<Color> colors;
    private Texture rgbTexture, hslTexture;

    public Palette() {
        this.name = "Default Palette";
        this.colors = new ArrayList<>();
        this.colors.addAll(Arrays.asList(Color.BLACK,
                Color.WHITE,
                Color.RED,
                Color.ORANGE,
                Color.YELLOW,
                Color.GREEN,
                Color.CYAN,
                Color.BLUE,
                Color.PINK,
                Color.MAGENTA,
                Color.BROWN,
                Color.WHITE));
        Texture[] textures = createTextures();
        rgbTexture = textures[0];
        hslTexture = textures[1];
    }

    public Palette(String name) {
        this.name = name;
        this.colors = new ArrayList<>();
        this.colors.add(Color.BLACK);
    }

    public Palette(String name, FileHandle handle) {
        Scanner scanner = new Scanner(handle.readString());
        this.name = name;
        this.colors = new ArrayList<>();
        this.colors.add(Color.BLACK);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.startsWith("#")) continue;;
            if(line.startsWith("GIMP Palette")) continue;
            String[] colorData = line.split("\\s+");
            if(colorData.length != 4) continue;
            int r = Integer.parseInt(colorData[0]);
            int g = Integer.parseInt(colorData[1]);
            int b = Integer.parseInt(colorData[2]);
            String colorName = line.substring(line.indexOf(colorData[3]));
            Color color = new Color(r / 255f, g / 255f, b / 255f, 1f);
            colors.add(color);
        }
        Texture[] textures = createTextures();
        rgbTexture = textures[0];
        hslTexture = textures[1];
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public Texture[] createTextures() {
        Texture[] textures = new Texture[2];
        ArrayList<Vector3> rgbs = new ArrayList<>();
        for (Color color : colors) {
            Vector3 rgb = ColorUtils.colorToRGBVector(color);
            rgbs.add(rgb);
        }
        ArrayList<Vector3> hsls = ColorUtils.convertRGBPaletteToHSL(colors);
        textures[0] = createTexture(rgbs);
        textures[1] = createTexture(hsls);
        return textures;
    }

    /**
     * Creates a texture with all colors. Size ( width ) is determined by colors in palette.
     * @return
     */
    public Texture createTexture(ArrayList<Vector3> colors) {
        Pixmap pixmap = new Pixmap(colors.size(), 1, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        for (int i = 0; i < colors.size(); i++) {
            pixmap.setColor(colors.get(i).x, colors.get(i).y, colors.get(i).z, 1);
            pixmap.fillRectangle(i , 0, 1, 1);
        }

        Texture t = new Texture(pixmap);
        pixmap.dispose();

        return t;
    }

    /**
     * Retrieves saved texture of palette. Usually used with shaders. See PaletteShader.
     * @return
     */
    public Texture getTexture() {
        return rgbTexture;
    }

    public Texture getHSLTexture() {
        return hslTexture;
    }

    public Texture getRGBTexture() {
        return rgbTexture;
    }

    public int size() {
        return colors.size();
    }

    public String getName() {
        return name;
    }

    public void addColor(Color color) {
        colors.add(color);
        Texture[] textures = createTextures();
        rgbTexture = textures[0];
        hslTexture = textures[1];
    }

//    public Texture transformTextureClosest(Texture texture) {
//
//    }

//    public Texture transformTextureIndexed(Texture texture) {
//
//    }
}
