package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.*;
import com.janfic.games.library.graphics.shaders.PixelPostProcessShader;
import com.janfic.games.library.graphics.shaders.postprocess.PostProcess;
import com.janfic.games.library.utils.ZComparator;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RenderSystem extends EntitySystem implements EntityListener {

    private Array<Entity> renderableEntities, rendererEntities;

    private Family renderableFamily = Family.all(PositionComponent.class).one(ModelInstanceComponent.class, TextureComponent.class, TextureRegionComponent.class).get();
    private Family rendererFamily = Family.all(CameraComponent.class, SpriteBatchComponent.class, ModelBatchComponent.class, FrameBufferComponent.class).get();

    private ZComparator zComparator;

    OrthographicCamera camera;
    CameraInputController camController;

    RenderContext context;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        renderableEntities = new Array<>(engine.getEntitiesFor(renderableFamily).toArray());
        rendererEntities = new Array<>(engine.getEntitiesFor(rendererFamily).toArray());
        zComparator = new ZComparator();
        renderableEntities.sort(zComparator);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        camera.update();
        context = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        camera.update();

        for (Entity rendererEntity : rendererEntities) {
            CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(rendererEntity);
            ModelBatchComponent modelBatchComponent = Mapper.modelBatchComponentMapper.get(rendererEntity);
            SpriteBatchComponent spriteBatchComponent = Mapper.spriteBatchComponentMapper.get(rendererEntity);
            FrameBufferComponent frameBufferComponent = Mapper.frameBufferComponentMapper.get(rendererEntity);
            PostProcessorsComponent postProcessorsComponent = Mapper.postProcessComponentMapper.get(rendererEntity);

            zComparator.setCameraComponent(cameraComponent);
            renderableEntities.sort(zComparator);

            if(camController == null) {
                camController = new CameraInputController(cameraComponent.camera);
                Gdx.input.setInputProcessor(camController);
            }
            context.begin();
            frameBufferComponent.frameBuffer.begin();
            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            for (Entity renderableEntity : renderableEntities) {
                PositionComponent positionComponent = Mapper.positionComponentMapper.get(renderableEntity);
                ModelInstanceComponent modelInstanceComponent = Mapper.modelInstanceComponentMapper.get(renderableEntity);
                TextureComponent textureComponent = Mapper.textureComponentMapper.get(renderableEntity);
                TextureRegionComponent textureRegionComponent = Mapper.textureRegionComponentMapper.get(renderableEntity);
                ShaderComponent shaderComponent = Mapper.shaderComponentMapper.get(renderableEntity);
                EnvironmentComponent environmentComponent = Mapper.environmentComponentMapper.get(renderableEntity);
                ShaderProgram.pedantic = false;

                modelBatchComponent.modelBatch.begin(cameraComponent.camera);
                if(modelInstanceComponent != null && environmentComponent != null && shaderComponent != null) {
                    modelBatchComponent.modelBatch.render(modelInstanceComponent.instance, environmentComponent.environment, shaderComponent.shader);
                }
                else if (modelInstanceComponent != null && environmentComponent != null) {
                    modelBatchComponent.modelBatch.render(modelInstanceComponent.instance, environmentComponent.environment);
                }
                else if (modelInstanceComponent != null && shaderComponent != null) {
                    modelBatchComponent.modelBatch.render(modelInstanceComponent.instance, shaderComponent.shader);
                }
                else if(modelInstanceComponent != null){
                    modelBatchComponent.modelBatch.render(modelInstanceComponent.instance);
                }
                modelBatchComponent.modelBatch.end();
            }
            frameBufferComponent.frameBuffer.end();
            context.end();

            Texture colorTexture = frameBufferComponent.frameBuffer.getTextureAttachments().get(FrameBufferComponent.DIFFUSE_ATTACHMENT);
            if(postProcessorsComponent != null && postProcessorsComponent.processors != null) {
                FrameBuffer current = frameBufferComponent.frameBuffer;
                for (PostProcess processor : postProcessorsComponent.processors) {
                    processor.render(current, camera, context);
                    current = processor.getFrameBuffer();
                    colorTexture = current.getTextureAttachments().get(FrameBufferComponent.DIFFUSE_ATTACHMENT);
                }
            }

            spriteBatchComponent.spriteBatch.begin();
            spriteBatchComponent.spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatchComponent.spriteBatch.draw(colorTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0,0,1, 1);
            spriteBatchComponent.spriteBatch.end();
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        if(renderableFamily.matches(entity)) {
            renderableEntities.add(entity);
        }
        if(rendererFamily.matches(entity)) {
            rendererEntities.add(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if(renderableFamily.matches(entity)) {
            renderableEntities.removeValue(entity, true);
        }
        if(rendererFamily.matches(entity)) {
            rendererEntities.removeValue(entity, true);
        }
    }
}
