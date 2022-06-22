package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.EnvironmentComponent;
import com.janfic.games.library.ecs.components.rendering.ModelBatchComponent;
import com.janfic.games.library.ecs.components.rendering.ModelInstanceComponent;
import com.janfic.games.library.ecs.components.world.GenerateWorldComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;
import com.janfic.games.library.ecs.components.world.WorldComponent;

public class WorldGenerationSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private static final Family worldGeneratorComponent = Family.all(GenerateWorldComponent.class).exclude(WorldComponent.class).get();

    Model model;
    BoundingBox tileBounds;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(worldGeneratorComponent);
        AssetManager assets = new AssetManager();
        assets.load("base_cube.obj", Model.class);
        assets.finishLoading();

        model = assets.get("base_cube.obj", Model.class);
        tileBounds = new BoundingBox();
        tileBounds = model.calculateBoundingBox(tileBounds);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (Entity entity : entities) {
            GenerateWorldComponent generateWorldComponent = Mapper.generateWorldComponentMapper.get(entity);

            int width = generateWorldComponent.width;
            int height = generateWorldComponent.height;
            int length = generateWorldComponent.length;

            WorldComponent worldComponent = new WorldComponent();
            worldComponent.world = new Entity[width][height][length];
            worldComponent.centerX = width / 2;
            worldComponent.centerY = height / 2;
            worldComponent.centerZ = length / 2;

            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    for (int y = 0; y < height; y++) {
                        Entity tileEntity = new Entity();

                        TileComponent tileComponent = new TileComponent();
                        tileComponent.worldX = x;
                        tileComponent.worldY = y;
                        tileComponent.worldZ = z;

                        ModelInstanceComponent modelInstanceComponent = new ModelInstanceComponent();
                        modelInstanceComponent.instance = new ModelInstance(model,
                                (x - worldComponent.centerX) * tileBounds.getWidth(),
                                (y - worldComponent.centerY) * tileBounds.getHeight(),
                                (z - worldComponent.centerZ) * tileBounds.getDepth()
                        );

                        PositionComponent positionComponent = new PositionComponent();
                        positionComponent.position = new Vector3();
                        positionComponent.position.set(
                                (x - worldComponent.centerX) * tileBounds.getWidth(),
                                (y - worldComponent.centerY) * tileBounds.getHeight(),
                                (z - worldComponent.centerZ) * tileBounds.getDepth()
                        );

                        tileEntity.add(modelInstanceComponent);
                        tileEntity.add(positionComponent);
                        tileEntity.add(tileComponent);

                        getEngine().addEntity(tileEntity);

                        worldComponent.world[x][y][z] = tileEntity;
                    }
                }
            }

            entity.add(worldComponent);
        }
    }
}
