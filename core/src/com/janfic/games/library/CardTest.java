package com.janfic.games.library;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.library.utils.cards.CardGameActor;
import com.janfic.games.library.utils.cards.standard.*;

import java.util.ArrayList;
import java.util.List;

public class CardTest implements Screen {

    WarCardGame war;

    List<CardGameActor> players = new ArrayList<>();

    public CardTest() {
        CardGameActor player1 = new CardGameActor("Player 1");
        CardGameActor player2 = new CardGameActor("Player 2");
        players.add(player1);
        players.add(player2);

        war = new WarCardGame(players);
        war.setup();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        war.update();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
