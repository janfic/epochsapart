package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.janfic.games.library.ecs.ECSEngine;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.*;
import com.janfic.games.library.graphics.shaders.BorderShader;
import com.janfic.games.library.graphics.shaders.postprocess.PostProcess;
import com.janfic.games.library.utils.ZComparator;

import javax.swing.border.Border;

public class GameRenderSystem extends EntitySystem {

    private ImmutableArray<Entity> renderableEntities, rendererEntities;

    private Family renderableFamily = Family.all(PositionComponent.class).one(RenderableProviderComponent.class, ModelInstanceComponent.class, TextureComponent.class, TextureRegionComponent.class).get();
    private Family rendererFamily = Family.all(
            CameraComponent.class,
            SpriteBatchComponent.class,
            ModelBatchComponent.class,
            FrameBufferComponent.class,
            EnvironmentComponent.class).get();

    OrthographicCamera camera;

    RenderContext context;

    ModelBatch shadowBatch;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        renderableEntities = engine.getEntitiesFor(renderableFamily);
        rendererEntities = engine.getEntitiesFor(rendererFamily);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
        camera.update();
        context = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));
        shadowBatch = new ModelBatch(new DepthShaderProvider());
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
            EnvironmentComponent environmentComponent = Mapper.environmentComponentMapper.get(rendererEntity);

            //zComparator.setCameraComponent(cameraComponent);

            context.begin();
            frameBufferComponent.frameBuffer.begin();
            Gdx.gl.glEnable(GL30.GL_BLEND);
            Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
            Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            ECSEngine e = (ECSEngine) getEngine();
            DirectionalShadowLight shadowLight = e.light;

            modelBatchComponent.modelBatch.begin(cameraComponent.camera);
            Gdx.gl30.glEnable(GL30.GL_DEPTH_TEST);
            Gdx.gl30.glClearColor(0, 0, 0, 1);
            Gdx.gl30.glEnable(GL30.GL_CULL_FACE);
            Gdx.gl30.glCullFace(GL30.GL_BACK);
            Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
            for (Entity renderableEntity : renderableEntities) {
                PositionComponent positionComponent = Mapper.positionComponentMapper.get(renderableEntity);
                ModelInstanceComponent modelInstanceComponent = Mapper.modelInstanceComponentMapper.get(renderableEntity);
                RenderableProviderComponent renderableProviderComponent = Mapper.renderableProviderComponentMapper.get(renderableEntity);
                TextureComponent textureComponent = Mapper.textureComponentMapper.get(renderableEntity);
                TextureRegionComponent textureRegionComponent = Mapper.textureRegionComponentMapper.get(renderableEntity);
                ShaderComponent shaderComponent = Mapper.shaderComponentMapper.get(renderableEntity);
                ShaderProgram.pedantic = false;

                modelBatchComponent.modelBatch.setCamera(cameraComponent.camera);

                RenderableProvider provider = null;
                if(renderableProviderComponent != null) {
                    provider = renderableProviderComponent.renderableProvider;
                }
                else if (modelInstanceComponent != null) {
                    provider = modelInstanceComponent.instance;
                }
                if (provider != null && environmentComponent != null && shaderComponent != null) {
                    if(shaderComponent.shader instanceof BorderShader) {
                        float scale = 1 + ((cameraComponent.camera.viewportWidth * 80 ) / Gdx.graphics.getWidth()) / 10;
                        modelInstanceComponent.instance.transform.scale(scale,scale,scale);
                        modelBatchComponent.modelBatch.render( modelInstanceComponent.instance, environmentComponent.environment, shaderComponent.shader);
                        modelBatchComponent.modelBatch.flush();
                        Gdx.gl30.glDisable(GL30.GL_DEPTH_TEST);
                        modelInstanceComponent.instance.transform.scale(1 / scale,1 / scale,1 / scale);
                    }

                    modelBatchComponent.modelBatch.render( modelInstanceComponent.instance, environmentComponent.environment);
                    if(shaderComponent.shader instanceof BorderShader) {
                        modelBatchComponent.modelBatch.flush();
                        Gdx.gl30.glEnable(GL30.GL_DEPTH_TEST);
                    }
                } else if (provider != null && environmentComponent != null) {
                    modelBatchComponent.modelBatch.render(provider, environmentComponent.environment);
                } else if (provider != null && shaderComponent != null) {
                    modelBatchComponent.modelBatch.render(provider, shaderComponent.shader);
                } else if (provider != null) {
                    modelBatchComponent.modelBatch.render(provider);
                }
            }
            modelBatchComponent.modelBatch.end();

            frameBufferComponent.frameBuffer.end();
            context.end();

            Texture colorTexture = frameBufferComponent.frameBuffer.getTextureAttachments().get(FrameBufferComponent.DIFFUSE_ATTACHMENT);
            if (postProcessorsComponent != null && postProcessorsComponent.processors != null) {
                FrameBuffer current = frameBufferComponent.frameBuffer;
                for (PostProcess processor : postProcessorsComponent.processors) {
                    processor.render(current, camera, context);
                    current = processor.getFrameBuffer();
                    colorTexture = current.getTextureAttachments().get(FrameBufferComponent.DIFFUSE_ATTACHMENT);
                }
            }

            spriteBatchComponent.spriteBatch.begin();
            spriteBatchComponent.spriteBatch.setProjectionMatrix(camera.combined);
            spriteBatchComponent.spriteBatch.draw(colorTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
            spriteBatchComponent.spriteBatch.end();
        }
    }
}
