package com.janfic.games.library.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Objects;

/**
 * An abstract Action is a way for entities to perform tasks and change the game state. An Action is performed by its
 * owning entity, and applied to a target entity. Note that these can be the same entity. Each action typically
 * takes time to complete; this is represented by the actions progress.
 * <p>
 * Action's execute begin() when starting, act(deltaTime) per frame, and end() when completed.
 * <p>
 * The Action System will not perform an Action if the owner or target are not valid according to the isValidOwner()
 * and isValidTarget()
 */
public abstract class Action {

    /*
        Member Variables
     */

    /**
     * The name of the action. Used in UI
     */
    private final String name;

    /**
     * The Action's owner.
     */
    private final Entity owner;

    /**
     * The Action's target.
     */
    private final Entity target;

    /**
     * The Action's current progress.
     */
    private float progress;

    /*
        Used in UI
     */
    TextureRegion icon;

    /**
     * @param name   the name of the action ( used as a label in the game UI )
     * @param owner  the desired owner of this action
     * @param target the desired target of this action
     */
    public Action(String name,  Entity owner, Entity target) {
        this.owner = owner;
        this.target = target;
        this.name = name;
        this.progress = -1;
    }

    public Action(String name,  TextureRegion icon, Entity owner, Entity target) {
        this(name, owner, target);
        this.icon = icon;
    }

    /**
     * Called when this action begins.
     */
    public abstract void begin();

    /**
     * Called every frame.
     *
     * @param deltaTime the amount of seconds past since last frame
     * @return the current progress of this action
     */
    public abstract float act(float deltaTime);

    /**
     * Called when the action is completed.
     */
    public abstract void end();

    /**
     * Cancels this current action.
     * Implement with components, the target, and the owner in mind.
     */
    public abstract void cancel();

    /**
     * An action may filter families of entities to be valid performers of this action.
     *
     * @param entity the entity to check validity
     * @return true if the given entity is a valid performer of this action, false otherwise.
     */
    public abstract boolean isValidOwner(Entity entity);

    /**
     * An action may filter families of entities to be valid targets of this action.
     *
     * @param entity the entity to test
     * @return true if the given entity is valid to be this action's target, false otherwise.
     */
    public abstract boolean isValidTarget(Entity entity);

    /**
     * Name Getter
     *
     * @return the action's name
     */
    public String getName() {
        return name;
    }

    /**
     * Owner Getter
     *
     * @return this action's current owner ( the performer of the action )
     */
    public Entity getOwner() {
        return owner;
    }

    /**
     * Target Getter
     *
     * @return this action's current target
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * Progress Getter
     *
     * @return a float [0-1] representing how complete this action is.
     */
    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    /**
     * Calculates if the Action is completed
     * @return true if the action is complete, false otherwise.
     */
    public boolean isComplete() {
        return progress >= 1.0f;
    }

    /**
     * Icon Getter
     * @return the actions icon ( for UI )
     */
    public TextureRegion getIcon() {
        return icon;
    }

    public void setIcon(TextureRegion icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(name, action.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
