package com.janfic.games.dddserver.epochsapart.cards;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.library.utils.ScrollStage;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class ActionDeck extends Deck<ActionCard> {
    int active;

    public ActionDeck() {
        super("Actions");
        active = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ScrollStage stage = (ScrollStage) getStage();
        if(stage.getScrolled() != 0) {
            cards.get(active).setActive(false);
            active += stage.getScrolled();
            if(active < 0 ) {
                active = cards.size() - 1;
            }
            if(active >= cards.size()) active = 0;
            cards.get(active).setActive(true);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int c = active;
        int d = cards.size();

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            float ds = (i - active);
            float ad = Math.abs(ds);
            card.setPosition(
                    (int) (getX() + getOriginX() - 100 * Math.cos(Math.PI / 2 + ((ds / cards.size()) * Math.PI / 2)) + ds),
                    (int) (getY() + getOriginY() + 10 * Math.sin(Math.PI / 2 + ((ad / cards.size()) * Math.PI / 2))) - ad * 2);
        }

        while(d >= 0) {
            int pi = c + d;
            int ni = c - d;
            if(pi < cards.size()) {
                Card card = cards.get(pi);
                card.draw(batch, parentAlpha);
            }
            if(ni >= 0) {
                Card card = cards.get(ni);
                card.draw(batch, parentAlpha);
            }
            d--;
        }
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.setTypeName("class");
        json.writeType(ActionDeck.class);
        json.setTypeName(null);
    }

    public int getActive() {
        return active;
    }

    public ActionCard getActiveCard() {
        return getCards().get(getActive());
    }
}
