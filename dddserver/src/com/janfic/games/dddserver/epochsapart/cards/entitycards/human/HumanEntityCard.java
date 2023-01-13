package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.attributes.FleshAttribute;
import com.janfic.games.dddserver.epochsapart.cards.attributes.HumanAttribute;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanEntityCard extends EntityCard {

    int maxHealth;
    float currentHealth;

    ProgressBar healthBar;

    public HumanEntityCard(String name, int maxHealth) {
        super(name);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        healthBar = new ProgressBar(0, maxHealth, maxHealth / (84f / 2f), true, Assets.getSingleton().getSkin());
        add(healthBar).height(86).padRight(8);
        addAttribute(new HumanAttribute());
        addAttribute(new FleshAttribute());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        healthBar.setValue(currentHealth);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("maxHealth", maxHealth);
        json.writeValue("currentHealth", currentHealth);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        maxHealth = json.readValue("maxHealth", Integer.class, jsonData);
        currentHealth = json.readValue("currentHealth", Float.class, jsonData);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        healthBar.setColor(currentHealth > maxHealth * 0.75 ? Color.GREEN : currentHealth > maxHealth * 0.25 ? Color.YELLOW : Color.SCARLET);
    }

    public ProgressBar getHealthBar() {
        return healthBar;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public void addHealth(float amount) {
        currentHealth += amount;
    }
}
