package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.janfic.games.library.ecs.components.actions.ActionControllerComponent;
import com.janfic.games.library.ecs.components.actions.ActionQueueComponent;
import com.janfic.games.library.ecs.components.actions.ActionsComponent;
import com.janfic.games.library.ecs.components.body.BodyComponent;
import com.janfic.games.library.ecs.components.events.*;
import com.janfic.games.library.ecs.components.input.ClickableComponent;
import com.janfic.games.library.ecs.components.input.HitBoxComponent;
import com.janfic.games.library.ecs.components.input.InputProcessorComponent;
import com.janfic.games.library.ecs.components.inventory.*;
import com.janfic.games.library.ecs.components.isometric.IsometricCameraComponent;
import com.janfic.games.library.ecs.components.physics.*;
import com.janfic.games.library.ecs.components.rendering.*;
import com.janfic.games.library.ecs.components.time.CooldownComponent;
import com.janfic.games.library.ecs.components.time.TimeComponent;
import com.janfic.games.library.ecs.components.ui.ActorComponent;
import com.janfic.games.library.ecs.components.ui.StageComponent;
import com.janfic.games.library.ecs.components.world.*;

public class Mapper {
    // Physics
    public static final ComponentMapper<PositionComponent>        positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VelocityComponent>        velocityComponentMapper = ComponentMapper.getFor(VelocityComponent.class);
    public static final ComponentMapper<AccelerationComponent>    accelerationComponentMapper = ComponentMapper.getFor(AccelerationComponent.class);
    public static final ComponentMapper<RotationComponent>        rotationComponentMapper = ComponentMapper.getFor(RotationComponent.class);
    public static final ComponentMapper<WeightComponent>          weightComponentMapper = ComponentMapper.getFor(WeightComponent.class);
    public static final ComponentMapper<BoundingBoxComponent>     boundingBoxComponentMapper = ComponentMapper.getFor(BoundingBoxComponent.class);
    public static final ComponentMapper<CollideableComponent>     collideableComponentMapper = ComponentMapper.getFor(CollideableComponent.class);
    public static final ComponentMapper<GravityComponent>         gravityComponentMapper = ComponentMapper.getFor(GravityComponent.class);
    public static final ComponentMapper<ForceComponent>           forceComponentMapper = ComponentMapper.getFor(ForceComponent.class);

    // General Rendering
    public static final ComponentMapper<CameraComponent>          cameraComponentMapper = ComponentMapper.getFor(CameraComponent.class);
    public static final ComponentMapper<EnvironmentComponent>     environmentComponentMapper = ComponentMapper.getFor(EnvironmentComponent.class);
    public static final ComponentMapper<FrameBufferComponent>     frameBufferComponentMapper = ComponentMapper.getFor(FrameBufferComponent.class);
    public static final ComponentMapper<ModelBatchComponent>      modelBatchComponentMapper = ComponentMapper.getFor(ModelBatchComponent.class);
    public static final ComponentMapper<PostProcessorsComponent>     postProcessComponentMapper = ComponentMapper.getFor(PostProcessorsComponent.class);
    public static final ComponentMapper<SpriteBatchComponent>     spriteBatchComponentMapper = ComponentMapper.getFor(SpriteBatchComponent.class);
    public static final ComponentMapper<ShaderComponent>          shaderComponentMapper = ComponentMapper.getFor(ShaderComponent.class);
    public static final ComponentMapper<ViewportComponent>        viewportComponentMapper = ComponentMapper.getFor(ViewportComponent.class);
    public static final ComponentMapper<RenderableProviderComponent>        renderableProviderComponentMapper = ComponentMapper.getFor(RenderableProviderComponent.class);
    public static final ComponentMapper<SpriteComponent>        spriteComponentMapper = ComponentMapper.getFor(SpriteComponent.class);

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
    public static final ComponentMapper<WorldComponent> worldComponentMapper = ComponentMapper.getFor(WorldComponent.class);
    public static final ComponentMapper<TileComponent> tileComponentMapper = ComponentMapper.getFor(TileComponent.class);
    public static final ComponentMapper<WorldInputComponent> worldInputComponentMapper = ComponentMapper.getFor(WorldInputComponent.class);
    public static final ComponentMapper<WorldToScreenTransformComponent> worldToScreenComponentMapper = ComponentMapper.getFor(WorldToScreenTransformComponent.class);

    // World Generation
    public static final ComponentMapper<GenerateWorldComponent> generateWorldComponentMapper = ComponentMapper.getFor(GenerateWorldComponent.class);


    // Game State

    // Networking

    // Input
    public static final ComponentMapper<InputProcessorComponent> inputProcessorComponentMapper = ComponentMapper.getFor(InputProcessorComponent.class);
    public static final ComponentMapper<HitBoxComponent> hitBoxComponentMapper = ComponentMapper.getFor(HitBoxComponent.class);
    public static final ComponentMapper<ClickableComponent> clickableComponentMapper = ComponentMapper.getFor(ClickableComponent.class);


    // Events
    public static final ComponentMapper<EventQueueComponent> eventQueueComponentMapper = ComponentMapper.getFor(EventQueueComponent.class);
    public static final ComponentMapper<EventEntityAddComponentComponent> eventAddComponentComponentMapper = ComponentMapper.getFor(EventEntityAddComponentComponent.class);
    public static final ComponentMapper<EventEntityRemoveComponentsComponent> eventRemoveComponentsComponentMapper = ComponentMapper.getFor(EventEntityRemoveComponentsComponent.class);
    public static final ComponentMapper<EventComponentChangeComponent> eventComponentChangeComponentMapper = ComponentMapper.getFor(EventComponentChangeComponent.class);
    public static final ComponentMapper<EventFamilyAddComponentsComponent> eventFamilyAddComponentsComponentMapper = ComponentMapper.getFor(EventFamilyAddComponentsComponent.class);
    public static final ComponentMapper<EventFamilyRemoveComponentsComponent> eventFamilyRemoveComponentsComponentMapper = ComponentMapper.getFor(EventFamilyRemoveComponentsComponent.class);

    // Isometric Rules
    public static final ComponentMapper<IsometricCameraComponent> isometricCameraComponentMapper = ComponentMapper.getFor(IsometricCameraComponent.class);

    // Inventory
    public static final ComponentMapper<InventoryComponent> inventoryComponentMapper = ComponentMapper.getFor(InventoryComponent.class);
    public static final ComponentMapper<ItemComponent> itemComponentMapper = ComponentMapper.getFor(ItemComponent.class);
    public static final ComponentMapper<ItemFilterComponent> itemFilterComponentMapper = ComponentMapper.getFor(ItemFilterComponent.class);
    public static final ComponentMapper<ItemPropertyComponent> itemPropertyComponentMapper = ComponentMapper.getFor(ItemPropertyComponent.class);

    // Body
    public static final ComponentMapper<BodyComponent> bodyComponentMapper = ComponentMapper.getFor(BodyComponent.class);

    // Actions
    public static final ComponentMapper<ActionQueueComponent> actionQueueComponentMapper = ComponentMapper.getFor(ActionQueueComponent.class);
    public static final ComponentMapper<ActionsComponent> actionsComponentMapper = ComponentMapper.getFor(ActionsComponent.class);
    public static final ComponentMapper<ActionControllerComponent> actionControllerComponentMapper = ComponentMapper.getFor(ActionControllerComponent.class);

    // Time
    public static final ComponentMapper<TimeComponent> timeComponentMapper = ComponentMapper.getFor(TimeComponent.class);
    public static final ComponentMapper<CooldownComponent> cooldownComponentMapper = ComponentMapper.getFor(CooldownComponent.class);


}
