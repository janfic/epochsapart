package com.janfic.games.library.ecs.components.input;

import com.badlogic.ashley.core.Component;
import com.janfic.games.library.utils.ECSClickListener;

public class ClickableComponent implements Component {
    public ECSClickListener listener;
}
