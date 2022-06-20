package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class Palette {
    private String name;
    private ArrayList<Color> colors;

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
                Color.BROWN));
    }

    public Palette(String name, FileHandle handle) {
        Scanner scanner = new Scanner(handle.readString());
        this.name = name;
        this.colors = new ArrayList<>();
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.startsWith("#")) continue;;
            if(line.startsWith("GIMP Palette")) continue;
            String[] colorData = line.split("\\s*");
            if(colorData.length != 4) continue;
            int r = Integer.parseInt(colorData[0]);
            int g = Integer.parseInt(colorData[1]);
            int b = Integer.parseInt(colorData[2]);
            String colorName = line.substring(line.indexOf(colorData[3]));
            Color color = new Color(r, g, b, 1f);
            colors.add(color);
        }
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public Texture createTexture() {
        Pixmap pixmap = new Pixmap(colors.size(), 1, Pixmap.Format.RGBA8888);

        for (int i = 0; i < colors.size(); i++) {
            pixmap.setColor(colors.get(i));
            pixmap.drawPixel(i , 0);
        }

        return new Texture(pixmap);
    }

    public int size() {
        return colors.size();
    }

    public String getName() {
        return name;
    }


}
