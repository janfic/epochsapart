package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanRightHandEntityCard extends HumanEntityCard {

    float health;

    public HumanRightHandEntityCard() {
        super("Human Right Hand", 5);
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("right_hand"));
        setFaceUp(true);
        addImage();
    }
}
