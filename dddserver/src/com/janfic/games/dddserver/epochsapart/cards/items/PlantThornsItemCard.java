package com.janfic.games.dddserver.epochsapart.cards.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.ItemCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.ModifierCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.status.LightCutStatusCard;

public class PlantThornsItemCard extends ModifierCard {
    public PlantThornsItemCard() {
        super("Thorns");
        setFace(new TextureRegion(new Texture("cards/items/thorns.png")));
        setFaceUp(true);
    }

    @Override
    public boolean isValidCard(Card card) {
        return false;
    }

    @Override
    public void modify() {
        getEntityCard().addModifier(new LightCutStatusCard());
        getEntityCard().getModifierCards().remove(this);
    }
}
