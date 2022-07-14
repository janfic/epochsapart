package com.janfic.games.library.body;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BodyPart {
    private String name;
    private TextureRegion image;
    private Vector2 location;
    private float maxHealth;
    private float currentHealth;
    private List<BiConsumer<Engine, Entity>> attachments, detachments;
    protected Entity owner;
    public boolean isAttached;

    public BodyPart(String name, TextureRegion image, Vector2 location, float maxHealth, float currentHealth, List<BiConsumer<Engine, Entity>> attachments,  List<BiConsumer<Engine, Entity>> detachments) {
        this.name = name;
        this.image = image;
        this.location = location;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.attachments = attachments;
        this.attachments = detachments;
    }

    public BodyPart(String name, float maxHealth) {
        this.name = name;
        this.attachments = new ArrayList<>();
        this.detachments = new ArrayList<>();
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public void setImage(TextureRegion image) {
        this.image = image;
    }


    public void setCurrentHealth(float currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void attachToEntity(Engine engine, Entity entity) {
        isAttached = true;
        for (BiConsumer<Engine, Entity> modification : attachments) {
            modification.accept(engine, entity);
        }
    }

    public void detachFromEntity(Engine engine, Entity entity) {
        isAttached = false;
        for (BiConsumer<Engine, Entity> detachment : detachments) {
            detachment.accept(engine, entity);
        }
    }

    public abstract boolean triggerDetach(Engine engine);
    public abstract boolean triggerAttach(Engine engine);

    public boolean hasOwner() {
        return owner != null;
    }

    public List<BiConsumer<Engine, Entity>> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<BiConsumer<Engine, Entity>> attachments) {
        this.attachments = attachments;
    }

    public List<BiConsumer<Engine, Entity>> getDetachments() {
        return detachments;
    }

    public void setDetachments(List<BiConsumer<Engine, Entity>> detachments) {
        this.detachments = detachments;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }
}
