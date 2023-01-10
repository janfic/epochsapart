package com.janfic.games.dddserver.epochsapart.minigames.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.Deck;
import com.janfic.games.dddserver.epochsapart.entities.Inventory;
import com.janfic.games.library.utils.gamebuilder.GameState;

public class InventoryGameState extends GameState {

    Window window;
    Table table, decks, cards, cardInfo;
    ScrollPane decksScroll, cardsScroll, infoScrollPane;

    Image image;

    Inventory inventory;
    Skin skin;

    public InventoryGameState() {
        this.skin = new Skin(Gdx.files.internal("skins/epochsapart/epochsapart.json"));
        window = new Window("Inventory", skin);
        table = new Table();
        decks = new Table();
        cards = new Table();
        cards.top().left();
        cards.defaults().pad(5);
        cardInfo = new Table();
        decksScroll = new ScrollPane(decks);
        cardsScroll = new ScrollPane(cards, skin);
        infoScrollPane = new ScrollPane(cardInfo, skin);
        cardInfo.setFillParent(true);
        table.setFillParent(true);
        table.left();
        table.add(decksScroll).growY().width(100);
        Table cardTable = new Table();
        cardTable.add(cardsScroll).grow().row();
        cardTable.add(infoScrollPane).growX().height(120).row();
        table.add(cardTable).grow().row();
        table.padTop(30);
        table.padRight(16);
        window.add(table).grow();
        window.setKeepWithinStage(false);
        addActor(window);
        decks.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(decksScroll);
            }
        });
        cards.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(cardsScroll);
            }
        });
    }

    @Override
    public void update(float delta) {

    }

    public InventoryGameState(Inventory entityInventory) {
        this();
        this.inventory = entityInventory.makeCopy();
        for (Deck deck : inventory.getDecks()) {
            Image i = deck.getImage();
            decks.add(i).row();
            i.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    float width = cards.getWidth();
                    cards.clear();
                    float rowWidth = 0;

                    for (Object card : deck.getCards()) {
                        Card c = (Card) card;
                        Image im = new Image(c.getFace());
                        im.addListener(new InputListener() {
                            @Override
                            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                                cardInfo.clear();
                                cardInfo.add(c.getInformationTable());
                            }
                        });

                        float cardWidth = im.getWidth();
                        if(rowWidth + cardWidth >= width - cardWidth) {
                            rowWidth = 0;
                            cards.row();
                        }
                        cards.add(im);
                        rowWidth += cardWidth;
                    }
                }
            });
        }
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            getStage().setScrollFocus(decksScroll);
            window.setPosition(getStage().getWidth() * (1 / 8f), getStage().getHeight() * (1f / 8f));
            window.setSize(getStage().getWidth() * (3f / 4f), getStage().getHeight() * (3f / 4f));
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("inventory", inventory);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.inventory = json.readValue("inventory", Inventory.class, jsonData);
    }

    @Override
    public void reset() {

    }

    @Override
    public void repopulate(GameState state) {

    }
}
