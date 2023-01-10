package com.janfic.games.dddserver.epochsapart.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

public abstract class Card extends Table implements Json.Serializable {
    protected String name;
    protected int id, deckID;
    private TextureRegion face, back;
    private Image imageFace, imageBack;
    private boolean isFaceUp;
    private List<CardAttribute> attributes;

    protected static List<TextureRegion> playingCards;

    protected Table informationTable;

    public Card() {
        attributes = new ArrayList<>();
        setOrigin(Align.center);
        setSize(62, 87);
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
    }

    public Card(String name) {
        this();
        this.name = name;
        this.id = name.hashCode();
    }

    public void update(float delta) {

    }

    public List<CardAttribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(CardAttribute cardAttribute) {
        this.attributes.add(cardAttribute);
    }

    public boolean hasAttribute(String name) {
        return attributes.contains(name);
    }

    public void setBack(TextureRegion back) {
        this.back = back;
        imageBack = new Image(back);
        imageBack.setSize(62,83);
    }

    public void setFace(TextureRegion face) {
        this.face = face;
        imageFace = new Image(face);
        imageFace.setSize(62,83);
    }

    public TextureRegion getFace() {
        return face;
    }

    public TextureRegion getBack() {
        return back;
    }

    public void setInformationTable(Table informationTable) {
        this.informationTable = informationTable;
    }

    public String getName() {
        return name;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("id", id);
        json.writeValue("deckID", deckID);
        json.writeValue("attributes", attributes);
        json.setTypeName("class");
        json.writeType(getClass());
        json.setTypeName(null);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = json.readValue("name", String.class, jsonData);
        id = json.readValue("id", Integer.class, jsonData);
        deckID = json.readValue("deckID", Integer.class, jsonData);
        attributes = json.readValue("attributes", List.class, CardAttribute.class,  jsonData);
    }

    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
        //add(isFaceUp ? imageFace : imageBack).top();
    }

    public Cell<Image> addImage() {
        return add(isFaceUp ? imageFace : imageBack).top();
    }

    public Table getInformationTable() {
        if(informationTable == null) {
            informationTable = new Table();
            informationTable.defaults().space(10);
            informationTable.add(new Image(getFace()));
            for (CardAttribute attribute : attributes) {
                informationTable.add(attribute.getLabel());
            }
        }
        return informationTable;
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        for (Actor child : getChildren()) {
            child.setColor(color);
        }
    }

    public void setDeck(Deck deck) {
        this.deckID = deck.getID();
    }

    public int getDeckID() {
        return deckID;
    }

    public int getID() {
        return id;
    }
}
