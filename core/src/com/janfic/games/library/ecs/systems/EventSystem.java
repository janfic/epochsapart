package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.events.EventComponentChangeComponent;
import com.janfic.games.library.ecs.components.events.EventEntityAddComponentComponent;
import com.janfic.games.library.ecs.components.events.EventQueueComponent;
import com.janfic.games.library.ecs.components.events.EventEntityRemoveComponentsComponent;

/**
 * Processes events. Events in ECS are when components are added or removed to entities. These events are put into a
 * queue and one is processes per tick. Add Events can also be used as updating a component ( used in serialization ).
 */
public class EventSystem extends EntitySystem {

    private ImmutableArray<Entity> eventQueueEntities, eventsEntities;

    private final static Family queueFamily = Family.all(EventQueueComponent.class).get();
    private final static Family eventsFamily = Family.one(EventEntityAddComponentComponent.class, EventEntityRemoveComponentsComponent.class, EventComponentChangeComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        eventQueueEntities = engine.getEntitiesFor(queueFamily);
        eventsEntities = engine.getEntitiesFor(eventsFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity eventQueueEntity : eventQueueEntities) {
            EventQueueComponent eventQueueComponent = Mapper.eventQueueComponentMapper.get(eventQueueEntity);

            // Queue Events
            for (Entity eventsEntity : eventsEntities) {
                EventComponentChangeComponent changeComponent = Mapper.eventComponentChangeComponentMapper.get(eventsEntity);
                EventEntityAddComponentComponent addComponent = Mapper.eventAddComponentComponentMapper.get(eventsEntity);
                EventEntityRemoveComponentsComponent removeComponent = Mapper.eventRemoveComponentsComponentMapper.get(eventsEntity);

                if(changeComponent != null) {
                    eventQueueComponent.events.add(changeComponent);
                    eventsEntity.remove(EventComponentChangeComponent.class);
                }

                if(addComponent != null) {
                    eventQueueComponent.events.add(addComponent);
                    eventsEntity.remove(EventEntityAddComponentComponent.class);
                }

                if(removeComponent != null) {
                    eventQueueComponent.events.add(removeComponent);
                    eventsEntity.remove(EventEntityRemoveComponentsComponent.class);
                }
            }

            // Process Events
            Component event = eventQueueComponent.events.poll();
            if(event instanceof EventComponentChangeComponent) {
                EventComponentChangeComponent changeComponent = (EventComponentChangeComponent) event;
                changeComponent.componentConsumer.accept(changeComponent.component);
            }
            if(event instanceof EventEntityAddComponentComponent) {
                EventEntityAddComponentComponent addComponent = (EventEntityAddComponentComponent) event;
                for (Component component : addComponent.components) {
                    addComponent.entity.add(component);
                }
            }
            else if(event instanceof EventEntityRemoveComponentsComponent) {
                EventEntityRemoveComponentsComponent removeComponent = (EventEntityRemoveComponentsComponent) event;
                for (Class<? extends Component> componentType : removeComponent.componentTypes) {
                    removeComponent.entity.remove(componentType);
                }
            }
        }
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
    }
}
