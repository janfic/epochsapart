package com.janfic.games.library.ecs.components.planet;

import com.badlogic.ashley.core.Component;
import com.janfic.games.library.utils.isometric.IsometricWorld;

import java.util.List;

public class PlanetComponent implements Component {
    public IsometricWorld world;
    public List<TectonicPlate> plates;
}
