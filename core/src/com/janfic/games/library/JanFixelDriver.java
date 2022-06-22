package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.ECSEngine;
import com.janfic.games.library.graphics.shaders.postprocess.DitherPostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.Palette;
import com.janfic.games.library.graphics.shaders.postprocess.PalettePostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PixelizePostProcess;
import com.janfic.games.library.utils.ColorUtils;

public class JanFixelDriver extends ApplicationAdapter {

	ECSEngine engine;

	SpriteBatch batch;

	PalettePostProcess palettePostProcess;
	DitherPostProcess ditherPostProcess;
	PixelizePostProcess pixelizePostProcess;
	Palette aap64, blackWhite;

	DirectionalLight light;

	@Override
	public void create () {
		engine = new ECSEngine();
		batch = new SpriteBatch();
		aap64 = new Palette("AAP-64", Gdx.files.local("aap-64.gpl"));
		blackWhite = new Palette("black/white");
		for (float i = 0; i <= 1; i+= 0.1f) {
			blackWhite.addColor(new Color(i, i, i, 1f));
		}
		palettePostProcess = engine.palettePostProcess;
		ditherPostProcess = engine.ditherPostProcess;
		pixelizePostProcess = engine.pixelizePostProcess;
		light = engine.light;
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		engine.update(Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			this.ditherPostProcess.pixelSize += 2;
			this.pixelizePostProcess.pixelSize += 2;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			this.ditherPostProcess.pixelSize -= 2;
			this.pixelizePostProcess.pixelSize -= 2;
			if(this.ditherPostProcess.pixelSize <= 0 || this.pixelizePostProcess.pixelSize <= 0) {
				this.ditherPostProcess.pixelSize = 1;
				this.pixelizePostProcess.pixelSize = 1;
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
			palettePostProcess.useHSL = !palettePostProcess.useHSL;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			palettePostProcess.palette = palettePostProcess.palette == blackWhite ? aap64 : blackWhite;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			light.setDirection(light.direction.x, light.direction.y - 0.01f, light.direction.z);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			light.setDirection(light.direction.x, light.direction.y + 0.01f, light.direction.z);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			light.setDirection(light.direction.x + 0.01f, light.direction.y, light.direction.z);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			light.setDirection(light.direction.x - 0.01f, light.direction.y, light.direction.z);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			light.setDirection(light.direction.x , light.direction.y, light.direction.z + 0.01f);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			light.setDirection(light.direction.x , light.direction.y, light.direction.z - 0.01f);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
			light.setDirection(0, light.direction.y, light.direction.z);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.X)) {
			light.setDirection(light.direction.x , 0, light.direction.z );
		}
		if (Gdx.input.isKeyPressed(Input.Keys.C)) {
			light.setDirection(light.direction.x , light.direction.y, 0 );
		}
	}

	@Override
	public void dispose () {
	}


}
