package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.janfic.games.library.ecs.ECSEngine;

public class JanFixelDriver extends ApplicationAdapter {

	ECSEngine engine;

	@Override
	public void create () {
		engine = new ECSEngine();
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		engine.update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose () {
	}


}
