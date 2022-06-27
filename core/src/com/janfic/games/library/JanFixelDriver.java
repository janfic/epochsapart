package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;
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

	DirectionalLight light;

	boolean sunSim = false;
	float time = 0;
	private ModelBatch modelBatch;

	@Override
	public void create () {
		engine = new ECSEngine();
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();
		palettePostProcess = engine.palettePostProcess;
		ditherPostProcess = engine.ditherPostProcess;
		pixelizePostProcess = engine.pixelizePostProcess;
		light = engine.light;
	}

	private static void noiseStage(Grid grid, NoiseGenerator noiseGenerator, int radius, float modifier) {
		noiseGenerator.setRadius(radius);
		noiseGenerator.setModifier(modifier);
		noiseGenerator.setSeed(Generators.rollSeed());
		noiseGenerator.generate(grid);
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
