package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.attributes.IntestineAttribute;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanStomachEntityCard extends HumanEntityCard {
    public HumanStomachEntityCard() {
        super("Human Stomach", 10);
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("stomach"));
        setFaceUp(true);
        addImage();
        addAttribute(new IntestineAttribute());
    }
}
