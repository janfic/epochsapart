package com.janfic.games.library.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.files.FileHandle;

public class GenerateWorldComponent implements Component {
    public int length, width, height;
    public FileHandle generationSettings;
}
