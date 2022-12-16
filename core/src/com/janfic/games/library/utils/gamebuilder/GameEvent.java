package com.janfic.games.library.utils.gamebuilder;

public abstract class GameEvent<T extends GameState> extends GameStateChange<T>{
    public GameEvent(String invalidMessage) {
        super(0, 0);
    }
}
