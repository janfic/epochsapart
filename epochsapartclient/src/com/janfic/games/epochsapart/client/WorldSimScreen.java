package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.janfic.games.dddserver.worldsim.HexWorld;
import com.janfic.games.dddserver.worldsim.Vertex;
import com.janfic.games.dddserver.worldsim.World;
import org.graalvm.compiler.loop.MathUtil;
import org.lwjgl.opengl.GL20;

public class WorldSimScreen implements Screen {

    ShapeRenderer renderer;
    World world;
    HexWorld hexWorld;
    Mesh mesh;
    ShaderProgram shaderProgram;

    FirstPersonCameraController controller;
    PerspectiveCamera camera;
    float radius;

    ModelBatch batch;
    ModelInstance instance;

    public WorldSimScreen(EpochsApartDriver game) {
        world = new World(1);
        hexWorld = new HexWorld(15, 0, 0, 4);
        renderer = new ShapeRenderer();
        mesh = hexWorld.polyhedron.makeMesh(Color.WHITE, GL20.GL_LINES);
        radius = 20;
//        shaderProgram = new ShaderProgram(DefaultShader.getDefaultVertexShader(), DefaultShader.getDefaultFragmentShader());
        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/basicShader.vertex.glsl"), Gdx.files.internal("shaders/basicShader.fragment.glsl"));
        camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(hexWorld.polyhedron.getCenter().cpy().add(0, 0, radius));
        camera.lookAt(hexWorld.polyhedron.getCenter());
        camera.near = 1f;
        camera.far = 300f;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float deltaTime) {
        Vector3 pos = hexWorld.polyhedron.getCenter().cpy();
        Vector3 norm = camera.position.cpy().sub(pos);
        Vector3 delta = new Vector3();

        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            radius += 10 * deltaTime;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            radius -= 10 * deltaTime;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            delta.add(camera.up.cpy().nor());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            delta.sub(camera.up.cpy().nor());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            delta.sub(camera.up.cpy().crs(norm).nor());
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            delta.add(camera.up.cpy().crs(norm).nor());
        }


        camera.position.set(camera.position.cpy().add(delta.scl(deltaTime)));
        norm = camera.position.cpy().sub(pos);
        norm.scl(radius / norm.len());
        camera.position.set(pos.cpy().add(norm));
        camera.lookAt(hexWorld.polyhedron.getCenter());
        camera.update();

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
