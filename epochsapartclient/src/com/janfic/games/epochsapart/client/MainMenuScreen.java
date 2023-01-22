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

public class MainMenuScreen implements Screen {

    EpochsApartDriver game;

    Stage stage;

    TextButton startLFPrototype, startWorldSimPrototype;

    Skin defaultSkin;

    public MainMenuScreen(EpochsApartDriver game) {
        this.game = game;
        defaultSkin = new Skin(Gdx.files.internal("skins/epochsapart/epochsapart.json"));
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        startLFPrototype = new TextButton("Low-Fidelity Prototype", defaultSkin);
        startWorldSimPrototype = new TextButton("World Sim Prototype", defaultSkin);
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(4).space(5).minWidth(100);

        table.add(new Label("Epochs Apart", defaultSkin)).row();
        table.add(startLFPrototype).row();
        table.add(startWorldSimPrototype).row();

        startLFPrototype.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EpochsApartDriver.log("Pressed Singleplayer Button");
                game.setScreen(game.screens.get("startSinglePlayer"));
            }
        });
        startWorldSimPrototype.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EpochsApartDriver.log("Pressed Singleplayer Button");
                game.setScreen(game.screens.get("startWorldSimPrototype"));
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
