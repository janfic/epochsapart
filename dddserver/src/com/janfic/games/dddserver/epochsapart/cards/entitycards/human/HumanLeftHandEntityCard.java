package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;
import com.janfic.games.dddserver.epochsapart.cards.items.PlantThornsItemCard;

public class HumanLeftHandEntityCard extends HumanEntityCard {

    Skin skin;

    public HumanLeftHandEntityCard() {
        super("Human Left Hand", 5);
        skin = new Skin(Gdx.files.internal("ui/skins/default/skin/uiskin.json"));
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("left_hand"));
        setFaceUp(true);
        addImage();
    }

    @Override
    public boolean isValidCard(Card card) {
        boolean b = super.isValidCard(card);
        if(card instanceof PlantThornsItemCard) return true;
        return b;
    }
}
