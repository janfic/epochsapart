package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.library.utils.gamebuilder.GameClient;

import java.util.*;

public class EpochsApartDriver extends Game {

    SpriteBatch batch;
    Stage stage;

    GameClient<EpochsApartGameState> client;

    public Map<String, Screen> screens;
    Screen currentScreen;
    @Override
    public void create() {
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));

        stage.getCamera().position.set(0,0,0);
        stage.getCamera().update();

        Screen mainMenu = new MainMenuScreen(this);
        Screen startMultiPlayerScreen = new WorldSimScreen(this);
        Screen startSinglePlayerScreen = new SinglePlayerScreen(this);

        screens = new HashMap<>();
        screens.put("mainMenu", mainMenu);
        screens.put("startWorldSimPrototype", startMultiPlayerScreen);
        screens.put("startSinglePlayer", startSinglePlayerScreen);

        currentScreen = mainMenu;

        /**
        client = new GameClient<>(1);
        GameServerAPI.getSingleton().setClient(client);
        GameServerAPI.getSingleton().connectToServer("localhost", 7272);
        GameServerAPI.getSingleton().start();
        GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.REQUEST_CREATE_GAME, EpochsApartGame.class.getName());
        **/

        setScreen(currentScreen);



        //stage.addActor(new HexGrid(4));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(new Color(0x231132FF));
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        super.render();

        //client.update(delta);

        //stage.act(delta);
        //stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public static void log(String string) {
        System.out.println("[CLIENT]: " + string);
    }
}
