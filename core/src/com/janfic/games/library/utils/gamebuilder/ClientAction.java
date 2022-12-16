package com.janfic.games.library.utils.gamebuilder;

public abstract class ClientAction<T extends GameState> extends GameStateChange<T>{

    private GameClient<T> client;

    public ClientAction(GameClient<T> client) {
        super(1, -1);
        this.client = client;
    }
}
