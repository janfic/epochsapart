package com.janfic.games.library.utils.cards;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.util.function.Function;

public class MoveCardByAmountAction extends PlayerAction {

    private final CardSlot cardSlotA, cardSlotB;
    private final int cards;

    public MoveCardByAmountAction(CardGameActor actor, CardSlot cardSlotA, CardSlot cardSlotB, int cards) {
        super(actor);
        this.cards = cards;
        this.cardSlotA = cardSlotA;
        this.cardSlotB = cardSlotB;
    }

    public CardSlot getCardSlotA() {
        return cardSlotA;
    }

    public CardSlot getCardSlotB() {
        return cardSlotB;
    }

    public int getCards() {
        return cards;
    }

    public void applyStateChange(CardGameState state) {
        for (int i = 0; i < cards; i++) {
            Card c = state.getSlotByID(cardSlotA.id).popCard();
            state.getSlotByID(cardSlotB.id).addFirst(c);
        }
    }

    @Override
    public Action changeAnimation(CardGameState state) {
        SequenceAction sequenceAction = new SequenceAction();

        for(int i = 0; i < cards; i++) {
            Card c = state.getSlotByID(cardSlotA.id).getCards().get(i);
            Action a = Actions.moveBy(c.getParent().getX() - cardSlotB.getX(), c.getParent().getY() - cardSlotB.getY(), 1f);
            sequenceAction.addAction(a);
        }
        return sequenceAction;
    }

    public static class StandardMoveCardByAmountValidator implements Function<MoveCardByAmountAction, Boolean> {

        int amount;
        public StandardMoveCardByAmountValidator(int amount) {
            this.amount = amount;
        }

        @Override
        public Boolean apply(MoveCardByAmountAction moveCardByAmountAction) {
            return moveCardByAmountAction.getCardSlotA().getSize() >= amount && moveCardByAmountAction.cards == amount;
        }
    }
}
