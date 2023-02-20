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
import com.janfic.games.dddserver.worldsim.tasks.MakeWorldTask;
import com.janfic.games.library.ecs.components.rendering.FrameBufferComponent;
import com.janfic.games.library.ecs.components.rendering.PostProcessorsComponent;
import com.janfic.games.library.graphics.shaders.postprocess.DitherPostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PixelizePostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PostProcess;
import com.janfic.games.library.utils.ColorRamp;
import com.janfic.games.library.utils.multithreading.OngoingTask;
import com.janfic.games.library.utils.multithreading.TaskManager;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;

public class WorldSimScreen implements Screen {

    ShapeRenderer renderer;
    HexWorld hexWorld;
    ShaderProgram shaderProgram;

    PerspectiveCamera camera;
    float radius;

    int renderType = GL20.GL_TRIANGLES;

    ModelBatch batch;
    SpriteBatch spriteBatch;
    private Environment environment;

    private FrameBuffer buffer;
    private PostProcessorsComponent postProcessorsComponent;
    private RenderContext context;
    private OrthographicCamera orthographicCamera;

    public WorldSimScreen(EpochsApartDriver game) {
        renderer = new ShapeRenderer();
        radius = 15 * 2;

        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/basicShader.vertex.glsl"), Gdx.files.internal("shaders/basicShader.fragment.glsl"));
        camera = new PerspectiveCamera(30, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.5f;
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f,0.8f,0.8f,1.0f));

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

    MakeWorldTask worldTask;

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
        TaskManager taskManager = TaskManager.getSingleton();
        hexWorld = new HexWorld(30, 0, 0, 0);
        ColorRamp ramp = new ColorRamp();
        float max = 2;
        float steps = 20;
        ramp.addColor(0 * max, Color.NAVY);
        ramp.addColor(0.25f * max, Color.BLUE);
        ramp.addColor(0.49f * max, Color.SKY);
        ramp.addColor(0.50f * max, Color.YELLOW);
        ramp.addColor(0.55f * max, Color.FOREST);
        ramp.addColor(0.70f * max, Color.OLIVE);
        ramp.addColor(0.85f * max, Color.SLATE);
        ramp.addColor(0.95f * max, Color.WHITE);
        worldTask = new MakeWorldTask(hexWorld, ramp, (int) steps, max, 8);
        taskManager.addTask(worldTask);
    }

    @Override
    public void render(float deltaTime) {

        TaskManager.getSingleton().update();

        if(!worldTask.isComplete()) return;

        Vector3 pos = hexWorld.polyhedron.getCenter().cpy();
        Vector3 norm = camera.position.cpy().sub(pos);
        Vector3 delta = new Vector3();

        processInput(deltaTime);
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


        camera.position.set(camera.position.cpy().add(delta.scl(Math.abs(radius - (hexWorld.height / 2 + 5))* deltaTime)));
        norm = camera.position.cpy().sub(pos);
        norm.scl(radius / norm.len());
        camera.position.set(pos.cpy().add(norm));
        camera.lookAt(hexWorld.polyhedron.getCenter());
        camera.far = camera.position.dst(hexWorld.polyhedron.getCenter()) + radius / 8;
        camera.update();

        float dst = camera.position.dst(hexWorld.polyhedron.getCenter());
        hexWorld.getPolyhedronFromDistance(dst).setRenderSettings(camera);
        hexWorld.getPolyhedronFromDistance(dst).setRenderType(renderType);

        //Rendering
        context.begin();
        buffer.begin();

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_FRONT);
        Gdx.gl.glCullFace(GL20.GL_CCW);

        batch.begin(camera);
        batch.render(hexWorld.getPolyhedronFromDistance(camera.position.dst(hexWorld.polyhedron.getCenter())), environment);
        batch.end();

        buffer.end();
        context.end();

        // Post Processing Shaders
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

    public void processInput(float deltaTime) {
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            radius += 10 * deltaTime;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            radius -= 10 * deltaTime;
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
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.far += deltaTime;
            camera.update();
        }
        if(Gdx.input.isKeyPressed(Input.Keys.X)) {
            camera.far -= deltaTime;
            camera.update();
        }
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
