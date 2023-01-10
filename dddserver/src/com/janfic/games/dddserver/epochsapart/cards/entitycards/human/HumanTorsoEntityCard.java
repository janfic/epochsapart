package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanTorsoEntityCard extends HumanEntityCard {
    public HumanTorsoEntityCard() {
        super("Human Torso", 30);
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("torso"));
        setFaceUp(true);
        addImage();
    }
}
