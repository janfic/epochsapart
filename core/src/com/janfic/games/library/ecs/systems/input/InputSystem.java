package com.janfic.games.library.ecs.systems.input;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.input.InputProcessorComponent;

import java.util.Comparator;

public class InputSystem extends SortedIteratingSystem implements EntityListener {

    private ImmutableArray<Entity> entities;

    private static final Family inputFamily = Family.all(InputProcessorComponent.class).get();

    private InputMultiplexer multiplexer;

    public InputSystem() {
        super(inputFamily, new InputProcessorComparator());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(inputFamily);
        multiplexer = new InputMultiplexer();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //multiplexer.clear();
        for (Entity entity : entities) {
            processEntity(entity, deltaTime);
        }
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        InputProcessorComponent inputProcessorComponent = Mapper.inputProcessorComponentMapper.get(entity);
        multiplexer.addProcessor(inputProcessorComponent.inputProcessor);
    }

    private static class InputProcessorComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity a, Entity b) {
            InputProcessorComponent aInputComponent = Mapper.inputProcessorComponentMapper.get(a);
            InputProcessorComponent bInputComponent = Mapper.inputProcessorComponentMapper.get(b);

            return (int) Math.signum(aInputComponent.priority - bInputComponent.priority);
        }
    }
}
