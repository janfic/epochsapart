package com.janfic.games.library.ecs.components.inventory;

import com.badlogic.ashley.core.Component;

public class ItemComponent implements Component {
    public String name;
    public int width, height;
    public float[][] space;
}
