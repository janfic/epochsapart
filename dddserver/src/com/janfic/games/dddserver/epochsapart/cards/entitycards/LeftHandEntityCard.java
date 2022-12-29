package com.janfic.games.dddserver.epochsapart.cards.entitycards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;

public class LeftHandEntityCard extends EntityCard {

    float health;

    public LeftHandEntityCard() {
        super("Left Hand");
        setFace(new TextureRegion(new Texture("cards/entitycards/entity_human_left_hand.png")));
        setFaceUp(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
