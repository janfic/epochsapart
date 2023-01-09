package com.janfic.games.dddserver.epochsapart.cards.entitycards.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.janfic.games.dddserver.epochsapart.Assets;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;

public class HumanLeftFootEntityCard extends EntityCard {

    float health;

    public HumanLeftFootEntityCard() {
        super("Human Left Foot");
        setFace(Assets.getSingleton().getHumanEntityCards().findRegion("left_foot"));
        setFaceUp(true);
        Table table = new Table();
        Image image = new Image(getFace());
        Label label = new Label("This is a description. This is a description. This is a description.", new Skin(Gdx.files.internal("assets/ui/skins/default/skin/uiskin.json")));
        table.add(image);
        label.setWrap(true);
        table.add(label).growY().width(200);
        setInformationTable(table);
    }
}
