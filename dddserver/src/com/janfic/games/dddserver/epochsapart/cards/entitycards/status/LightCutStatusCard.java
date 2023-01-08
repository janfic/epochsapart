package com.janfic.games.dddserver.epochsapart.cards.entitycards.status;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.ModifierCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.TimedModifierCard;
import com.janfic.games.dddserver.epochsapart.cards.items.ClothItemCard;

public class LightCutStatusCard extends TimedModifierCard {

    public LightCutStatusCard() {
        super("Light Cut", 10, 0);
        setFace(new TextureRegion(new Texture("cards/entitycards/status/light_cut.png")));
        setFaceUp(true);
        add(getBar()).width(62).row();
    }

    @Override
    public boolean isValidCard(Card card) {
        return card instanceof ClothItemCard;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(isTimerOver()) {

        }
    }

    @Override
    public void modify() {
        getEntityCard().addModifier(new BleedingStatusCard());
    }
}
