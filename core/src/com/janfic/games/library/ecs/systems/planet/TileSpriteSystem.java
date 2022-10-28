package com.janfic.games.library.ecs.systems.planet;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.planet.PlanetComponent;
import com.janfic.games.library.ecs.components.rendering.InvisibleComponent;
import com.janfic.games.library.ecs.components.rendering.TextureAtlasComponent;
import com.janfic.games.library.ecs.components.rendering.TextureRegionComponent;
import com.janfic.games.library.ecs.components.world.DirtyTileComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;
import com.janfic.games.library.utils.isometric.IsometricChunk;

public class TileSpriteSystem extends EntitySystem {
    private ImmutableArray<Entity> entities, planets;

    private static final Family planetFamily = Family.all( TextureAtlasComponent.class, PlanetComponent.class).get();
    private static final Family entityFamily = Family.all( DirtyTileComponent.class, TileComponent.class).get();

    Thread thread;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(entityFamily);
        planets = engine.getEntitiesFor(planetFamily);
    }

    int tilesPerFrame = IsometricChunk.CHUNK_SIZE_X * IsometricChunk.CHUNK_SIZE_Z * IsometricChunk.CHUNK_SIZE_Y;

    byte[] neighbors = {

    };

    @Override
    public void update(float deltaTime) {
        if(planets.size() == 0) return;
        PlanetComponent planetComponent = Mapper.planetComponentMapper.get(planets.first());
        TextureAtlasComponent atlasComponent = Mapper.textureAtlasComponentMapper.get(planets.first());
        for (int i = 0; i < tilesPerFrame && i < entities.size(); i++) {
            Entity entity = entities.get(i);
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            TileComponent tileComponent = Mapper.tileComponentMapper.get(entity);
            TextureRegionComponent component = Mapper.textureRegionComponentMapper.get(entity);
            if (component == null) {component = new TextureRegionComponent(); entity.add(component);}
            String variant = "";
            int x = (int) positionComponent.position.x;
            int y = (int) positionComponent.position.y;
            int z = (int) positionComponent.position.z;
            if(tileComponent.name.equals("grass")) {
//                if(planetComponent.world.isTileAt(x + 1, y, z) &&
//                        planetComponent.world.isTileAt(x - 1, y, z) &&
//                        !planetComponent.world.isTileAt(x, y, z - 1) &&
//                        planetComponent.world.isTileAt(x, y, z + 1)) {
//                    //variant = "_s";
//                }
//                if(planetComponent.world.isTileAt(x + 1, y, z) &&
//                        !planetComponent.world.isTileAt(x - 1, y, z) &&
//                        planetComponent.world.isTileAt(x, y, z - 1) &&
//                        planetComponent.world.isTileAt(x, y, z + 1)) {
//                    //variant = "_e";
//                }
            }

            component.textureRegion = atlasComponent.textureAtlas.findRegion(tileComponent.name + variant);
            entity.remove(DirtyTileComponent.class);
        }
    }
}
