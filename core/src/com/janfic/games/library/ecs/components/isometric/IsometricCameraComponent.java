package com.janfic.games.library.ecs.components.isometric;

import com.badlogic.ashley.core.Component;

public class IsometricCameraComponent implements Component {
    public float angle, target;
    public float snapAngle, offset;
    public float distance;
}
