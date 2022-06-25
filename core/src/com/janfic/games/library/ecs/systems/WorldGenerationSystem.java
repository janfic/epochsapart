package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.physics.RotationComponent;
import com.janfic.games.library.ecs.components.rendering.ModelComponent;
import com.janfic.games.library.ecs.components.rendering.ModelInstanceComponent;
import com.janfic.games.library.ecs.components.rendering.RenderableProviderComponent;
import com.janfic.games.library.ecs.components.world.GenerateWorldComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;
import com.janfic.games.library.ecs.components.world.WorldComponent;
import com.janfic.games.library.utils.voxel.VoxelChunk;
import com.janfic.games.library.utils.voxel.VoxelWorld;
import jdk.internal.icu.text.NormalizerBase;

public class WorldGenerationSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private static final Family worldGeneratorFamily = Family.all(GenerateWorldComponent.class).exclude(WorldComponent.class).get();

    Model slantModel, cubeModel, cornerModel;
    BoundingBox tileBounds;

    Json settings;

    TextureAttribute dirtCubeAttribute;
    TextureAttribute grassCubeAttribute, grassSlantAttribute, grassCornerAttribute;
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(worldGeneratorFamily);
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
            Json json = new Json();
            GenerateWorldComponent generateWorldComponent = Mapper.generateWorldComponentMapper.get(entity);
            JsonValue v = json.fromJson(null, generateWorldComponent.generationSettings);

            int width = generateWorldComponent.width;
            int height = generateWorldComponent.height;
            int length = generateWorldComponent.length;

            NoiseGenerator noiseGenerator = new NoiseGenerator();
            Grid grid = new Grid(width, length);
            JsonValue stages = v.get("generation").get("stages");
            JsonValue stage = stages.child();
            while(stage.next() != null) {
                int radius = stage.getInt(0);
                float modifier = stage.getFloat(1);
                noiseStage(grid, noiseGenerator, radius , modifier);
                stage = stage.next();
            }

            WorldComponent worldComponent = new WorldComponent();
            worldComponent.world = new Entity[width][height][length];
            worldComponent.centerX = width / 2;
            worldComponent.centerY = height / 2;
            worldComponent.centerZ = length / 2;

            RenderableProviderComponent renderableProviderComponent = new RenderableProviderComponent();
            VoxelWorld world = new VoxelWorld(null, (int) Math.ceil(width / (float)VoxelChunk.CHUNK_SIZE_X),(int) Math.ceil(height / (float)VoxelChunk.CHUNK_SIZE_Y),(int) Math.ceil(length / (float)VoxelChunk.CHUNK_SIZE_Z));
            renderableProviderComponent.renderableProvider = world;

            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    int h = (int) (grid.get(x,z) * height);
                    for (int y = 0; y <= h; y++) {
                        world.set(x,y,z,(byte) 1);
                    }
                }
            }

            PositionComponent positionComponent = new PositionComponent();
            positionComponent.position = new Vector3(worldComponent.centerX, worldComponent.centerY, worldComponent.centerZ);

            entity.add(positionComponent);
            entity.add(worldComponent);
            entity.add(renderableProviderComponent);


        }
    }

    private static void noiseStage(Grid grid, NoiseGenerator noiseGenerator, int radius, float modifier) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        noiseGenerator.setSeed(Generators.rollSeed());
        noiseGenerator.generate(grid);
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
