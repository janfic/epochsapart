package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.dddserver.worldsim.HexWorld;
import com.janfic.games.dddserver.worldsim.World;
import com.janfic.games.library.ecs.components.rendering.ModelBatchComponent;
import org.lwjgl.opengl.GL20;

import java.util.Arrays;

public class WorldSimScreen implements Screen {

    ShapeRenderer renderer;
    World world;
    HexWorld hexWorld;
    Mesh mesh;
    ShaderProgram shaderProgram;

    FirstPersonCameraController controller;
    PerspectiveCamera camera;

    ModelBatch batch;
    ModelInstance instance;

    public WorldSimScreen(EpochsApartDriver game) {
        world = new World(1);
        hexWorld = new HexWorld(1, 0, 0, 1);
        renderer = new ShapeRenderer();
        mesh = hexWorld.makeMesh();
//        shaderProgram = new ShaderProgram(DefaultShader.getDefaultVertexShader(), DefaultShader.getDefaultFragmentShader());
        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/basicShader.vertex.glsl"), Gdx.files.internal("shaders/basicShader.fragment.glsl"));
        System.out.println(shaderProgram.getFragmentShaderSource());
        camera = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10, 0, 0);
        camera.lookAt(0,0,0);
        camera.near = 1f;
        camera.far = 300f;
        controller = new FirstPersonCameraController(camera);
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createSphere(1f, 1f, 1f, 30, 30,
                new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);
        instance.transform.translate(1, 0,0);
        batch = new ModelBatch();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
//        renderer.begin(ShapeRenderer.ShapeType.Line);
//        renderer.setColor(Color.WHITE);
//        //hexWorld.draw(renderer, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
//        renderer.end();

        controller.update();

        shaderProgram.bind();
        shaderProgram.setUniformMatrix("u_projViewTrans", camera.combined);
        mesh.render(shaderProgram, GL20.GL_LINES);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
