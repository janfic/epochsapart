package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.janfic.games.dddserver.epochsapart.Assets;

public class HumanEyesEntityCard extends HumanEntityCard {
    public HumanEyesEntityCard() {
        super("Human Left Arm", 10);
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("eyes"));
        setFaceUp(true);
        addImage();
    }
}
