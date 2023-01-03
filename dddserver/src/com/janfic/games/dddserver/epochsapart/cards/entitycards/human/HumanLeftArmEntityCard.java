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
import com.janfic.games.dddserver.epochsapart.cards.entitycards.status.BleedingStatusCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.status.LightCutStatusCard;

public class HumanLeftArmEntityCard extends EntityCard {

    float health;

    public HumanLeftArmEntityCard() {
        super("Human Left Arm");
        setFace(new TextureRegion(new Texture("cards/entitycards/entity_human_left_arm.png")));
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
