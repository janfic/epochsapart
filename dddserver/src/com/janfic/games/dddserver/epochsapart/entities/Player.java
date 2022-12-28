package com.janfic.games.dddserver.epochsapart.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.actioncards.InspectActionCard;
import com.janfic.games.dddserver.epochsapart.cards.actioncards.MoveActionCard;

public class Player extends HexEntity {

    TextureRegion region;

    public Player() {
        this(-1, 0,0,0);
        region = new TextureRegion(new Texture("world/hextiles/player.png"));
        MoveActionCard actionCard = new MoveActionCard();
        getInventory().getActionCardDeck().addCard(actionCard);
        getInventory().getActionCardDeck().addCard(new MoveActionCard());
        getInventory().getActionCardDeck().addCard(new InspectActionCard());
    }

    public Player(long clientID, float q, float r, float s) {
        super(q, r, s);
        region = new TextureRegion(new Texture("world/hextiles/player.png"));
        setSize(region.getRegionWidth(), region.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setID(clientID);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor());
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.setTypeName("class");
        json.writeType(Player.class);
        json.setTypeName(null);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
    }
}
