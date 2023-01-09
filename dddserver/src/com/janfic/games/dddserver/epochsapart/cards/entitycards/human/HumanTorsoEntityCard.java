package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanTorsoEntityCard extends EntityCard {
    public HumanTorsoEntityCard() {
        super("Human Torso");
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("torso"));
        setFaceUp(true);
    }
    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
