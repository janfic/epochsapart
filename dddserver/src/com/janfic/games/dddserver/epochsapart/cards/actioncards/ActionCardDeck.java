package com.janfic.games.dddserver.epochsapart.cards.actioncards;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.Deck;
import com.janfic.games.library.utils.ScrollStage;

import java.util.List;

public class ActionCardDeck extends Deck<ActionCard> {
    int active;

    public ActionCardDeck() {
        super("Actions");
        active = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ScrollStage stage = (ScrollStage) getStage();
        setHeight(90);
        setWidth(300);
        setOrigin(Align.center);

        if(stage.getScrollFocus() == null && stage.getScrolled() != 0) {
            cards.get(active).setActive(false);
            active += stage.getScrolled();
            if(active < 0 ) {
                active = cards.size() - 1;
            }
            if(active >= cards.size()) active = 0;
            cards.get(active).setActive(true);
        }

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if(card.getParent() == this) {
                float ds = (i - active);
                float ad = Math.abs(ds);
                card.setPosition(
                        (int) (getOriginX() - 100 * Math.cos(Math.PI / 2 + ((ds / cards.size()) * Math.PI / 2)) + ds) - card.getWidth() / 2,
                        (int) (getOriginY() + 10 * Math.sin(Math.PI / 2 + ((ad / cards.size()) * Math.PI / 2))) - ad * 2 - card.getHeight() / 2);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int c = active;
        int d = cards.size();

        while(d >= 0) {
            int pi = c + d;
            int ni = c - d;
            if(pi < cards.size()) {
                Card card = cards.get(pi);
                //card.draw(batch, parentAlpha);
                card.setZIndex(cards.size());
            }
            if(ni >= 0) {
                Card card = cards.get(ni);
                //card.draw(batch, parentAlpha);
                card.setZIndex(cards.size());
            }
            d--;
        }

        super.draw(batch, parentAlpha);
    }

    public int getActive() {
        return active;
    }

    public ActionCard getActiveCard() {
        return getCards().get(getActive());
    }
}
