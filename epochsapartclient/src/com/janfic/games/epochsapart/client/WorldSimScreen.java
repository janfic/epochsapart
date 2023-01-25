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

import java.util.ArrayList;
import java.util.List;

public class WorldSimScreen implements Screen {

    ShapeRenderer renderer;
    World world;
    HexWorld hexWorld;
    Mesh mesh;
    ShaderProgram shaderProgram;

    FirstPersonCameraController controller;
    PerspectiveCamera camera;
    float radius;

    int renderType = GL20.GL_TRIANGLES;

    ModelBatch batch;
    ModelInstance instance;

    List<Renderable> renderables;

    public WorldSimScreen(EpochsApartDriver game) {
        world = new World(1);
        hexWorld = new HexWorld(15, 0, 0, 0);
        renderer = new ShapeRenderer();
        mesh = hexWorld.polyhedron.makeMesh(Color.WHITE, renderType);
        radius = 15 * 2;
//        shaderProgram = new ShaderProgram(DefaultShader.getDefaultVertexShader(), DefaultShader.getDefaultFragmentShader());
        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/basicShader.vertex.glsl"), Gdx.files.internal("shaders/basicShader.fragment.glsl"));
        camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(hexWorld.polyhedron.getCenter().cpy().add(0, 0, radius));
        camera.lookAt(hexWorld.polyhedron.getCenter());
        camera.near = 1f;
        camera.far = radius * 2;
        renderables = new ArrayList<>();
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
        if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            hexWorld.truncate();
            mesh = hexWorld.polyhedron.makeMesh(Color.WHITE, renderType);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            hexWorld.dual();
            mesh = hexWorld.polyhedron.makeMesh(Color.WHITE, renderType);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            hexWorld.sphere();
            mesh = hexWorld.polyhedron.makeMesh(Color.WHITE, renderType);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            hexWorld.reset();
            mesh = hexWorld.polyhedron.makeMesh(Color.WHITE, renderType);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            renderType = renderType == GL20.GL_LINES ? GL20.GL_TRIANGLES : GL20.GL_LINES;
            mesh = hexWorld.polyhedron.makeMesh(Color.WHITE, renderType);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.far += deltaTime;
            camera.update();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.far -= deltaTime;
            camera.update();
        }

        camera.position.set(camera.position.cpy().add(delta.scl(Math.abs(radius - (hexWorld.height / 2 + 5))* deltaTime)));
        norm = camera.position.cpy().sub(pos);
        norm.scl(radius / norm.len());
        camera.position.set(pos.cpy().add(norm));
        camera.lookAt(hexWorld.polyhedron.getCenter());
        camera.update();

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);

        shaderProgram.bind();
        shaderProgram.setUniformMatrix("u_projViewTrans", camera.combined);
        Gdx.gl.glCullFace(GL20.GL_FRONT);
        mesh.render(shaderProgram, renderType);
        //Gdx.gl.glCullFace(GL20.GL_BACK);
        //mesh.render(shaderProgram, GL20.GL_TRIANGLES);
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
