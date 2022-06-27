package com.janfic.games.library.ecs.components.inventory;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.List;

public class InventoryComponent implements Component {
    String name;
    int width, height;
    boolean[][] slots;
    List<Entity> items;
}
