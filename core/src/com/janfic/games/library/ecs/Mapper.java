package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.janfic.games.library.ecs.components.events.*;
import com.janfic.games.library.ecs.components.input.InputProcessorComponent;
import com.janfic.games.library.ecs.components.physics.*;
import com.janfic.games.library.ecs.components.rendering.*;
import com.janfic.games.library.ecs.components.ui.ActorComponent;
import com.janfic.games.library.ecs.components.ui.StageComponent;

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
    public static final ComponentMapper<ViewportComponent>        viewportComponentMapper = ComponentMapper.getFor(ViewportComponent.class);

    // 2D Graphics
    public static final ComponentMapper<TextureComponent>         textureComponentMapper = ComponentMapper.getFor(TextureComponent.class);
    public static final ComponentMapper<TextureRegionComponent>   textureRegionComponentMapper = ComponentMapper.getFor(TextureRegionComponent.class);

    // 3D Graphics
    public static final ComponentMapper<ModelComponent>           modelComponentMapper = ComponentMapper.getFor(ModelComponent.class);
    public static final ComponentMapper<ModelInstanceComponent>   modelInstanceComponentMapper = ComponentMapper.getFor(ModelInstanceComponent.class);

    // User Interface
    public static final ComponentMapper<StageComponent>           stageComponentMapper = ComponentMapper.getFor(StageComponent.class);
    public static final ComponentMapper<ActorComponent>           actorComponentMapper = ComponentMapper.getFor(ActorComponent.class);

    // World

    // World Generation

    // Game State

    // Networking

    // Input
    public static final ComponentMapper<InputProcessorComponent> inputProcessorComponentMapper = ComponentMapper.getFor(InputProcessorComponent.class);

    // Events
    public static final ComponentMapper<EventQueueComponent> eventQueueComponentMapper = ComponentMapper.getFor(EventQueueComponent.class);
    public static final ComponentMapper<EventEntityAddComponentComponent> eventAddComponentComponentMapper = ComponentMapper.getFor(EventEntityAddComponentComponent.class);
    public static final ComponentMapper<EventEntityRemoveComponentsComponent> eventRemoveComponentsComponentMapper = ComponentMapper.getFor(EventEntityRemoveComponentsComponent.class);
    public static final ComponentMapper<EventComponentChangeComponent> eventComponentChangeComponentMapper = ComponentMapper.getFor(EventComponentChangeComponent.class);
    public static final ComponentMapper<EventFamilyAddComponentsComponent> eventFamilyAddComponentsComponentMapper = ComponentMapper.getFor(EventFamilyAddComponentsComponent.class);
    public static final ComponentMapper<EventFamilyRemoveComponentsComponent> eventFamilyRemoveComponentsComponentMapper = ComponentMapper.getFor(EventFamilyRemoveComponentsComponent.class);

}
