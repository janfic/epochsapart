package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.physics.RotationComponent;
import com.janfic.games.library.ecs.components.rendering.ModelInstanceComponent;
import com.janfic.games.library.ecs.components.world.GenerateWorldComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;
import com.janfic.games.library.ecs.components.world.WorldComponent;

public class WorldGenerationSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private static final Family worldGeneratorComponent = Family.all(GenerateWorldComponent.class).exclude(WorldComponent.class).get();

    Model slantModel, cubeModel, cornerModel;
    BoundingBox tileBounds;

    TextureAttribute dirtCubeAttribute;
    TextureAttribute grassCubeAttribute, grassSlantAttribute, grassCornerAttribute;
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(worldGeneratorComponent);
        AssetManager assets = new AssetManager();
        assets.load("models/baseTiles/base_slant.obj", Model.class);
        assets.load("models/baseTiles/base_cube.obj", Model.class);
        assets.load("models/baseTiles/base_corner.obj", Model.class);
        assets.finishLoading();

        slantModel = assets.get("models/baseTiles/base_slant.obj", Model.class);
        cubeModel = assets.get("models/baseTiles/base_cube.obj", Model.class);
        cornerModel = assets.get("models/baseTiles/base_corner.obj", Model.class);

        Texture dirtCubeAttribute = new Texture("models/tileTextures/dirt_cube.png");
        dirtCubeAttribute.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.dirtCubeAttribute = TextureAttribute.createDiffuse(dirtCubeAttribute);

        Texture grassSlantTexture = new Texture("models/tileTextures/grass_slant.png");
        grassSlantTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        grassSlantAttribute = TextureAttribute.createDiffuse(grassSlantTexture);

        Texture grassCornerTexture = new Texture("models/tileTextures/grass_corner.png");
        grassCornerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        grassCornerAttribute = TextureAttribute.createDiffuse(grassCornerTexture);

        Texture grassCubeTexture = new Texture("models/tileTextures/grass.png");
        grassCubeTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        grassCubeAttribute = TextureAttribute.createDiffuse(grassCubeTexture);

        tileBounds = new BoundingBox();
        tileBounds = slantModel.calculateBoundingBox(tileBounds);
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
                    for (int y = 0; y < 1; y++) {

                        Entity tileEntity = makeCube(x, y, z, worldComponent, grassCubeAttribute);

                        getEngine().addEntity(tileEntity);
                        worldComponent.world[x][y][z] = tileEntity;
                    }
                }
            }

            Entity tileEntity = makeCube(worldComponent.centerX,  1, worldComponent.centerZ, worldComponent, grassCubeAttribute);
            getEngine().addEntity(tileEntity);
            worldComponent.world[worldComponent.centerX][1][worldComponent.centerZ] = tileEntity;

            float rot = -90;
            for (int x = -1; x <= 1; x+=2) {
                Entity slant = makeSlant(worldComponent.centerX + x,  1, worldComponent.centerZ, rot, worldComponent, grassSlantAttribute);
                getEngine().addEntity(slant);
                worldComponent.world[worldComponent.centerX + x][1][worldComponent.centerZ ] = tileEntity;
                rot += 180;
            }
            rot -= 90;
            for (int z = -1; z <= 1; z+=2) {
                Entity slant = makeSlant(worldComponent.centerX,  1, worldComponent.centerZ + z, rot, worldComponent, grassSlantAttribute);
                getEngine().addEntity(slant);
                worldComponent.world[worldComponent.centerX][1][worldComponent.centerZ + z] = tileEntity;
                rot += 180;
            }

            rot = -180;
            for (int x = -1; x <= 1; x+=2) {
                for (int z = -1; z <= 1; z += 2) {
                    Entity corner = makeCorner(worldComponent.centerX + x,  1, worldComponent.centerZ + z, rot, worldComponent, grassCornerAttribute);
                    getEngine().addEntity(corner);
                    worldComponent.world[worldComponent.centerX][1][worldComponent.centerZ + z] = tileEntity;
                    rot += -x * 90;
                }
                rot += 90;
            }

            entity.add(worldComponent);
        }
    }

    public Entity makeTile(int x, int y, int z, float rotation, WorldComponent worldComponent, TextureAttribute attribute, Model model) {
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
        modelInstanceComponent.instance.materials.get(0).set(attribute);

        RotationComponent rotationComponent = new RotationComponent();
        rotationComponent.axis = new Vector3(0,1,0);
        rotationComponent.angle = rotation;

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
        tileEntity.add(rotationComponent);

        return tileEntity;
    }

    public Entity makeCorner(int x, int y, int z, float rotation, WorldComponent worldComponent, TextureAttribute attribute) {
        return makeTile(x, y,z, rotation,  worldComponent,  attribute, cornerModel);
    }

    public Entity makeCube(int x, int y, int z, WorldComponent worldComponent, TextureAttribute attribute) {
        return makeTile(x, y, z, 0, worldComponent, attribute, cubeModel);
    }

    public Entity makeSlant(int x, int y, int z, float degrees, WorldComponent worldComponent, TextureAttribute attribute) {
        return makeTile(x, y, z, degrees, worldComponent, attribute, slantModel);
    }
}
