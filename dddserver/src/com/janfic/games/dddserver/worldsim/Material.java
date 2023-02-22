package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

public abstract class Material {
    String name;
    float amount;
    Color color;
    Tile tile;

    public Material(String name, Color color, float amount) {
        this.color = color;
        this.name = name;
        this.amount = amount;
    }

    public void addToMesh(VertexAttributes attributes, float[] vertices, short[] indices, int vertexOffset, int indexOffset, int renderType, float bottomHeight) {

    }
}
