package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.janfic.games.library.ecs.ECSEngine;
import com.janfic.games.library.graphics.shaders.postprocess.DitherPostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.Palette;
import com.janfic.games.library.graphics.shaders.postprocess.PalettePostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PixelizePostProcess;

public class JanFixelDriver extends ApplicationAdapter {

	ECSEngine engine;

	SpriteBatch batch;
	Palette p;

	PalettePostProcess palettePostProcess;
	DitherPostProcess ditherPostProcess;
	PixelizePostProcess pixelizePostProcess;

	@Override
	public void create () {
		engine = new ECSEngine();
		batch = new SpriteBatch();
		ditherPostProcess = new DitherPostProcess(5);
		pixelizePostProcess = new PixelizePostProcess(5);
		palettePostProcess = new PalettePostProcess(new Palette("AAP-64", Gdx.files.local("aap-64.gpl")), true);
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		engine.update(Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
			if(engine.postProcessesComponent.processors.size() == 0) {
				engine.postProcessesComponent.processors.add(palettePostProcess);
			}
			else if (engine.postProcessesComponent.processors.size() == 1 ) {
				engine.postProcessesComponent.processors.add(this.ditherPostProcess);
			}
			else if(engine.postProcessesComponent.processors.size() == 2) {
				engine.postProcessesComponent.processors.add(this.pixelizePostProcess);
			}
			else if(engine.postProcessesComponent.processors.size() == 3) {
				engine.postProcessesComponent.processors.clear();
			}
		}

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
	}

	@Override
	public void dispose () {
	}


}
