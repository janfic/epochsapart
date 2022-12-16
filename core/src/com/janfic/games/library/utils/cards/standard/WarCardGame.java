package com.janfic.games.library.utils.cards.standard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.janfic.games.library.utils.cards.*;
import com.janfic.games.library.utils.cards.MoveCardByAmountAction.StandardMoveCardByAmountValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WarCardGame extends TurnedBasedCardGame {

    Stage stage;

    CardSlot player1Slot, player2Slot, player1Pile, player2Pile;

    public WarCardGame(List<CardGameActor> players) {
        super("War");

        List<CardSlot> slots = new ArrayList<>();
        Consumer<Card> faceUp = card -> card.setFaceUp(true);
        Consumer<Card> faceDown = card -> card.setFaceUp(false);

        player1Slot = new FieldSlot("Player 1 Slot", faceUp, card -> card.setFaceUp(false), true, 1);
        player2Slot = new FieldSlot("Player 2 Slot", faceUp, card -> card.setFaceUp(false), true,2);
        player1Pile = new FieldSlot("Player 1 Pile", faceDown, faceDown, true,3);
        player2Pile = new FieldSlot("Player 2 Pile", faceDown, faceDown, true,4);
        slots.add(new FieldSlot("Dealer Slot", card -> {}, card -> {}, true,0));
        slots.add(player1Slot);
        slots.add(player2Slot);
        slots.add(player1Pile);
        slots.add(player2Pile);

        List<Card> playingCards = new ArrayList<>();
        String[] suits = new String[] {"spade", "heart", "club", "diamond"};
        for (String suit : suits) {
            for (int i = 0; i <= 12; i++) {
                PlayingCard pc = new PlayingCard(suit, i);
                playingCards.add(pc);
            }
        }

        this.state.addActors(players);
        this.state.addSlots(slots);
        this.state.addCards(playingCards);
        this.turns.addAll(players);
        this.events.add(new CardGameEvent() {
            @Override
            public boolean isTriggered(CardGameState state) {
                return player1Slot.getSize() > 0 && player2Slot.getSize() > 0;
            }

            @Override
            public void applyStateChange(CardGameState state) {
                PlayingCard p1Card = (PlayingCard) player1Slot.popCard();
                PlayingCard p2Card = (PlayingCard) player2Slot.popCard();
                if(p1Card.getValue() >= p2Card.getValue()) {
                    player1Pile.addLast(p1Card);
                    player1Pile.addLast(p2Card);
                }
                else {
                    player2Pile.addLast(p1Card);
                    player2Pile.addLast(p2Card);
                }
            }

            @Override
            public Action changeAnimation(CardGameState state) {
                return null;
            }
        });

        this.rules.add(new CardGameRule<>(players.get(0), player1Pile, player1Slot, new StandardMoveCardByAmountValidator(1)));
        this.rules.add(new CardGameRule<>(players.get(1), player2Pile, player2Slot, new StandardMoveCardByAmountValidator(1)));

    }

    @Override
    public void setup() {
        CardSlot dealer = state.getSlotByID(0);

        for (Card card : state.getCards()) {
            dealer.addFirst(card);
        }
        dealer.shuffle();
        Card card = null;
        do {
            player1Pile.addFirst(dealer.popCard());
            player2Pile.addFirst(dealer.popCard());
        }
        while(dealer.getSize() > 0);

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        player1Pile.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MoveCardByAmountAction testAction = new MoveCardByAmountAction(state.getActors().get(0), player1Pile, player1Slot, 1);
                state.getActors().get(0).queueAction(testAction);
            }
        });
        player2Pile.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MoveCardByAmountAction testAction = new MoveCardByAmountAction(state.getActors().get(1), player2Pile, player2Slot, 1);
                state.getActors().get(1).queueAction(testAction);
            }
        });

        int width = Gdx.graphics.getWidth() / 2 - 63;
        float height = Gdx.graphics.getHeight() / 2f - player1Pile.getHeight() / 2f;
        player1Pile.setPosition(width + 200, height - 400);
        player2Pile.setPosition(width - 200 , height + 400);
        player1Slot.setPosition(width, height - 100);
        player2Slot.setPosition(width, height + 100);
        player1Pile.setMessiness(0);
        player1Pile.setStackOffset(0.5f);
        player2Pile.setStackOffset(0.5f);
        player1Slot.setMessiness(15);

        stage.addActor(player1Pile);
        stage.addActor(player2Pile);
        stage.addActor(player1Slot);
        stage.addActor(player2Slot);


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update() {
        for (CardGameActor actor : this.state.getActors()) {
            while(!actor.getQueuedActions().isEmpty()) {
                this.actionQueue.add(actor.getQueuedActions().poll());
            }
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        super.update();
    }

    @Override
    public void updateObservers() {

    }
}
