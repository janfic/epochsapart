package com.janfic.games.library.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

import java.util.List;
import java.util.Map;

public class ForceComponent implements Component {
    public Map<String, Vector3> named;
    public List<Vector3> forces;
}
