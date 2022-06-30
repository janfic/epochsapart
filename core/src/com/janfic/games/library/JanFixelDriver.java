package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;
import com.janfic.games.library.ecs.ECSEngine;
import com.janfic.games.library.ecs.components.rendering.EnvironmentComponent;
import com.janfic.games.library.graphics.shaders.postprocess.DitherPostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.Palette;
import com.janfic.games.library.graphics.shaders.postprocess.PalettePostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PixelizePostProcess;
import com.janfic.games.library.utils.ColorUtils;
import com.janfic.games.library.utils.voxel.VoxelChunk;
import com.janfic.games.library.utils.voxel.VoxelWorld;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

public class JanFixelDriver extends ApplicationAdapter {

	ECSEngine engine;

	SpriteBatch batch;

	PalettePostProcess palettePostProcess;
	DitherPostProcess ditherPostProcess;
	PixelizePostProcess pixelizePostProcess;

	DirectionalLight light;

	PerspectiveCamera camera;

	boolean sunSim = false;
	float time = 0;
	private ModelBatch modelBatch;

	VoxelWorld world;

	ModelInstance instance;

	EnvironmentComponent environmentComponent;

	@Override
	public void create () {
		engine = new ECSEngine();
		batch = new SpriteBatch();
		modelBatch = new ModelBatch();

		palettePostProcess = engine.palettePostProcess;
		ditherPostProcess = engine.ditherPostProcess;
		pixelizePostProcess = engine.pixelizePostProcess;
		light = engine.light;
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(100, 100, -100);
		camera.lookAt(0,0,0);
		camera.near = 1f;
		camera.far = 1000f;
		camera.update();

		instance = new ModelInstance(new ModelBuilder().createSphere(2,2,2, 10, 10, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
		instance.transform.setToTranslation(new Vector3(0, 32 ,0));

		int width = 32;
		int height = 32;
		int length = 32;
		Texture[] tiles = new Texture[1];
		tiles[0] = new Texture("models/tileTextures/textures.png");
		world = new VoxelWorld(tiles, (int) Math.ceil(width / (float) VoxelChunk.CHUNK_SIZE_X),(int) Math.ceil(height / (float)VoxelChunk.CHUNK_SIZE_Y),(int) Math.ceil(length / (float)VoxelChunk.CHUNK_SIZE_Z));

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height/2; y++) {
				for (int z = 0; z < length; z++) {
					world.set(x,y,z, (byte)2);
				}
			}
		}

		environmentComponent = new EnvironmentComponent();
		environmentComponent.environment = new Environment();
		environmentComponent.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		//environmentComponent.environment.add(new PointLight().set(1f, 1f, 1f, 300, 200, 200, 20000));
		light = new DirectionalShadowLight(1024, 1024, 60f, 60f, .1f, 50f);
		light.set(1, 1, 1f, 0.5f, -2f, 0.4f);
		environmentComponent.environment.add(light);
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
//		instance.transform.translate(0,Gdx.graphics.getDeltaTime() * 1,0);
//		modelBatch.begin(camera);
//		modelBatch.render(instance, environmentComponent.environment);
//		modelBatch.setCamera(camera);
//		modelBatch.render(world, environmentComponent.environment);
//		modelBatch.end();
	}

	@Override
	public void dispose () {
	}


}
