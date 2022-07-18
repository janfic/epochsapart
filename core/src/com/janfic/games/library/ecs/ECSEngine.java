package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.actions.actions.PickUpAction;
import com.janfic.games.library.actions.actions.WalkAction;
import com.janfic.games.library.actions.controllers.PlayerActionController;
import com.janfic.games.library.body.bodyparts.HumanHand;
import com.janfic.games.library.body.bodyparts.HumanLeg;
import com.janfic.games.library.ecs.components.actions.ActionControllerComponent;
import com.janfic.games.library.ecs.components.actions.ActionQueueComponent;
import com.janfic.games.library.ecs.components.actions.ActionsComponent;
import com.janfic.games.library.ecs.components.body.BodyComponent;
import com.janfic.games.library.ecs.components.events.EventComponent;
import com.janfic.games.library.ecs.components.events.EventComponentChangeComponent;
import com.janfic.games.library.ecs.components.events.EventQueueComponent;
import com.janfic.games.library.ecs.components.input.ClickableComponent;
import com.janfic.games.library.ecs.components.input.HitBoxComponent;
import com.janfic.games.library.ecs.components.input.InputProcessorComponent;
import com.janfic.games.library.ecs.components.isometric.IsometricCameraComponent;
import com.janfic.games.library.ecs.components.physics.*;
import com.janfic.games.library.ecs.components.rendering.*;
import com.janfic.games.library.ecs.components.time.TimeComponent;
import com.janfic.games.library.ecs.components.ui.StageComponent;
import com.janfic.games.library.ecs.components.world.GenerateWorldComponent;
import com.janfic.games.library.ecs.components.world.WorldInputComponent;
import com.janfic.games.library.ecs.systems.*;
import com.janfic.games.library.ecs.systems.actions.ActionControllerSystem;
import com.janfic.games.library.ecs.systems.actions.ActionSystem;
import com.janfic.games.library.ecs.systems.body.BodySystem;
import com.janfic.games.library.ecs.systems.input.InputSystem;
import com.janfic.games.library.ecs.systems.input.ModelClickSystem;
import com.janfic.games.library.ecs.systems.physics.BoundingBoxSystem;
import com.janfic.games.library.ecs.systems.physics.GravitySystem;
import com.janfic.games.library.ecs.systems.physics.ModelPositionSystem;
import com.janfic.games.library.ecs.systems.physics.PhysicsSystem;
import com.janfic.games.library.ecs.systems.rendering.*;
import com.janfic.games.library.ecs.systems.time.TimeSystem;
import com.janfic.games.library.ecs.systems.world.WorldCollisionSystem;
import com.janfic.games.library.ecs.systems.world.WorldGenerationSystem;
import com.janfic.games.library.ecs.systems.world.WorldInputSystem;
import com.janfic.games.library.graphics.shaders.BorderShader;
import com.janfic.games.library.graphics.shaders.postprocess.*;
import com.janfic.games.library.utils.ECSClickListener;
import com.janfic.games.library.utils.voxel.CubeVoxel;
import com.janfic.games.library.utils.voxel.WorldInputListener;

import java.util.*;

public class ECSEngine extends Engine {

    public PostProcessorsComponent postProcessesComponent;

    Entity modelRenderer;

    public DirectionalShadowLight  light;

    public PalettePostProcess palettePostProcess;
    public DitherPostProcess ditherPostProcess;
    public PixelizePostProcess pixelizePostProcess;

    public OrthographicCamera camera;

    public ECSEngine() {

        ShaderProgram.pedantic = false;

        Entity gameEntity = new Entity();
        EventQueueComponent eventQueueComponent = new EventQueueComponent();
        eventQueueComponent.events = new LinkedList<>();
        gameEntity.add(eventQueueComponent);

        addEntity(gameEntity);

        // Systems
        GameRenderSystem gameRenderSystem = new GameRenderSystem();
        UserInterfaceSystem userInterfaceSystem = new UserInterfaceSystem();
        InputSystem inputSystem = new InputSystem();
        EventSystem eventSystem = new EventSystem();
        ModelPositionSystem positionSystem = new ModelPositionSystem();
        WorldGenerationSystem worldGenerationSystem = new WorldGenerationSystem();
        addSystem(worldGenerationSystem);
        addSystem(inputSystem);
        addSystem(new GravitySystem());
        addSystem(new BodySystem());
        addSystem(new WorldInputSystem());
        addSystem(new WorldCollisionSystem());
        addSystem(new PhysicsSystem());
        addSystem(new ActionSystem());
        addSystem(new ActionControllerSystem());
        addSystem(eventSystem);
        addSystem(positionSystem);
        addSystem(new CameraPositionSystem());
        addSystem(new CameraFollowSystem());
        addSystem(new IsometricCameraSystem());
        addSystem(new ModelClickSystem());
        addSystem(new BoundingBoxSystem());
        addSystem(new TimeSystem());
        addSystem(gameRenderSystem);
        addSystem(userInterfaceSystem);

        makePlayer();
        makeRenderer();
//        makeGameEntities();
        makeWorld();
        makeUISystem();
    }

    private void makeWorld() {
        Entity entity = new Entity();
        TimeComponent timeComponent = new TimeComponent();
        timeComponent.minutesPerRealSecond = 1 / 5f;
        entity.add(timeComponent);

        GenerateWorldComponent generateWorldComponent = new GenerateWorldComponent();
        generateWorldComponent.height = 64;
        generateWorldComponent.width = 256;
        generateWorldComponent.length = 256;
        generateWorldComponent.generationSettings = Gdx.files.local("worldGeneration/biomes/plains/plains.json");
        entity.add(generateWorldComponent);
        WorldInputComponent worldInputComponent = new WorldInputComponent();
        worldInputComponent.listener = new WorldInputListener() {

            Vector3 lastEntity;
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                if(lastEntity != null && !worldVoxel.equals(lastEntity)) {
                    CubeVoxel last = world.get(lastEntity.x, lastEntity.y, lastEntity.z);
                    if(last.getType().contains("hover")) {
                        last.setType(last.getType().replace("hover", ""));
                    }
                    world.set(lastEntity.x, lastEntity.y, lastEntity.z, last);
                }

                CubeVoxel v = world.get(worldVoxel.x, worldVoxel.y, worldVoxel.z);
                if(!v.getType().contains("hover")) {
                    v.setType(v.getType() + "hover");
                }
                world.set(worldVoxel.x, worldVoxel.y, worldVoxel.z, v);
                lastEntity = worldVoxel.cpy();
                return super.mouseMoved(event, x, y);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(event.getButton() == 1) {
                    Vector3 location = worldVoxel.cpy();
                    PositionComponent positionComponent = Mapper.positionComponentMapper.get(player);
                    location.z = worldVoxel.z + (positionComponent.position.z - (float)Math.floor(positionComponent.position.z));
                    location.x = worldVoxel.x + (positionComponent.position.x - (float)Math.floor(positionComponent.position.x)) ;
                    location.y = worldVoxel.y + (positionComponent.position.y - (float)Math.floor(positionComponent.position.y)) + 1;
                    actionQueueComponent.actionQueue.add(new WalkAction(player, player, location, world));
                }
            }
        };

        entity.add(worldInputComponent);
        addEntity(entity);
    }

    Entity player;
    ActionQueueComponent actionQueueComponent;
    ActionsComponent actionsComponent;
    ActionControllerComponent actionControllerComponent;

    public void makePlayer() {
        player = createEntity();

        ShaderComponent shaderComponent = new ShaderComponent();
        shaderComponent.shader = new BorderShader(Color.BLACK);
        shaderComponent.shader.init();

        PositionComponent pos = new PositionComponent();
        pos.position = new Vector3(127.5f, 100.5f, 127.5f);

        VelocityComponent velocityComponent = new VelocityComponent();
        velocityComponent.velocity = new Vector3();

        AccelerationComponent accelerationComponent = new AccelerationComponent();
        accelerationComponent.acceleration = new Vector3();

        GravityComponent gravityComponent = new GravityComponent();
        gravityComponent.gravity = new Vector3(0, -1f, 0);

        ForceComponent forceComponent = new ForceComponent();
        forceComponent.forces = new ArrayList<>();

        ModelInstanceComponent modelInstanceComponent = new ModelInstanceComponent();
        modelInstanceComponent.instance = new ModelInstance(new ModelBuilder().createSphere(1,1,1, 50, 50,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates));

        CameraFollowComponent cameraFollowComponent = new CameraFollowComponent();

        BoundingBoxComponent boundingBoxComponent = new BoundingBoxComponent();
        BoundingBox box = new BoundingBox();
        boundingBoxComponent.boundingBox = modelInstanceComponent.instance.calculateBoundingBox(box);

        ClickableComponent clickableComponent = new ClickableComponent();
        clickableComponent.listener = new ECSClickListener(this) {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                ((BorderShader)(shaderComponent.shader)).setColor(Color.WHITE);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                ((BorderShader)(shaderComponent.shader)).setColor(Color.CLEAR);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println(event.getButton());
                if(event.getButton() == Input.Buttons.RIGHT) {
                    System.out.println("clicked");
                }
            }

            @Override
            public EventComponent getEvent() {
                return null;
            }
        };

        HitBoxComponent hitBoxComponent = new HitBoxComponent();
        hitBoxComponent.hitBox = new Rectangle(0,0, 1, 1);

        actionQueueComponent = new ActionQueueComponent();
        actionQueueComponent.actionQueue = new LinkedList<>();

        actionsComponent = new ActionsComponent();
        actionsComponent.actions = new HashSet<>();

        BodyComponent bodyComponent = new BodyComponent();
        bodyComponent.parts = new ArrayList<>();
        bodyComponent.parts.add(new HumanLeg(true, null));
        bodyComponent.parts.add(new HumanLeg(false, null));
        bodyComponent.parts.add(new HumanHand( true,5));
        bodyComponent.parts.add(new HumanHand( true, 5));

        PlayerActionController controller = new PlayerActionController(this);
        controller.addMappingByActionName("right_click_icon", "Walk");
        controller.addMappingByActionName("left_click_icon", "Pick Up");

        actionControllerComponent = new ActionControllerComponent();
        actionControllerComponent.actionController = controller;

        player.add(pos);
        player.add(modelInstanceComponent);
        player.add(cameraFollowComponent);
        player.add(shaderComponent);
        player.add(forceComponent);
        player.add(clickableComponent);
        player.add(hitBoxComponent);
        player.add(boundingBoxComponent);
        player.add(velocityComponent);
        player.add(accelerationComponent);
        player.add(gravityComponent);
        player.add(actionsComponent);
        player.add(actionQueueComponent);
        player.add(actionControllerComponent);
        player.add(bodyComponent);

        addEntity(player);
    }

    private void makeRenderer() {
        modelRenderer = createEntity();
        CameraFollowComponent cameraCanFollowComponent = new CameraFollowComponent();
        CameraComponent cameraComponent = new CameraComponent();
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 60f, Gdx.graphics.getHeight() / 60f);
        cameraComponent.camera = camera;

        cameraComponent.camera.position.set(100,100, 200);
        cameraComponent.camera.lookAt(0,0,0);
        cameraComponent.camera.near = 1;
        cameraComponent.camera.far = 1000f;
        cameraComponent.camera.update();

        EnvironmentComponent environmentComponent = new EnvironmentComponent();
        environmentComponent.environment = new Environment();
        environmentComponent.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        //environmentComponent.environment.add(new PointLight().set(1f, 1f, 1f, 300, 200, 200, 20000));
        light = new DirectionalShadowLight(1024, 1024, 60f, 60f, .1f, 50f);
        light.set(1, 1, 1f, 0.5f, -2f, 0.4f);
        environmentComponent.environment.add(light);

        SpriteBatchComponent spriteBatchComponent = new SpriteBatchComponent();
        spriteBatchComponent.spriteBatch = new SpriteBatch();

        ModelBatchComponent modelBatchComponent = new ModelBatchComponent();
        modelBatchComponent.modelBatch = new ModelBatch();

        GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
        frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);
        FrameBufferComponent frameBufferComponent = new FrameBufferComponent();
        frameBufferComponent.frameBuffer = frameBufferBuilder.build();

        postProcessesComponent = new PostProcessorsComponent();
        postProcessesComponent.processors = new ArrayList<>();
        Palette aap64 = new Palette("AAP-64", Gdx.files.local("palettes/aap-64.gpl"));
        postProcessesComponent.processors.add(new DitherPostProcess(3));
        postProcessesComponent.processors.add(new PalettePostProcess(aap64, false));
        postProcessesComponent.processors.add(new PixelizePostProcess(3));

        PositionComponent positionComponent = new PositionComponent();
        positionComponent.position = new Vector3(100, 100, 100);

        IsometricCameraComponent isometricCameraComponent = new IsometricCameraComponent();
        isometricCameraComponent.snapAngle = 90;
        isometricCameraComponent.angle = 180;
        isometricCameraComponent.distance = 300;
        isometricCameraComponent.offset = 45;
        isometricCameraComponent.target = 180;

        modelRenderer.add(cameraComponent);
        modelRenderer.add(spriteBatchComponent);
        modelRenderer.add(modelBatchComponent);
        modelRenderer.add(cameraCanFollowComponent);
        modelRenderer.add(frameBufferComponent);
        modelRenderer.add(environmentComponent);
        modelRenderer.add(positionComponent);
        modelRenderer.add(isometricCameraComponent);
        modelRenderer.add(postProcessesComponent);

        InputProcessorComponent inputProcessorComponent = new InputProcessorComponent();
        inputProcessorComponent.inputProcessor = new InputProcessor() {

            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Input.Keys.UP) {
                    cameraComponent.camera.viewportWidth *= 0.5f;
                    cameraComponent.camera.viewportHeight *= 0.5f;
                    cameraComponent.camera.update();
		        }
		        if(keycode == Input.Keys.DOWN) {
                    cameraComponent.camera.viewportWidth *= 2f;
                    cameraComponent.camera.viewportHeight *= 2f;
                    cameraComponent.camera.update();
		        }
                if(keycode == Input.Keys.LEFT) {
                    isometricCameraComponent.target += isometricCameraComponent.snapAngle;
                }
                if(keycode == Input.Keys.RIGHT) {
                    isometricCameraComponent.target -= isometricCameraComponent.snapAngle;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        };
        inputProcessorComponent.priority = 2;

        modelRenderer.add(inputProcessorComponent);

        addEntity(modelRenderer);
    }

    private void makeUISystem() {
        // UI Renderer Entity
        Entity uiRendererEntity = new Entity();

        SpriteBatchComponent sbComponent = new SpriteBatchComponent();
        sbComponent.spriteBatch = new SpriteBatch();

        ViewportComponent viewportComponent = new ViewportComponent();
        viewportComponent.viewport = new FitViewport(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        CameraComponent camComponent = new CameraComponent();
        camComponent.camera = viewportComponent.viewport.getCamera();

        uiRendererEntity.add(sbComponent);
        uiRendererEntity.add(viewportComponent);
        uiRendererEntity.add(camComponent);

        addEntity(uiRendererEntity);

        // UI Entity
        Entity stageEntity = new Entity();

        StageComponent stageComponent = new StageComponent();
        stageComponent.stage = new Stage(viewportComponent.viewport);

        InputProcessorComponent inputProcessorComponent = new InputProcessorComponent();
        inputProcessorComponent.inputProcessor = stageComponent.stage;
        inputProcessorComponent.priority = 0;

        Skin skin = new Skin(Gdx.files.local("skins/custom/custom.json"));


        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);

        ECSClickListener ecsInputListener = new ECSClickListener(this) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //modelRenderer.add(this.getEvent());
            }

            @Override
            public EventComponent getEvent() {
                EventComponentChangeComponent<PostProcessorsComponent> event = new EventComponentChangeComponent();
                event.component = postProcessesComponent;
                event.componentConsumer = component -> {
                    if(component.processors.size() == 0) {
                        component.processors.add(palettePostProcess);
                    }
                    else if (component.processors.size() == 1 ) {
                        component.processors.clear();
                        component.processors.add(ditherPostProcess);
                        component.processors.add(palettePostProcess);
                        component.processors.add(pixelizePostProcess);
                    }
                    else if(component.processors.size() == 3) {
                        component.processors.clear();
                    }
                };
                return event;
            }
        };

        Table actionTable = new Table(skin);

        Table actionQueueTable = new Table(skin) {
            int size = 0;
            @Override
            public void act(float delta) {
                super.act(delta);
                if(actionQueueComponent.actionQueue.size() != size) {
                    clear();
                    for (Action action : actionQueueComponent.actionQueue){
                        Table table = new Table(skin);
                        table.setBackground("table");
                        Image icon = new Image(action.getIcon());
                        table.add(icon);
                        add(table);
                    }
                    size = actionQueueComponent.actionQueue.size();
                }
            }
        };
        //actionQueueTable.setBackground("table");
        actionQueueTable.left();

        ScrollPane pane = new ScrollPane(actionQueueTable);
        pane.setFillParent(true);

        Table availableActionTable = new Table(skin) {
            int size = 0;

            @Override
            public void act(float delta) {
                super.act(delta);
                PlayerActionController actionController = (PlayerActionController) actionControllerComponent.actionController;
                if(size != actionController.getKeyMap().size()) {
                    clear();
                    size = actionController.getKeyMap().size();
                    for (String key : actionController.getKeyMap().keySet()) {
                        Action action = actionController.getKeyMap().get(key);
                        Table a = new Table(skin);
                        a.setBackground("table");
                        Image icon = new Image(new Texture(Gdx.files.local("ui/controls/" + key + ".png")));
                        Image i = new Image(action.getIcon());
                        a.add(icon).size(8,8);
                        a.add().row();
                        a.add();
                        a.add(i);
                        add(a);
                    }
                }
            }
        };
        availableActionTable.left();
        availableActionTable.setBackground("table");

        actionTable.add(actionQueueTable).growX().minHeight(40).row();
        actionTable.add(availableActionTable).left().minHeight(40).growX().row();

        mainTable.bottom();
        mainTable.add().expand();
        mainTable.add(actionTable).maxWidth(stageComponent.stage.getWidth() / 3).growX().bottom();
        mainTable.add().expand();

        stageComponent.stage.addActor(mainTable);

        stageEntity.add(stageComponent);
        stageEntity.add(inputProcessorComponent);

        addEntity(stageEntity);
    }

    public void makeGameEntities() {
        AssetManager assets = new AssetManager();
        assets.load("color_block2.obj", Model.class);
        assets.finishLoading();

        // Sphere Entity
        Entity sphere = new Entity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.position = new Vector3();

        ModelComponent modelComponent = new ModelComponent();
        modelComponent.model = assets.get("color_block2.obj", Model.class);
       // modelComponent.model.materials.add(new Material(ColorAttribute.createDiffuse(Color.RED)));
//        modelComponent.model = new ModelBuilder().createSphere(2, 2,2, 100, 100,
//                new Material(ColorAttribute.createDiffuse(Color.RED), ColorAttribute.createSpecular(Color.GREEN)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked);
//        modelComponent.model = new ModelBuilder().createBox(200, 200,200,
//                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
//        );

        ModelInstanceComponent modelInstanceComponent = new ModelInstanceComponent();
        modelInstanceComponent.instance = new ModelInstance(modelComponent.model);
        //modelInstanceComponent.instance.transform.scale(20, 20, 20);

        TextureComponent textureComponent = new TextureComponent();
        textureComponent.texture = new Texture("badlogic.jpg");

        sphere.add(positionComponent);
        sphere.add(modelComponent);
        sphere.add(modelInstanceComponent);

        addEntity(sphere);
    }
}
