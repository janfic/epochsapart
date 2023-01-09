package com.janfic.games.dddserver.epochsapart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {
    private static Assets singleton;

    private TextureAtlas humanEntityCards;

    private Assets() {
        this.humanEntityCards = new TextureAtlas(Gdx.files.internal("cards/entitycards/human.atlas"));
    }

    public static Assets getSingleton() {
        if(singleton == null) singleton = new Assets();
        return singleton;
    }

    public TextureAtlas getHumanEntityCards() {
        return humanEntityCards;
    }
}
