package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.components.events.EventQueueComponent;
import com.janfic.games.library.ecs.components.physics.*;
import com.janfic.games.library.ecs.components.planet.PlanetGenerationComponent;
import com.janfic.games.library.ecs.components.rendering.*;
import com.janfic.games.library.ecs.components.world.TileComponent;
import com.janfic.games.library.ecs.systems.physics.GravitySystem;
import com.janfic.games.library.ecs.systems.physics.PhysicsSystem;
import com.janfic.games.library.ecs.systems.planet.PlanetCollisionSystem;
import com.janfic.games.library.ecs.systems.planet.PlanetGenerationSystem;
import com.janfic.games.library.ecs.systems.planet.PlanetUpdateSystem;
import com.janfic.games.library.ecs.systems.planet.TileSpriteSystem;
import com.janfic.games.library.ecs.systems.rendering.CameraFollowSystem;
import com.janfic.games.library.ecs.systems.rendering.CameraPositionSystem;
import com.janfic.games.library.ecs.systems.rendering.SpriteRenderSystem;
import com.janfic.games.library.utils.IsometricRenderComparator;
import com.janfic.games.library.utils.isometric.IsometricWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Isometric2DEngine extends Engine {

    public Isometric2DEngine() {
        Entity gameEntity = new Entity();
        EventQueueComponent eventQueueComponent = new EventQueueComponent();
        eventQueueComponent.events = new LinkedList<>();
        gameEntity.add(eventQueueComponent);

        addEntity(gameEntity);

        addSystem(new PlanetGenerationSystem());
        addSystem(new PlanetUpdateSystem());
        addSystem(new TileSpriteSystem());
        addSystem(new GravitySystem());
        addSystem(new PhysicsSystem());
        addSystem(new PlanetCollisionSystem());
        addSystem(new CameraFollowSystem());
        addSystem(new SpriteRenderSystem(new IsometricRenderComparator()));

        createRendererEntity();
        createPlanet();
        createPlayer();
    }



    public void createRendererEntity() {
        Entity rendererEntity = createEntity();

        SpriteBatchComponent spriteBatchComponent = new SpriteBatchComponent();
        spriteBatchComponent.spriteBatch = new SpriteBatch();

        CameraComponent cameraComponent = new CameraComponent();
        OrthographicCamera orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 3f);
//        OrthographicCamera orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        cameraComponent.camera = orthographicCamera;
        orthographicCamera.position.set(0,0,0);
        orthographicCamera.update();

        WorldToScreenTransformComponent worldToScreenTransformComponent = new WorldToScreenTransformComponent();
        worldToScreenTransformComponent.transform = new WorldToScreenTransformComponent.IsometricWorldTransform(16,8, 15);

        CameraFollowComponent cameraFollowComponent = new CameraFollowComponent();

        rendererEntity.add(spriteBatchComponent);
        rendererEntity.add(cameraComponent);
        rendererEntity.add(worldToScreenTransformComponent);
        rendererEntity.add(cameraFollowComponent);

        addEntity(rendererEntity);

    }

    public void createPlanet() {

        Entity planetEntity = createEntity();

        PlanetGenerationComponent planetGenerationComponent = new PlanetGenerationComponent();
        planetGenerationComponent.planetSettings = Gdx.files.local("planet/default.json");
        planetGenerationComponent.width = 128;
        planetGenerationComponent.height = 32;
        planetGenerationComponent.length = 128;

        planetEntity.add(planetGenerationComponent);

        addEntity(planetEntity);
    }

    PositionComponent positionComponent;
    public void createPlayer() {
        Entity player = createEntity();

        positionComponent = new PositionComponent();
        positionComponent.position = new Vector3(32, 64, 32);

        TextureRegionComponent textureRegionComponent = new TextureRegionComponent();
        textureRegionComponent.textureRegion = new TextureRegion(new Texture("test.png"));

        CameraFollowComponent cameraFollowComponent = new CameraFollowComponent();

        OriginComponent originComponent = new OriginComponent();
        originComponent.origin = new Vector3(18, -8, 0);

        VelocityComponent velocityComponent = new VelocityComponent();
        velocityComponent.velocity = new Vector3(0, 0, 0);

        ForceComponent forceComponent = new ForceComponent();
        forceComponent.forces = new ArrayList<>();
        forceComponent.named = new HashMap<>();

        AccelerationComponent accelerationComponent = new AccelerationComponent();
        accelerationComponent.acceleration = new Vector3();

        GravityComponent gravityComponent = new GravityComponent();
        gravityComponent.gravity = new Vector3(0, -9, 0);

        player.add(positionComponent);
        player.add(velocityComponent);
        player.add(accelerationComponent);
        player.add(forceComponent);
        player.add(gravityComponent);
        player.add(originComponent);
        player.add(cameraFollowComponent);
        player.add(textureRegionComponent);

        addEntity(player);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            positionComponent.position.add(0,0,-1);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            positionComponent.position.add(0,0,1);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            positionComponent.position.add(0,1,0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            positionComponent.position.add(0,-1,0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            positionComponent.position.add(-1,0,0);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            positionComponent.position.add(1,0,0);
        }
        System.out.println(Gdx.graphics.getFramesPerSecond());
    }
}
