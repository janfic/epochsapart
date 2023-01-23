package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.janfic.games.dddserver.worldsim.HexWorld;
import com.janfic.games.dddserver.worldsim.World;
import org.lwjgl.opengl.GL20;

public class WorldSimScreen implements Screen {

    ShapeRenderer renderer;
    World world;
    HexWorld hexWorld;
    Mesh mesh0, mesh1, mesh2;
    ShaderProgram shaderProgram;

    FirstPersonCameraController controller;
    PerspectiveCamera camera;

    ModelBatch batch;
    ModelInstance instance;

    public WorldSimScreen(EpochsApartDriver game) {
        world = new World(1);
        hexWorld = new HexWorld(5, 0, 0, 1);
        renderer = new ShapeRenderer();
        mesh0 = hexWorld.polyhedron0.makeMesh();
        mesh1 = hexWorld.polyhedron1.makeMesh();
        mesh2 = hexWorld.polyhedron2.makeMesh();
//        shaderProgram = new ShaderProgram(DefaultShader.getDefaultVertexShader(), DefaultShader.getDefaultFragmentShader());
        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/basicShader.vertex.glsl"), Gdx.files.internal("shaders/basicShader.fragment.glsl"));
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

        controller.update();
        hexWorld.polyhedron0.addTransform(new Matrix4().rotate(hexWorld.polyhedron0.getUp().cpy().sub(hexWorld.polyhedron0.getCenter()), 30 * delta));
        hexWorld.polyhedron1.addTransform(new Matrix4().rotate(hexWorld.polyhedron1.getUp().cpy().sub(hexWorld.polyhedron1.getCenter()), 30 * delta));
        hexWorld.polyhedron2.addTransform(new Matrix4().rotate(hexWorld.polyhedron2.getUp().cpy().sub(hexWorld.polyhedron2.getCenter()), 30 * delta));
        mesh0 = hexWorld.polyhedron0.makeMesh();
        mesh1 = hexWorld.polyhedron1.makeMesh();
        mesh2 = hexWorld.polyhedron2.makeMesh();

        shaderProgram.bind();
        shaderProgram.setUniformMatrix("u_projViewTrans", camera.combined);
        mesh0.render(shaderProgram, GL20.GL_LINES);
        mesh1.render(shaderProgram, GL20.GL_LINES);
        mesh2.render(shaderProgram, GL20.GL_LINES);
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
