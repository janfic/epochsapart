package com.janfic.games.dddserver.epochsapart.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.actioncards.InspectActionCard;

import java.util.ArrayList;
import java.util.List;

public abstract class Card extends Actor implements Json.Serializable {
    protected String name;
    //protected int id;
    private TextureRegion face, back;
    private boolean isFaceUp;

    protected static List<TextureRegion> playingCards;

    public Card() {
        setSize(62,83);
        setOrigin(Align.center);
        if(playingCards == null) {
            List<TextureRegion> frames = new ArrayList<>();
            Texture tex = new Texture("cards/playing_cards.png");
            int mw = 8;
            int mh = 7;
            int w = tex.getWidth() / mw;
            int h = tex.getHeight() / mh;
            for (int y = 0; y < mh; y++) {
                for (int x = 0; x < mw; x++) {
                    frames.add(new TextureRegion(tex, x * w, y * h, w, h));
                }
            }
            playingCards = frames;
        }
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                System.out.println(name + " enter ");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                System.out.println(name + "exit");
            }
        });
    }

    public Card(String name) {
        this();
        this.name = name;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor());
        batch.draw(isFaceUp ? face : back, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setColor(Color.WHITE);
    }

    public void setBack(TextureRegion back) {
        this.back = back;
    }

    public void setFace(TextureRegion face) {
        this.face = face;
    }

    public String getName() {
        return name;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.setTypeName("class");
        json.writeType(getClass());
        json.setTypeName(null);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = json.readValue("name", String.class, jsonData);
    }

    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

}
