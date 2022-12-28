package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.janfic.games.dddserver.epochsapart.EpochsApartGame;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.cards.ActionDeck;
import com.janfic.games.dddserver.epochsapart.cards.Deck;
import com.janfic.games.dddserver.epochsapart.entities.Inventory;
import com.janfic.games.dddserver.epochsapart.entities.Player;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.PlayerJoinGameStateChange;
import com.janfic.games.library.utils.ScrollStage;
import com.janfic.games.library.utils.gamebuilder.*;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerScreen implements Screen {

    public static float attacks;
    EpochsApartDriver game;
    ScrollStage stage;
    GameServer server;
    GameClient<EpochsApartGameState> client;
    Skin defaultSkin;
    Json json = new Json();

    public SinglePlayerScreen(EpochsApartDriver game) {
        this.game = game;
        defaultSkin = new Skin(Gdx.files.internal("assets/ui/skins/default/skin/uiskin.json"));
        stage = new ScrollStage(new FitViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().update();
    }

    @Override
    public void show() {
        EpochsApartDriver.log("Showing SinglePlayer Screen");
        server = new GameServer();
        server.startServer(7272);

        client = new GameClient<>(new EpochsApartGameState(), 0);

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

        stage.addActor(client.getGameState());
        stage.getCamera().position.set(0, 0, 0);
        stage.getCamera().update();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        client.update(delta);
        server.update(delta);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            server.stopServer();
            GameClientAPI.getSingleton().dispose();
            GameServerAPI.getSingleton().stopProcesses();
            Gdx.app.exit();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.REQUEST_FULL_GAME_STATE, "");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            PlayerJoinGameStateChange playerJoinGameStateChange = new PlayerJoinGameStateChange(client.getID());
            GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, new Json().toJson(playerJoinGameStateChange));
        }

        Player player = (Player) client.getGameState().getEntityByID(client.getID());
        if (player != null) {
            Inventory inventory = player.getInventory();
            Deck deck = inventory.getActionCardDeck();
            if (deck.getName().equals("Action") && deck.getStage() == null) {
                stage.addActor(deck);
                deck.setBounds(-stage.getWidth() / 4f, -stage.getHeight() / 2f, stage.getWidth() / 2, 100);
                deck.setOrigin(Align.center);
            }
        }

        if(player != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Inventory inventory = player.getInventory();
            ActionDeck deck = inventory.getActionCardDeck();
            GameStateChange<EpochsApartGameState> stateChange = deck.getActiveCard().performAction(client, client.getGameState());
            if(stateChange != null)
                GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, json.toJson(stateChange));
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
