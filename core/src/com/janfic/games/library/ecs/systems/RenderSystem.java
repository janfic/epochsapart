package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.*;
import com.janfic.games.library.graphics.shaders.PixelPostProcessShader;
import com.janfic.games.library.utils.ZComparator;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RenderSystem extends EntitySystem implements EntityListener {

    private Array<Entity> renderableEntities, rendererEntities;

    private Family renderableFamily = Family.all(PositionComponent.class).one(ModelInstanceComponent.class, TextureComponent.class, TextureRegionComponent.class).get();
    private Family rendererFamily = Family.all(CameraComponent.class, SpriteBatchComponent.class, ModelBatchComponent.class, FrameBufferComponent.class).get();

    private ZComparator zComparator;

    OrthographicCamera camera;
    PixelPostProcessShader shader;
    CameraInputController camController;

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
        shader = new PixelPostProcessShader();
        shader.init();

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

            zComparator.setCameraComponent(cameraComponent);
            renderableEntities.sort(zComparator);

            if(camController == null) {
                camController = new CameraInputController(cameraComponent.camera);
                Gdx.input.setInputProcessor(camController);
            }

            frameBufferComponent.frameBuffer.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            for (Entity renderableEntity : renderableEntities) {
                PositionComponent positionComponent = Mapper.positionComponentMapper.get(renderableEntity);
                ModelInstanceComponent modelInstanceComponent = Mapper.modelInstanceComponentMapper.get(renderableEntity);
                TextureComponent textureComponent = Mapper.textureComponentMapper.get(renderableEntity);
                TextureRegionComponent textureRegionComponent = Mapper.textureRegionComponentMapper.get(renderableEntity);
                ShaderComponent shaderComponent = Mapper.shaderComponentMapper.get(renderableEntity);
                EnvironmentComponent environmentComponent = Mapper.environmentComponentMapper.get(renderableEntity);
                ShaderProgram.pedantic = false;

//                spriteBatchComponent.spriteBatch.setProjectionMatrix(cameraComponent.camera.combined);
//                spriteBatchComponent.spriteBatch.begin();
//                spriteBatchComponent.spriteBatch.enableBlending();
//
//                if(textureComponent != null) {
//                    spriteBatchComponent.spriteBatch.draw(textureComponent.texture, positionComponent.position.x, positionComponent.position.y);
//                }
//                if(textureRegionComponent != null) {
//                    spriteBatchComponent.spriteBatch.draw(textureRegionComponent.textureRegion, positionComponent.position.x, positionComponent.position.y);
//                }
//                spriteBatchComponent.spriteBatch.end();

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

            spriteBatchComponent.spriteBatch.begin();
            spriteBatchComponent.spriteBatch.setShader(shader.getProgram());
            shader.getProgram().setUniformi("u_depth_buffer", frameBufferComponent.frameBuffer.getDepthBufferHandle());
            spriteBatchComponent.spriteBatch.disableBlending();
            spriteBatchComponent.spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatchComponent.spriteBatch.draw(frameBufferComponent.frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0,0,1, 1);
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
