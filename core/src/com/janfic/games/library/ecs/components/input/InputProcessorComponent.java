package com.janfic.games.library.ecs.components.input;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.InputProcessor;

public class InputProcessorComponent implements Component {
    public InputProcessor inputProcessor;
    public int priority;
}
