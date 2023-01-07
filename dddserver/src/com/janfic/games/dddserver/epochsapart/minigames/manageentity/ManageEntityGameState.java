package com.janfic.games.dddserver.epochsapart.minigames.manageentity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.Deck;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.ModifierCard;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.MiniGameStateChange;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameMessage;
import com.janfic.games.library.utils.gamebuilder.GameServerAPI;
import com.janfic.games.library.utils.gamebuilder.GameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageEntityGameState extends GameState<ManageEntityGame> {

    Window window;
    Skin skin;

    Table table, entityCards, overview, inventory, decks, deckCards;
    Image entityImage;

    ScrollPane entityScroll;

    DragAndDrop dragAndDrop;

    HexEntity entity;

    public ManageEntityGameState() {
        this.skin = new Skin(Gdx.files.internal("ui/skins/default/skin/uiskin.json"));
        this.window = new Window("Entity", skin);
        this.table = new Table();
        this.entityCards = new Table();
        this.overview = new Table();
        this.inventory = new Table();
        this.deckCards = new Table();
        this.decks = new Table();
        table.setFillParent(true);
        table.padTop(30);
        table.left();
        table.defaults().space(5);
        deckCards.defaults().space(10);
        deckCards.pad(10);
        deckCards.left().top();
        window.setKeepWithinStage(false);
        window.add(table).grow();
        entityImage = new Image();
        entityScroll = new ScrollPane(entityCards, skin);

        entityCards.left().top();
        entityCards.defaults().space(10);
        entityCards.pad(10);

        table.add(entityImage).growX();
        table.add(entityScroll).grow().row();
        table.add(overview).growX();
        table.add(inventory).growX().minHeight(150);

        ScrollPane decksScroll = new ScrollPane(decks, skin);
        ScrollPane deckCardsScroll = new ScrollPane(deckCards, skin);
        inventory.left();
        decks.left().top();
        decks.pad(10);
        decks.defaults().space(10);
        inventory.add(decksScroll).growY().minWidth(100);
        inventory.add(deckCardsScroll).grow();

        addActor(window);
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setKeepWithinStage(false);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public ManageEntityGameState(HexEntity entity) {
        this();
        this.entity = entity;
        makeEntityCardTable();
        makeInventory();
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            window.setPosition(getStage().getWidth() * (1 / 8f), getStage().getHeight() * (1f / 8f));
            window.setSize(getStage().getWidth() * (3f / 4f), getStage().getHeight() * (3f / 4f));
        }
    }

    @Override
    public void reset() {
        entityCards.clear();
        decks.clear();
        deckCards.clear();
        dragAndDrop.clear();
    }

    public void makeInventory() {
        List<Deck> decks = entity.getInventory().getDecks();
        for (int j = 0; j < decks.size(); j++) {
            Deck deck = decks.get(j);
            if(deck.getName().equals("Action") || deck.getName().equals("Entity")) continue;
            Image i = deck.getImage();
            i.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    deckCards.clear();
                    for (Object card : deck.getCards()) {
                        Card c = (Card) card;
                        deckCards.add(c);
                        dragAndDrop.addSource(new Source(c) {
                            @Override
                            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                                Payload payload = new Payload();
                                payload.setObject(c);
                                Image i = new Image(c.getFace());
                                i.setAlign(Align.center);
                                i.setOrigin(Align.center);
                                payload.setDragActor(i);
                                return payload;
                            }
                        });
                    }
                }
            });
            if(deckCards.getChildren().isEmpty()) {
                for (Object card : deck.getCards()) {
                    Card c = (Card) card;
                    deckCards.add(c);
                    dragAndDrop.addSource(new Source(c) {
                        @Override
                        public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                            Payload payload = new Payload();
                            payload.setObject(c);
                            Image i = new Image(c.getFace());
                            i.setAlign(Align.center);
                            i.setOrigin(Align.center);
                            payload.setDragActor(i);
                            return payload;
                        }
                    });
                }
            }
            this.decks.add(i).row();
        }
    }

    public void makeEntityCardTable() {
        entity.getInventory().getEntityCardDeck().getCards().sort((a, b) -> (int) Math.signum(b.getModifierCards().size() - a.getModifierCards().size()));
        for (EntityCard card : entity.getInventory().getEntityCardDeck().getCards()) {
            entityCards.add(card).expandY().top();
            dragAndDrop.addTarget(new Target(card) {
                @Override
                public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                    if(!(payload.getObject() instanceof ModifierCard)) return false;
                    ModifierCard p = (ModifierCard) payload.getObject();
                    EntityCard entityCard = (EntityCard) getActor();
                    boolean b = entityCard.isValidCard(p);
                    getActor().setColor(b ? Color.GREEN : Color.RED);
                    return b;
                }

                @Override
                public void reset(Source source, Payload payload) {
                    super.reset(source, payload);
                    getActor().setColor(Color.WHITE);
                }

                @Override
                public void drop(Source source, Payload payload, float x, float y, int pointer) {
                    if(!(payload.getObject() instanceof ModifierCard)) return;
                    ModifierCard p = (ModifierCard) payload.getObject();
                    EntityCard entityCard = (EntityCard) getActor();
                    if(!entityCard.isValidCard(p)) return;
                    dragAndDrop.removeSource(source);
                    ApplyModifierCard stateChange = new ApplyModifierCard(entityCard, p);
                    GameClient client = GameServerAPI.getSingleton().getGameClient();
                    MiniGameStateChange<ManageEntityGameState, ApplyModifierCard> request = new MiniGameStateChange<>(ManageEntityGameState.this.getGame().miniGameID, stateChange);
                    Json json = new Json();
                    GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, json.toJson(request));
                }
            });
            for (ModifierCard modifierCard : card.getModifierCards()) {
                entityCards.add(modifierCard).expandY().top();
            }
            entityCards.row();
        }
    }

    @Override
    public void repopulate(GameState state) {
        if(!(state instanceof ManageEntityGameState)) return;
        makeEntityCardTable();
        makeInventory();
    }

    @Override
    public void write(Json json) {
        json.writeValue("entity", entity);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.entity = json.readValue("entity", HexEntity.class, jsonData);
    }
}
