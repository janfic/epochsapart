package com.janfic.games.library.ecs.components.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationComponent<T> implements Component {
    Animation<T> animation;
}
