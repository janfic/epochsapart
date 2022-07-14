package com.janfic.games.library.ecs.components.body;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.body.BodyPart;

import java.util.List;

public class BodyComponent implements Component {
    public List<BodyPart> parts;
    public float maxHealth, currentHealth;
    public Entity owner;
}
