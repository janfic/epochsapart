package com.janfic.games.dddserver.epochsapart.cards.entitycards.status;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.ModifierCard;
import com.janfic.games.dddserver.epochsapart.cards.items.ClothItemCard;

public class BleedingStatusCard extends ModifierCard {

    public BleedingStatusCard() {
        super("Bleeding");
        setFace(new TextureRegion(new Texture("cards/entitycards/status/light_bleeding.png")));
        setFaceUp(true);
    }

    @Override
    public boolean isValidCard(Card card) {
        return false;
    }

    @Override
    public void modify() {

    }
}
