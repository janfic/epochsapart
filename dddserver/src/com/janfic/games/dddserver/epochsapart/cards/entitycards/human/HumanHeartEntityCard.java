package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.attributes.IntestineAttribute;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanHeartEntityCard extends HumanEntityCard {
    float health;

    public HumanHeartEntityCard() {
        super("Human Left Arm", 40);
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("heart"));
        setFaceUp(true);
        addImage();
        addAttribute(new IntestineAttribute());
    }
}
