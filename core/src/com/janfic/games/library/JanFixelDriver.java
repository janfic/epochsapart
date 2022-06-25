package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	Palette aap64, blackWhite;

	DirectionalLight light;

	Texture noiseTexture;

	boolean sunSim = false;
	float time = 0;

	@Override
	public void create () {
		engine = new ECSEngine();
		batch = new SpriteBatch();
		aap64 = new Palette("AAP-64", Gdx.files.local("palettes/aap-64.gpl"));
		blackWhite = new Palette("black/white");
		for (float i = 0; i <= 1; i+= 0.1f) {
			blackWhite.addColor(new Color(i, i, i, 1f));
		}
		palettePostProcess = engine.palettePostProcess;
		ditherPostProcess = engine.ditherPostProcess;
		pixelizePostProcess = engine.pixelizePostProcess;
		light = engine.light;

		final Pixmap pixMap = new Pixmap(512, 512, Pixmap.Format.RGBA8888);

		final NoiseGenerator noiseGenerator = new NoiseGenerator();
		int size = 64;
		final Grid grid = new Grid(size);

		noiseStage(grid, noiseGenerator, 32, 1f / 2f);
		noiseStage(grid, noiseGenerator, 16, 1f / 4f);
		noiseStage(grid, noiseGenerator, 8, 1f / 8f);
		noiseStage(grid, noiseGenerator, 4, 1f / 16f);
		noiseStage(grid, noiseGenerator, 2, 1f / 32f);
		//noiseStage(grid, noiseGenerator, 1, 1f / 32f);


		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				float c = grid.get(x,y);
				if( grid.get(x,y) >= 1) System.out.println( grid.get(x,y));
				pixMap.setColor(new Color(c,c,c, 1));
				pixMap.drawPixel(x,y);
			}
		}

		noiseTexture = new Texture(pixMap);
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
		batch.begin();
		batch.draw(noiseTexture, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
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

		if(Gdx.input.isKeyPressed(Input.Keys.T)) {
			engine.camera.viewportWidth *= 0.99f;
			engine.camera.viewportHeight *= 0.99f;
			engine.camera.update();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.G)) {
			engine.camera.viewportWidth *= 1.01f;
			engine.camera.viewportHeight *= 1.01f;
			engine.camera.update();
		}

		if(!sunSim && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
			sunSim = true;
			time = 0;
		}

		if(sunSim) {
			time += Gdx.graphics.getDeltaTime();
			// 24 seconds = 24 in game hours
			float x = (float) Math.cos(Math.PI * 2 * ((time - 12) / 12));
			float y = (float) Math.sin(Math.PI * 2 * ((time - 12) / 12));
			light.setDirection(x, y, 0.8f);
			if(Math.abs(y) < 0.4) {
				light.setColor(Color.ORANGE);
			}
			else if (y > 0) {
				light.setColor(Color.WHITE);
			}
			else if( y < 0 ) {
				light.setColor(Color.LIGHT_GRAY);
			}
		}
	}

	@Override
	public void dispose () {
	}


}
