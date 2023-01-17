package com.janfic.games.dddserver.epochsapart.cards.entitycards.status;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.ModifierCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.TimedModifierCard;
import com.janfic.games.dddserver.epochsapart.cards.items.ClothItemCard;

public class LightCutStatusCard extends TimedModifierCard {

    public LightCutStatusCard() {
        super("Light Cut", 10, 0, false);
        setFace(new TextureRegion(new Texture("cards/entitycards/status/light_cut.png")));
        setFaceUp(true);
        getBar().setColor(Color.ORANGE);
        addImage();
        row();
        add(getBar()).width(62).height(10).pad(2).row();
    }

    @Override
    public boolean isValidCard(Card card) {
        return card instanceof ClothItemCard;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(isTimerOver()) {
            ModifierCard bleeding = null;
            for (ModifierCard modifierCard : getEntityCard().getModifierCards()) {
                if(modifierCard instanceof BleedingStatusCard) {
                    bleeding = modifierCard;
                }
            }
            if(bleeding != null) {
                bleeding.remove();
                getEntityCard().getModifierCards().remove(bleeding);
            }
        }
    }

    @Override
    public void modify() {
        getEntityCard().addModifier(new BleedingStatusCard());
    }
}
