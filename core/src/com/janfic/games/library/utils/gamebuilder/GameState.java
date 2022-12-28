package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;

import java.util.List;

/**
 *
 */
public abstract class GameState extends Group implements Json.Serializable {
    // TODO: Add GameState Comparison / Change Serialization
    public abstract void reset();
    public abstract void repopulate(GameState state);
}
