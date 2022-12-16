package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ScreenUtils;
import com.janfic.games.library.ecs.Isometric2DEngine;
import com.janfic.games.library.utils.gamebuilder.*;

import java.util.Random;

public class JanFixelDriver extends ApplicationAdapter {

	Isometric2DEngine engine;
	SpriteBatch batch;

	GameServer server;
	GameClient client;

	@Override
	public void create () {
		engine = new Isometric2DEngine();
		batch = new SpriteBatch();

		server = new GameServer();
		server.startServer(7272);

		client = new GameClient();

		GameServerAPI.getSingleton().setClient(client);
		GameServerAPI.getSingleton().connectToServer("localhost", 7272);
		GameServerAPI.getSingleton().start();
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ScreenUtils.clear(new Color(0x231132FF));
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin();
		batch.end();

		server.update(delta);
		client.update(delta);

		//engine.update(Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
			GameClientAPI.getSingleton().toggleListening();
		}
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			GameServerAPI.getSingleton().sendMessage(new GameMessage(
					GameMessage.GameMessageType.REQUEST_CREATE_GAME,
					ChatGame.class.getName(),
					GameServerAPI.getSingleton().getClientID(), 0));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			GameServerAPI.getSingleton().sendMessage(new GameMessage(
					GameMessage.GameMessageType.REQUEST_ROOMS_INFO,
					"",
					GameServerAPI.getSingleton().getClientID(), 0));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
			GameServerAPI.getSingleton().sendMessage(new GameMessage(
					GameMessage.GameMessageType.REQUEST_JOIN_ROOM,
					"0",
					GameServerAPI.getSingleton().getClientID(), 0));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
			GameServerAPI.getSingleton().sendMessage(new GameMessage(
					GameMessage.GameMessageType.REQUEST_LEAVE_GAME,
					"",
					GameServerAPI.getSingleton().getClientID(), 0));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
			GameServerAPI.getSingleton().sendMessage(new GameMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE,
					new Json().toJson(new ChatGame.PostChat("Test Chat", GameServerAPI.getSingleton().getClientID(), 0)),
					GameServerAPI.getSingleton().getClientID(), 0));
		}
	}

	@Override
	public void dispose () {
	}
}
