package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.components.*;
import com.janfic.games.library.ecs.systems.RenderSystem;
import com.janfic.games.library.graphics.shaders.postprocess.DitherPostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PixelizePostProcess;

import java.util.ArrayList;

public class ECSEngine extends Engine {
    public ECSEngine() {

        // Systems
        RenderSystem renderSystem = new RenderSystem();
        addEntityListener(renderSystem);
        addSystem(renderSystem);

        // Entities
        Entity rendererEntity = createEntity();
        CameraComponent cameraComponent = new CameraComponent();
        cameraComponent.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraComponent.camera.position.set(100,100,400);
        cameraComponent.camera.lookAt(0,0,0);
        cameraComponent.camera.near = 1;
        cameraComponent.camera.far = 1000f;
        cameraComponent.camera.update();

        SpriteBatchComponent spriteBatchComponent = new SpriteBatchComponent();
        spriteBatchComponent.spriteBatch = new SpriteBatch();

        ModelBatchComponent modelBatchComponent = new ModelBatchComponent();
        modelBatchComponent.modelBatch = new ModelBatch();

        GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
        frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);
        FrameBufferComponent frameBufferComponent = new FrameBufferComponent();
        frameBufferComponent.frameBuffer = frameBufferBuilder.build();

        PostProcessorsComponent postProcessesComponent = new PostProcessorsComponent();
        postProcessesComponent.processors = new ArrayList<>();
        postProcessesComponent.processors.add(new DitherPostProcess(5));
        postProcessesComponent.processors.add(new PixelizePostProcess(5));

        rendererEntity.add(cameraComponent);
        rendererEntity.add(spriteBatchComponent);
        rendererEntity.add(modelBatchComponent);
        rendererEntity.add(frameBufferComponent);
        rendererEntity.add(postProcessesComponent);

        // Sphere Entity
        Entity sphere = new Entity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.position = new Vector3();

        ModelComponent modelComponent = new ModelComponent();
        //modelComponent.model = new ModelBuilder().createSphere(1, 1,1, 100, 100, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked);
        modelComponent.model = new ModelBuilder().createBox(200, 200,200,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        ModelInstanceComponent modelInstanceComponent = new ModelInstanceComponent();
        modelInstanceComponent.instance = new ModelInstance(modelComponent.model);

        EnvironmentComponent environmentComponent = new EnvironmentComponent();
        environmentComponent.environment = new Environment();
        environmentComponent.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environmentComponent.environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -0.1f, -0.8f, -0.2f));

        TextureComponent textureComponent = new TextureComponent();
        textureComponent.texture = new Texture("badlogic.jpg");

        sphere.add(positionComponent);
        sphere.add(modelComponent);
        sphere.add(modelInstanceComponent);
        sphere.add(environmentComponent);

        addEntity(rendererEntity);
        addEntity(sphere);
    }
}
