package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.dddserver.epochsapart.EpochsApartGame;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.cards.actioncards.ActionCardDeck;
import com.janfic.games.dddserver.epochsapart.entities.Inventory;
import com.janfic.games.dddserver.epochsapart.entities.Player;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.CloseSelfMiniGameStateChange;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.OpenInventoryMiniGameStateChange;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.PlayerJoinGameStateChange;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.StartManageInventoryGameStateChange;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
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
        client.getGameState().setSize(stage.getWidth(), stage.getHeight());

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        client.update(delta);
        server.update(delta);

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


        // Get mini-games and UI
        Player player = (Player) client.getGameState().getEntityByID(client.getID());
        if (player != null) {
            stage.getCamera().position.set(player.getX(), player.getY(), 0);
            stage.getCamera().update();

            Inventory inventory = player.getInventory();
            inventory.setPosition(stage.getCamera().position.x -stage.getWidth()/2, stage.getCamera().position.y -stage.getHeight()/2);
            //inventory.setZIndex(1);

            if(!client.getGameState().getMiniGamesForHexEntity(client.getID()).isEmpty()) {
                List<EpochsApartMiniGame> miniGames = client.getGameState().getMiniGamesForHexEntity(client.getID());
                int z = 2;
                for (EpochsApartMiniGame miniGame : miniGames) {
                    if(miniGame.getGameState().getStage() != stage) {
                        client.getGameState().addActor(miniGame.getGameState());
                    }
                    miniGame.getGameState().setPosition(stage.getCamera().position.x -stage.getWidth()/2, stage.getCamera().position.y -stage.getHeight()/2);
                    //miniGame.getGameState().setZIndex(z++);
                }
            }

            if(inventory.getStage() != stage) {
                client.getGameState().addActor(inventory);
                inventory.getActionCardDeck().setVisible(true);
            }
        }

        if(player != null && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && client.getGameState().getMiniGamesForHexEntity(client.getID()).isEmpty()) {
            Inventory inventory = player.getInventory();
            ActionCardDeck deck = inventory.getActionCardDeck();
            GameStateChange<EpochsApartGameState> stateChange = deck.getActiveCard().performAction(client, client.getGameState());
            if(stateChange != null)
                GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, json.toJson(stateChange));
        }

        if(player != null && Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            if(client.getGameState().getMiniGamesForHexEntity(client.getID()).isEmpty())
                GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, json.toJson(new OpenInventoryMiniGameStateChange(player.getID())));
            else {
                GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, json.toJson(new CloseSelfMiniGameStateChange(player.getID())));
            }
        }

        if(player != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if(client.getGameState().getMiniGamesForHexEntity(client.getID()).isEmpty()){
                GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, json.toJson(new StartManageInventoryGameStateChange(player.getID())));
            }
            else {
                GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, json.toJson(new CloseSelfMiniGameStateChange(player.getID())));
            }
        }

        stage.act(delta);
        stage.draw();
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
    public void dispose() {}
}
