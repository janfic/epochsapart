package com.janfic.games.dddserver.epochsapart.cards.items;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.ItemCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.ModifierCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.status.BleedingStatusCard;

public class ClothItemCard extends ModifierCard {
    public ClothItemCard() {
        super("Cloth");
        setFace(new TextureRegion(new Texture("cards/items/cloth.png")));
        setFaceUp(true);
    }

    @Override
    public boolean isValidCard(Card card) {
        return false;
    }

    @Override
    public void modify() {
        for (ModifierCard modifierCard : getEntityCard().getModifierCards()) {
            if(modifierCard instanceof BleedingStatusCard) {
                getEntityCard().getModifierCards().remove(modifierCard);
                break;
            }
        }
    }
}
