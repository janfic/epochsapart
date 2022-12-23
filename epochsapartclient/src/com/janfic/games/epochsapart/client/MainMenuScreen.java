package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import sun.tools.jconsole.Tab;

public class MainMenuScreen implements Screen {

    EpochsApartDriver game;

    Stage stage;

    TextButton startSinglePlayer, startMultiPlayer;

    Skin defaultSkin;

    public MainMenuScreen(EpochsApartDriver game) {
        this.game = game;
        defaultSkin = new Skin(Gdx.files.internal("assets/ui/skins/default/skin/uiskin.json"));
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        startSinglePlayer = new TextButton("Singleplayer", defaultSkin);
        startMultiPlayer = new TextButton("Mulitplayer", defaultSkin);
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(4).space(5).minWidth(100);

        table.add(new Label("Epochs Apart", defaultSkin)).row();
        table.add(startSinglePlayer).row();
        table.add(startMultiPlayer).row();

        startSinglePlayer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EpochsApartDriver.log("Pressed Singleplayer Button");
                game.setScreen(game.screens.get("startSinglePlayer"));
            }
        });

        stage.addActor(table);
    }

    @Override
    public void show() {
        EpochsApartDriver.log("Showing Main Menu");
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        EpochsApartDriver.log("Main Menu Paused");
    }

    @Override
    public void resume() {
        EpochsApartDriver.log("Main Menu Resumed");
    }

    @Override
    public void hide() {
        EpochsApartDriver.log("Main Menu Hidden");
    }

    @Override
    public void dispose() {

    }
}
