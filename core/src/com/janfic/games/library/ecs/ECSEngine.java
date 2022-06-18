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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.components.*;
import com.janfic.games.library.ecs.systems.RenderSystem;
import com.janfic.games.library.graphics.shaders.PixelShader;

public class ECSEngine extends Engine {
    public ECSEngine() {

        // Systems
        RenderSystem renderSystem = new RenderSystem();
        addEntityListener(renderSystem);
        addSystem(renderSystem);

        // Entities
        Entity rendererEntity = createEntity();
        CameraComponent cameraComponent = new CameraComponent();
        cameraComponent.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraComponent.camera.position.set(2,2,2);
        cameraComponent.camera.lookAt(0,0,0);
        cameraComponent.camera.near = 1;
        cameraComponent.camera.far = 300f;
        cameraComponent.camera.update();

        SpriteBatchComponent spriteBatchComponent = new SpriteBatchComponent();
        spriteBatchComponent.spriteBatch = new SpriteBatch();

        ModelBatchComponent modelBatchComponent = new ModelBatchComponent();
        modelBatchComponent.modelBatch = new ModelBatch();

        FrameBufferComponent frameBufferComponent = new FrameBufferComponent();
        frameBufferComponent.frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        rendererEntity.add(cameraComponent);
        rendererEntity.add(spriteBatchComponent);
        rendererEntity.add(modelBatchComponent);
        rendererEntity.add(frameBufferComponent);

        // Sphere Entity
        Entity sphere = new Entity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.position = new Vector3();

        ModelComponent modelComponent = new ModelComponent();
        //modelComponent.model = new ModelBuilder().createSphere(1, 1,1, 100, 100, new Material(), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked);
        modelComponent.model = new ModelBuilder().createBox(1, 1,1,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        ModelInstanceComponent modelInstanceComponent = new ModelInstanceComponent();
        modelInstanceComponent.instance = new ModelInstance(modelComponent.model);

        EnvironmentComponent environmentComponent = new EnvironmentComponent();
        environmentComponent.environment = new Environment();
        environmentComponent.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        //environmentComponent.environment.add(new PointLight().set(Color.GREEN, new Vector3(2f,3,0), 10f));

        ShaderComponent shaderComponent = new ShaderComponent();
        shaderComponent.shader = new PixelShader();
        shaderComponent.shader.init();

        TextureComponent textureComponent = new TextureComponent();
        textureComponent.texture = new Texture("badlogic.jpg");

        sphere.add(positionComponent);
        sphere.add(modelComponent);
        sphere.add(modelInstanceComponent);
        sphere.add(environmentComponent);
        //sphere.add(textureComponent);
        //sphere.add(shaderComponent);

        addEntity(rendererEntity);
        addEntity(sphere);
    }
}
