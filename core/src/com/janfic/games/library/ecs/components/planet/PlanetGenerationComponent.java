package com.janfic.games.library.ecs.components.planet;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.files.FileHandle;

public class PlanetGenerationComponent implements Component {
    public int width, height, length;
    public FileHandle planetSettings;
    public boolean completed;
}
