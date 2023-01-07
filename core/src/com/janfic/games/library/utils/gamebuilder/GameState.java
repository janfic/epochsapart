package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;

import java.util.List;

/**
 *
 */
public abstract class GameState<G extends Game<? extends GameState<G>>> extends Group implements Json.Serializable {
    // TODO: Add GameState Comparison / Change Serialization
    G game;

    public GameState() {}
    public GameState(G game) {this.game = game;}

    public abstract void update(float delta);

    public abstract void reset();
    public abstract void repopulate(GameState state);

    public G getGame() {
        return game;
    }

    public void setGame(G game) {
        this.game = game;
    }
}
