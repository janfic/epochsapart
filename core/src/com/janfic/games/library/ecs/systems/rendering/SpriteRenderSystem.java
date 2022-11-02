package com.janfic.games.library.ecs.systems.rendering;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.*;
import com.janfic.games.library.graphics.shaders.postprocess.PostProcess;
import com.janfic.games.library.utils.spritestack.SpriteStack;

import java.util.Comparator;

public class SpriteRenderSystem extends SortedIteratingSystem {
    private ImmutableArray<Entity> rendererEntities;
    RenderContext context;
    OrthographicCamera camera;



    private static final Family rendererFamily = Family.all(CameraComponent.class, SpriteBatchComponent.class).get(),
            renderableFamily = Family.all(PositionComponent.class).one(TextureRegionComponent.class, SpriteStackComponent.class).exclude(InvisibleComponent.class).get();

    public SpriteRenderSystem(Comparator<Entity> sorter) {
        super(renderableFamily, sorter);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        rendererEntities = engine.getEntitiesFor(rendererFamily);
        context = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
        camera.update();
        shader = new ShaderProgram(Gdx.files.local("shaders/isometricShader.vertex.glsl"), Gdx.files.local("shaders/isometricShader.fragment.glsl"));
    }

    SpriteBatch batch;
    ShaderProgram shader;
    WorldToScreenTransformComponent worldToScreenTransformComponent;

    @Override
    public void update(float deltaTime) {
        // Get Renderer ( Batch )
        if(rendererEntities.size() == 0) return;
        SpriteBatchComponent spriteBatchComponent = Mapper.spriteBatchComponentMapper.get(rendererEntities.first());
        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(rendererEntities.first());
        FrameBufferComponent frameBufferComponent = Mapper.frameBufferComponentMapper.get(rendererEntities.first());
        PostProcessorsComponent postProcessorsComponent = Mapper.postProcessComponentMapper.get(rendererEntities.first());

        worldToScreenTransformComponent = Mapper.worldToScreenComponentMapper.get(rendererEntities.first());
        batch = spriteBatchComponent.spriteBatch;
        batch.setProjectionMatrix(cameraComponent.camera.combined);

        // Sort and Render
        forceSort();
        if(frameBufferComponent != null && frameBufferComponent.frameBuffer != null) {
            context.begin();
            frameBufferComponent.frameBuffer.begin();
        }

        batch.begin();
        super.update(deltaTime);
        batch.end();
        if(frameBufferComponent != null && frameBufferComponent.frameBuffer != null) {
            frameBufferComponent.frameBuffer.end();
            context.end();
        }

        if(frameBufferComponent != null && frameBufferComponent.frameBuffer != null) {
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

    Vector3 screenPos = new Vector3();


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
        TextureRegionComponent textureRegionComponent = Mapper.textureRegionComponentMapper.get(entity);
        SpriteStackComponent stackComponent = Mapper.spriteStackComponentMapper.get(entity);
        OriginComponent originComponent = Mapper.originComponentMapper.get(entity);
        ShaderProgramComponent shaderComponent = Mapper.shaderProgramComponentMapper.get(entity);

        if(shaderComponent != null) {
            batch.setShader(shaderComponent.program);
        }
        Vector3 s = worldToScreenTransformComponent.transform.worldToScreen(positionComponent.position, screenPos);
        if(textureRegionComponent != null) {
            if(originComponent != null && originComponent.origin != null)
                batch.draw(textureRegionComponent.textureRegion, s.x - originComponent.origin.x, s.y - originComponent.origin.y, originComponent.origin.x, originComponent.origin.y, textureRegionComponent.textureRegion.getRegionWidth(), textureRegionComponent.textureRegion.getRegionHeight(), 1f, 1f, 0f);
            else
                batch.draw(textureRegionComponent.textureRegion, s.x, s.y);
        }
        if(stackComponent != null) {
            SpriteStack stack = stackComponent.spriteStack;
            stack.setStackRotation(30 * deltaTime + stack.getRotation());
            for (int i = 0; i < stack.getSprites().length; i++) {

                Sprite sprite = stackComponent.spriteStack.getSprites()[i];
                for (int j = 0; j < stack.getLayerRepeats(); j++) {
                    batch.draw(sprite, s.x - sprite.getOriginX(), s.y + sprite.getOriginY() + (i * stack.getLayerSpacing()) + j , sprite.getOriginX(), sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), 1f, 1f,  stack.getRotation() + sprite.getRotation());
                }
            }
        }
        if(shaderComponent != null) {
            batch.setShader(null);
        }
    }
}