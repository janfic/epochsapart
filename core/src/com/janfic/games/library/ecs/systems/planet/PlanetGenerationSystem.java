package com.janfic.games.library.ecs.systems.planet;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.planet.PlanetComponent;
import com.janfic.games.library.ecs.components.planet.PlanetGenerationComponent;
import com.janfic.games.library.ecs.components.rendering.OriginComponent;
import com.janfic.games.library.ecs.components.rendering.TextureAtlasComponent;
import com.janfic.games.library.ecs.components.world.DirtyTileComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;
import com.janfic.games.library.utils.isometric.IsometricWorld;

public class PlanetGenerationSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private static final Family family = Family.all(PlanetGenerationComponent.class).exclude(PlanetComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PlanetGenerationComponent planetGenerationComponent = Mapper.planetGenerationComponentMapper.get(entity);

            if(planetGenerationComponent.completed) continue;

            Json json = new Json();

            JsonValue v = json.fromJson(null, planetGenerationComponent.planetSettings);

            int width = planetGenerationComponent.width;
            int height = planetGenerationComponent.height;
            int length = planetGenerationComponent.length;

            NoiseGenerator noiseGenerator = new NoiseGenerator();
            Grid grid = new Grid(width, length);

            JsonValue stages = v.get("biomes")
                    .child()
                    .get("generation")
                    .get("stages");
            JsonValue stage = stages.child();
            while(stage.next() != null) {
                int radius = stage.getInt(0);
                float modifier = stage.getFloat(1);
                noiseStage(grid, noiseGenerator, radius , modifier);
                stage = stage.next();
            }

            PlanetComponent planetComponent = new PlanetComponent();
            planetComponent.world = new IsometricWorld(width, height, length);

            TextureAtlasComponent textureAtlasComponent = new TextureAtlasComponent();
            textureAtlasComponent.textureAtlas = new TextureAtlas( Gdx.files.local("planet/" + v.get("tilesPack").asString()));

            for (int x = 0; x < planetComponent.world.tilesWidth; x++) {
                for (int z = 0; z < planetComponent.world.tilesDepth; z++) {
                    float h = grid.get(x,z) * planetComponent.world.tilesHeight;
                    for (int y = 0; y < h; y++) {
                        String tileType = "dirt";
                        if( y == (int)(h)) tileType = "grass";
                        Entity tileEntity = makeTile(x,y,z, tileType);
                        planetComponent.world.set(x,y,z, tileEntity);
                        getEngine().addEntity(tileEntity);
                    }
                }
            }

            planetGenerationComponent.completed = true;
            entity.add(planetComponent);
            entity.add(textureAtlasComponent);
        }
    }

    public Entity makeTile(int x, int y, int z, String name) {
        Entity tileEntity = new Entity();

        PositionComponent worldPosition = new PositionComponent();
        worldPosition.position = new Vector3();
        worldPosition.position.set(x,y,z);

        TileComponent tileComponent = new TileComponent();
        tileComponent.isVisible = true;
        tileComponent.name = name;

        OriginComponent originComponent = new OriginComponent();
        originComponent.origin = new Vector3(16, 0, 0);

        tileEntity.add(worldPosition);
        tileEntity.add(originComponent);
        tileEntity.add(tileComponent);
        return tileEntity;
    }

    private static void noiseStage(Grid grid, NoiseGenerator noiseGenerator, int radius, float modifier) {
        noiseGenerator.setRadius(radius);
        noiseGenerator.setModifier(modifier);
        noiseGenerator.setSeed(Generators.rollSeed());
        noiseGenerator.generate(grid);
    }
}
