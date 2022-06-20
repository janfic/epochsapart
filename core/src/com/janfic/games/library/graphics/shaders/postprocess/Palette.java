package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

/**
 * Describes a named set of colors.
 * Can be instantiated using a .gpl file ( GIMP Palette File Format ) or through traditional programmatic methods.
 */
public class Palette {
    private String name;
    private ArrayList<Color> colors;
    private Texture paletteTexture;

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
        paletteTexture = createTexture();
    }

    public Palette(String name) {
        this.name = name;
        this.colors = new ArrayList<>();
    }

    public Palette(String name, FileHandle handle) {
        Scanner scanner = new Scanner(handle.readString());
        this.name = name;
        this.colors = new ArrayList<>();
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
        paletteTexture = createTexture();
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    /**
     * Creates a texture with all colors. Size ( width ) is determined by colors in palette.
     * @return
     */
    public Texture createTexture() {
        Pixmap pixmap = new Pixmap(colors.size(), 1, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);

        for (int i = 0; i < colors.size(); i++) {
            pixmap.setColor(colors.get(i));
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
        return paletteTexture;
    }

    public int size() {
        return colors.size();
    }

    public String getName() {
        return name;
    }


}
