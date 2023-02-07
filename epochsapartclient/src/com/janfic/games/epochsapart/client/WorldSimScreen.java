package com.janfic.games.epochsapart.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.janfic.games.dddserver.worldsim.HexWorld;
import com.janfic.games.dddserver.worldsim.World;
import com.janfic.games.library.ecs.components.rendering.FrameBufferComponent;
import com.janfic.games.library.ecs.components.rendering.PostProcessorsComponent;
import com.janfic.games.library.graphics.shaders.postprocess.DitherPostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PixelizePostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PostProcess;
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
    SpriteBatch spriteBatch;
    ModelInstance instance;
    private Environment environment;
    private PointLight pointLight;
    private DirectionalLight directionalLight;

    private FrameBuffer buffer;
    private PostProcessorsComponent postProcessorsComponent;
    private RenderContext context;
    private OrthographicCamera orthographicCamera;


    public WorldSimScreen(EpochsApartDriver game) {
        world = new World(1);
        hexWorld = new HexWorld(15, 0, 0, 0);
        renderer = new ShapeRenderer();
        mesh = hexWorld.polyhedron.getFaces().get(0).makeMesh(renderType, hexWorld.polyhedron);

        radius = 15 * 2;
//        shaderProgram = new ShaderProgram(DefaultShader.getDefaultVertexShader(), DefaultShader.getDefaultFragmentShader());
        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/basicShader.vertex.glsl"), Gdx.files.internal("shaders/basicShader.fragment.glsl"));
        batch = new ModelBatch();
        camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(hexWorld.polyhedron.getCenter().cpy().add(0, 0, radius));
        camera.lookAt(hexWorld.polyhedron.getCenter());
        camera.near = 1f;
        camera.far = radius * 2;
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f,0.8f,0.8f,1.0f));
        pointLight = new PointLight().set(0.8f, 0.8f, 0.8f, 15,0,15, 300);
        directionalLight = new DirectionalLight().set(Color.WHITE.cpy().mul(0.5f), new Vector3(0,-1,0));
        //environment.add(pointLight);
        //.add(directionalLight);


        // Rendering
        batch = new ModelBatch();
        renderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        GLFrameBuffer.FrameBufferBuilder builder = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        builder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
        builder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);
        buffer = builder.build();
        context = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));
        orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        orthographicCamera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        orthographicCamera.update();

        postProcessorsComponent = new PostProcessorsComponent();
        postProcessorsComponent.processors = new ArrayList<>();
        postProcessorsComponent.processors.add(new DitherPostProcess(3));
        postProcessorsComponent.processors.add(new PixelizePostProcess(3));

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

        hexWorld.polyhedron.renderType = renderType;


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
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            hexWorld.dual();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            hexWorld.sphere();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            hexWorld.reset();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            renderType = renderType == GL20.GL_LINES ? GL20.GL_TRIANGLES : GL20.GL_LINES;
            hexWorld.polyhedron.setRenderType(renderType);
            hexWorld.polyhedron.dirty();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.far += deltaTime;
            camera.update();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.far -= deltaTime;
            camera.update();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.I)) {
            System.out.println(hexWorld.polyhedron.getFaces().size());
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            hexWorld.generateTerrain(1/ 8f, f -> f * 2f, 7, 1);
            hexWorld.polyhedron.dirty();
            Thread t = new Thread(()->{
                hexWorld.polyhedron.makeMeshes();
            });
            t.start();
        }


        hexWorld.polyhedron.setRenderSettings(camera);
        hexWorld.polyhedron.setRenderType(renderType);
//        directionalLight.setDirection(hexWorld.polyhedron.getCenter().cpy().sub(camera.position.cpy()).nor().rotate(Vector3.Y, 50));
        //directionalLight.setDirection(camera.up.cpy().scl(-1f).rotate(camera.up.cpy().crs(norm).nor(), 45));
        //directionalLight.setDirection(camera.up.cpy().nor());

        camera.position.set(camera.position.cpy().add(delta.scl(Math.abs(radius - (hexWorld.height / 2 + 5))* deltaTime)));
        norm = camera.position.cpy().sub(pos);
        norm.scl(radius / norm.len());
        camera.position.set(pos.cpy().add(norm));
        camera.lookAt(hexWorld.polyhedron.getCenter());
        camera.update();

        //pointLight.position.set(camera.position.cpy().add(camera.up.cpy().scl(20f)));

        context.begin();
        buffer.begin();

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_FRONT);
        Gdx.gl.glCullFace(GL20.GL_CCW);

        batch.begin(camera);
        batch.render(hexWorld.polyhedron, environment);
        batch.end();

        buffer.end();
        context.end();
        //mesh.render(shaderProgram, renderType);
        //mesh.render(shaderProgram, GL20.GL_TRIANGLES);

        Texture colorTexture = buffer.getTextureAttachments().get(FrameBufferComponent.DIFFUSE_ATTACHMENT);
        if (postProcessorsComponent != null && postProcessorsComponent.processors != null) {
            FrameBuffer current = buffer;
            for (PostProcess processor : postProcessorsComponent.processors) {
                processor.render(current, orthographicCamera, context);
                current = processor.getFrameBuffer();
                colorTexture = current.getTextureAttachments().get(FrameBufferComponent.DIFFUSE_ATTACHMENT);
            }
        }

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(orthographicCamera.combined);
        spriteBatch.draw(colorTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        spriteBatch.end();
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
