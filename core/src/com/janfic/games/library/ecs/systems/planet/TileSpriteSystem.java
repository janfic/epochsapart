package com.janfic.games.library.ecs.systems.planet;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.planet.PlanetComponent;
import com.janfic.games.library.ecs.components.rendering.TextureAtlasComponent;
import com.janfic.games.library.ecs.components.rendering.TextureRegionComponent;
import com.janfic.games.library.ecs.components.world.DirtyTileComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;

public class TileSpriteSystem extends EntitySystem {
    private ImmutableArray<Entity> entities, planets;

    private static final Family planetFamily = Family.all( TextureAtlasComponent.class, PlanetComponent.class).get();
    private static final Family entityFamily = Family.all( DirtyTileComponent.class, TileComponent.class).get();


    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(entityFamily);
        planets = engine.getEntitiesFor(planetFamily);
    }

    @Override
    public void update(float deltaTime) {
        if(planets.size() == 0) return;
        TextureAtlasComponent atlasComponent = Mapper.textureAtlasComponentMapper.get(planets.first());
        long start = System.currentTimeMillis();
        for (Entity entity : entities) {
            TileComponent tileComponent = Mapper.tileComponentMapper.get(entity);
            entity.remove(DirtyTileComponent.class);
            if(!tileComponent.isVisible) {
                entity.remove(TextureRegionComponent.class);
                continue;
            }
            if(!tileComponent.chunk.isVisible()) continue;
            TextureRegionComponent component = Mapper.textureRegionComponentMapper.get(entity);
            if (component == null) {component = new TextureRegionComponent(); entity.add(component);}
            component.textureRegion = atlasComponent.textureAtlas.findRegion(tileComponent.name);
        }

        long end = System.currentTimeMillis();
        System.out.println("tileSprite: " + (end-start)/1000f);
    }
}
