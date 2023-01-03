package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;
import com.janfic.games.dddserver.epochsapart.cards.items.PlantThornsItemCard;

public class HumanLeftHandEntityCard extends EntityCard {

    Skin skin;

    public HumanLeftHandEntityCard() {
        super("Human Left Hand");
        skin = new Skin(Gdx.files.internal("ui/skins/default/skin/uiskin.json"));
        setFace(new TextureRegion(new Texture("cards/entitycards/entity_human_left_hand.png")));
        setFaceUp(true);
        Table table = new Table();
        Image image = new Image(getFace());
        Label label = new Label("This is a description. This is a description. This is a description.", new Skin(Gdx.files.internal("assets/ui/skins/default/skin/uiskin.json")));
        table.add(image);
        label.setWrap(true);
        table.add(label).growY().width(200);
        setInformationTable(table);

    }

    @Override
    public boolean isValidCard(Card card) {
        boolean b = super.isValidCard(card);
        if(card instanceof PlantThornsItemCard) return true;
        return b;
    }
}
