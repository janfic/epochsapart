package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.Component;
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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.library.ecs.components.events.EventComponent;
import com.janfic.games.library.ecs.components.events.EventComponentChangeComponent;
import com.janfic.games.library.ecs.components.events.EventEntityAddComponentComponent;
import com.janfic.games.library.ecs.components.events.EventQueueComponent;
import com.janfic.games.library.ecs.components.input.InputProcessorComponent;
import com.janfic.games.library.ecs.components.isometric.IsometricCameraComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.*;
import com.janfic.games.library.ecs.components.ui.StageComponent;
import com.janfic.games.library.ecs.components.world.GenerateWorldComponent;
import com.janfic.games.library.ecs.systems.*;
import com.janfic.games.library.graphics.shaders.postprocess.DitherPostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.Palette;
import com.janfic.games.library.graphics.shaders.postprocess.PalettePostProcess;
import com.janfic.games.library.graphics.shaders.postprocess.PixelizePostProcess;
import com.janfic.games.library.utils.ECSClickListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;

public class ECSEngine extends Engine {

    public PostProcessorsComponent postProcessesComponent;

    Entity modelRenderer;

    public DirectionalShadowLight  light;

    public PalettePostProcess palettePostProcess;
    public DitherPostProcess ditherPostProcess;
    public PixelizePostProcess pixelizePostProcess;

    public OrthographicCamera camera;

    public ECSEngine() {

        Palette aap64 = new Palette("AAP-64", Gdx.files.local("palettes/aap-64.gpl"));
        ditherPostProcess = new DitherPostProcess(3);
        pixelizePostProcess = new PixelizePostProcess(3);
        palettePostProcess = new PalettePostProcess(aap64, false);

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
        //addEntityListener(gameRenderSystem);
        addSystem(gameRenderSystem);
        addSystem(positionSystem);
        addSystem(worldGenerationSystem);
        addSystem(userInterfaceSystem);
        addSystem(inputSystem);
        addSystem(eventSystem);
        addSystem(new IsometricCameraSystem());
        addSystem(new CameraPositionSystem());
        addSystem(new CameraFollowSystem());

        makeRenderer();
        //makeGameEntities();
        makeWorld();
        makeUISystem();
    }

    private void makeWorld() {
        Entity entity = new Entity();
        GenerateWorldComponent generateWorldComponent = new GenerateWorldComponent();
        generateWorldComponent.height = 100;
        generateWorldComponent.width = 512;
        generateWorldComponent.length = 512;
        generateWorldComponent.generationSettings = Gdx.files.local("worldGeneration/biomes/plains/plains.json");
        entity.add(generateWorldComponent);
        addEntity(entity);
    }

    private void makeRenderer() {

        Entity follow = new Entity();
        PositionComponent pos = new PositionComponent();
        pos.position = new Vector3(0, 60, 0);
        CameraFollowComponent cameraFollowComponent = new CameraFollowComponent();
        follow.add(pos);
        follow.add(cameraFollowComponent);
        addEntity(follow);

        modelRenderer = createEntity();
        CameraFollowComponent cameraCanFollowComponent = new CameraFollowComponent();
        CameraComponent cameraComponent = new CameraComponent();
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 20f, Gdx.graphics.getHeight() / 20f);
        cameraComponent.camera = camera;

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

        PositionComponent positionComponent = new PositionComponent();
        positionComponent.position = new Vector3();

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
                    cameraComponent.camera.viewportWidth *= 0.99f;
                    cameraComponent.camera.viewportHeight *= 0.99f;
                    cameraComponent.camera.update();
		        }
		        if(keycode == Input.Keys.DOWN) {
                    cameraComponent.camera.viewportWidth *= 1.01f;
                    cameraComponent.camera.viewportHeight *= 1.01f;
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

        Skin skin = new Skin(Gdx.files.local("skins/spacejunk/spaceSkin.json"));

        Table table = new Table();
        table.setFillParent(true);

        ECSClickListener ecsInputListener = new ECSClickListener(this) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                modelRenderer.add(this.getEvent());
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
                        component.processors.add(ditherPostProcess);
                    }
                    else if(component.processors.size() == 2) {
                        component.processors.add(pixelizePostProcess);
                    }
                    else if(component.processors.size() == 3) {
                        component.processors.clear();
                    }
                };
                return event;
            }
        };
        Actor actor = new TextButton("NEXT", skin);
        actor.addListener(ecsInputListener);

        table.add(actor).growX().expandY().bottom();
        stageComponent.stage.addActor(table);

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
