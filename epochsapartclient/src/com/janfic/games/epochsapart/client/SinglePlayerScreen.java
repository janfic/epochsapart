package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.dddserver.epochsapart.EpochsApartGame;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.PlayerJoinGameStateChange;
import com.janfic.games.dddserver.epochsapart.world.HexGrid;
import com.janfic.games.library.utils.gamebuilder.*;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerScreen implements Screen {

    EpochsApartDriver game;

    Stage stage;

    GameServer server;
    GameClient<EpochsApartGameState> client;

    Skin defaultSkin;

    public SinglePlayerScreen(EpochsApartDriver game) {
        this.game = game;
        defaultSkin = new Skin(Gdx.files.internal("assets/ui/skins/default/skin/uiskin.json"));
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));
        stage.getCamera().position.set(0,0,0);
        stage.getCamera().update();

    }

    @Override
    public void show() {
        EpochsApartDriver.log("Showing SinglePlayer Screen");
        server = new GameServer();
        server.startServer(7272);

        client = new GameClient<>( new EpochsApartGameState(), 0);

        GameServerAPI.getSingleton().setClient(client);
        GameServerAPI.getSingleton().connectToServer("localhost", 7272);
        GameServerAPI.getSingleton().start();
        GameServerAPI.getSingleton().waitForResponse();
        client.update(0);
        List<String> paramsList = new ArrayList<>();
        paramsList.add(EpochsApartGame.class.getName());
        paramsList.add("4");
        paramsList.add("" + client.getID());
        GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.REQUEST_CREATE_GAME, new Json().toJson(paramsList));
        GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.REQUEST_JOIN_ROOM, "0");
        Gdx.input.setInputProcessor(stage);

        stage.addActor(client.getGameState());
        stage.getCamera().position.set(0,0,0);
        stage.getCamera().update();
    }

    @Override
    public void render(float delta) {
        client.update(delta);
        server.update(delta);

        stage.act(delta);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            server.stopServer();
            GameClientAPI.getSingleton().dispose();
            GameServerAPI.getSingleton().stopProcesses();
            Gdx.app.exit();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.REQUEST_FULL_GAME_STATE, "");
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            PlayerJoinGameStateChange playerJoinGameStateChange = new PlayerJoinGameStateChange(client.getID());
            GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, new Json().toJson(playerJoinGameStateChange));
        }
    }

    @Override
    public void resize(int width, int height) {
        EpochsApartDriver.log("SinglePlayer Screen Resized");
    }

    @Override
    public void pause() {
        EpochsApartDriver.log("SinglePlayer Screen Paused");
    }

    @Override
    public void resume() {
        EpochsApartDriver.log("Resume SinglePlayer Screen");
    }

    @Override
    public void hide() {
        EpochsApartDriver.log("SinglePlayer Screen Hidden");
    }

    @Override
    public void dispose() {

    }
}
