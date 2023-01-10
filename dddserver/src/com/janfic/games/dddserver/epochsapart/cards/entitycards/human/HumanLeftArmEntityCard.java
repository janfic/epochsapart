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
import com.janfic.games.dddserver.epochsapart.cards.entitycards.status.BleedingStatusCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.status.LightCutStatusCard;

public class HumanLeftArmEntityCard extends HumanEntityCard {

    float health;

    public HumanLeftArmEntityCard() {
        super("Human Left Arm", 15);
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("left_arm"));
        setFaceUp(true);
        addImage();
    }
}
