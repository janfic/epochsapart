package com.janfic.games.library.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class RotationComponent implements Component {
    public Vector3 axis;
    public float angle;
}
