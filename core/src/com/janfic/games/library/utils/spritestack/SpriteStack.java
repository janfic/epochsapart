package com.janfic.games.library.utils.spritestack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteStack {
    Texture texture;
    Sprite[] sprites;
    int width, height, layers;
    float layerSpacing = 1;
    int layerRepeats = 1;
    float rotation;

    public SpriteStack(Texture texture, int layerWidth, int layerHeight, float layerSpacing, int layerRepeats) {
        this.width = layerWidth;
        this.height = layerHeight;
        this.texture = texture;
        int tWidth = (texture.getWidth() / layerWidth);
        int tHeight = (texture.getHeight() / layerHeight);
        this.layers = tWidth * tHeight;
        sprites = new Sprite[layers];
        int index = 0;
        for(int y = 0; y < tHeight; y++) {
            for(int x = 0; x < tWidth; x++) {
                Sprite sprite = new Sprite(texture, x * layerWidth, texture.getHeight() - ((y + 1)* layerHeight), layerWidth, layerHeight);
                sprite.setOrigin(layerWidth/2f, layerHeight/2f);
                sprites[index++] = sprite;
            }
        }
        System.out.println(index);
        this.layerSpacing = layerSpacing;
        this.layerRepeats = layerRepeats;
    }

    public SpriteStack(Sprite[] sprites) {
        this.sprites = sprites;
    }

    public float getLayerSpacing() {
        return layerSpacing;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getRotation() {
        return rotation;
    }

    public void setStackRotation(float rotation) {
        this.rotation = rotation;
    }

    public int getLayerRepeats() {
        return layerRepeats;
    }

    public void setLayerRepeats(int layerRepeats) {
        this.layerRepeats = layerRepeats;
    }
}
