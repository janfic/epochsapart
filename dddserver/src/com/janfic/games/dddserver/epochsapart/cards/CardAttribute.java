package com.janfic.games.dddserver.epochsapart.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.Assets;

public abstract class CardAttribute implements Json.Serializable{
    String name;
    Color color;
    Label label;

    public CardAttribute(String name, Color color) {
        this.name = name;
        this.label = new Label(name, Assets.getSingleton().getSkin(), "entity-attribute");
        this.color = color;
        this.label.setColor(color);
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CardAttribute) return ((CardAttribute)(obj)).name.equals(name);
        if(obj instanceof String) return name.equals((String)obj);
        return super.equals(obj);
    }

    @Override
    public void write(Json json) {
        json.setTypeName("class");
        json.writeType(this.getClass());
        json.setTypeName(null);
        json.writeValue("name", name);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = json.readValue("name", String.class, jsonData);
    }
}
