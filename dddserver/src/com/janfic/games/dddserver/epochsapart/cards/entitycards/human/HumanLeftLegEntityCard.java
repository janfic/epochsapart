package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanLeftLegEntityCard extends EntityCard {

    float health;

    public HumanLeftLegEntityCard() {
        super("Human Left Leg");
        setFace(new TextureRegion(new Texture("cards/entitycards/entity_human_left_leg.png")));
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
    public void act(float delta) {
        super.act(delta);
    }
}
