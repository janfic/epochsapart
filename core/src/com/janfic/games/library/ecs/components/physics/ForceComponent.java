package com.janfic.games.library.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class ForceComponent implements Component {
    public List<Vector3> forces;
}
