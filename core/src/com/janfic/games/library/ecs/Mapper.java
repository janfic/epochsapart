package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.janfic.games.library.ecs.components.*;

public class Mapper {
    // Physics
    public static final ComponentMapper<PositionComponent>        positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VelocityComponent>        velocityComponentMapper = ComponentMapper.getFor(VelocityComponent.class);
    public static final ComponentMapper<AccelerationComponent>    accelerationComponentMapper = ComponentMapper.getFor(AccelerationComponent.class);

    // General Rendering
    public static final ComponentMapper<CameraComponent>          cameraComponentMapper = ComponentMapper.getFor(CameraComponent.class);
    public static final ComponentMapper<EnvironmentComponent>     environmentComponentMapper = ComponentMapper.getFor(EnvironmentComponent.class);
    public static final ComponentMapper<FrameBufferComponent>     frameBufferComponentMapper = ComponentMapper.getFor(FrameBufferComponent.class);
    public static final ComponentMapper<ModelBatchComponent>      modelBatchComponentMapper = ComponentMapper.getFor(ModelBatchComponent.class);
    public static final ComponentMapper<PostProcessorsComponent>     postProcessComponentMapper = ComponentMapper.getFor(PostProcessorsComponent.class);
    public static final ComponentMapper<SpriteBatchComponent>     spriteBatchComponentMapper = ComponentMapper.getFor(SpriteBatchComponent.class);
    public static final ComponentMapper<ShaderComponent>          shaderComponentMapper = ComponentMapper.getFor(ShaderComponent.class);

    // 2D Graphics
    public static final ComponentMapper<TextureComponent>         textureComponentMapper = ComponentMapper.getFor(TextureComponent.class);
    public static final ComponentMapper<TextureRegionComponent>   textureRegionComponentMapper = ComponentMapper.getFor(TextureRegionComponent.class);

    // 3D Graphics
    public static final ComponentMapper<ModelComponent>           modelComponentMapper = ComponentMapper.getFor(ModelComponent.class);
    public static final ComponentMapper<ModelInstanceComponent>   modelInstanceComponentMapper = ComponentMapper.getFor(ModelInstanceComponent.class);

    // World

    // World Generation

    // Game State

    // Networking

    // Input
    public static final ComponentMapper<InputProcessorComponent> inputProcessorComponentMapper = ComponentMapper.getFor(InputProcessorComponent.class);

    // Events
    public static final ComponentMapper<EventQueueComponent> eventQueueComponentMapper = ComponentMapper.getFor(EventQueueComponent.class);
    public static final ComponentMapper<EventAddComponentComponent> eventAddComponentComponentMapper = ComponentMapper.getFor(EventAddComponentComponent.class);
    public static final ComponentMapper<EventRemoveComponentsComponent> eventRemoveComponentsComponentMapper = ComponentMapper.getFor(EventRemoveComponentsComponent.class);

}
