package com.janfic.games.library.utils.spritestack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteStack {
    Texture texture;
    Sprite[] sprites;
    int width, height, layers;
    float layerWidth = 1;
    float rotation;

    public SpriteStack(Texture texture, int width, int height, float layerWidth) {
        this.width = width;
        this.height = height;
        this.texture = texture;
        int tWidth = (texture.getWidth() / width);
        int tHeight = (texture.getHeight() / height);
        this.layers = tWidth * tHeight;
        sprites = new Sprite[layers];
        for(int x = 0; x < tWidth; x++) {
            for(int y = 0; y < tHeight; y++) {
                Sprite sprite = new Sprite(texture, x * width, y * height, width, height);
                sprite.setOrigin(width/2f, height/2f);
                sprites[x + y * tWidth] = sprite;
            }
        }
        this.layerWidth = layerWidth;
    }

    public SpriteStack(Sprite[] sprites) {
        this.sprites = sprites;
    }

    public float getLayerWidth() {
        return layerWidth;
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
}
