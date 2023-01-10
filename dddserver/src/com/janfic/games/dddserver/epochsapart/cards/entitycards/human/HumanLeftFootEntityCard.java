package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanLeftFootEntityCard extends HumanEntityCard {

    public HumanLeftFootEntityCard() {
        super("Human Left Foot",5);
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("left_foot"));
        setFaceUp(true);
        addImage();
    }
}
