package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;

public abstract class Material {
    String name;
    float amount;
    Color color;

    public Material(String name, Color color, float amount) {
        this.color = color;
        this.name = name;
        this.amount = amount;
    }
}
