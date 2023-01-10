package com.janfic.games.dddserver.epochsapart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
    private static Assets singleton;

    private TextureAtlas humanEntityCards;

    private Skin skin;

    private Assets() {
        this.humanEntityCards = new TextureAtlas(Gdx.files.internal("cards/entitycards/human.atlas"));
        this.skin = new Skin(Gdx.files.internal("skins/epochsapart/epochsapart.json"));
        ProgressBar.ProgressBarStyle style_hori = new ProgressBar.ProgressBarStyle();
        TextureRegion t = new TextureRegion(new Texture("skins/epochsapart/progress_bar_before_horizontal.png"));
        t.setRegion(0,0, t.getRegionWidth() * 10, t.getRegionHeight());
        t.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        style_hori.background = skin.getDrawable("progress_bar");
        style_hori.knobBefore = new TextureRegionDrawable(t) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                getRegion().setRegion(0,0, (int)width, (int)height);
                super.draw(batch, x, y, width, height);
            }
        };

        skin.add("default-horizontal", style_hori);

        ProgressBar.ProgressBarStyle style_vert = new ProgressBar.ProgressBarStyle();
        TextureRegion v = new TextureRegion(new Texture("skins/epochsapart/progress_bar_before_vertical.png"));
        v.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        style_vert.background = skin.getDrawable("progress_bar_vertical");
        style_vert.knobBefore = new TextureRegionDrawable(v) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                getRegion().setRegion(0,0, (int)width, (int)height);
                super.draw(batch, x, y, width, height);
            }
        };
        skin.add("default-vertical", style_vert);
    }

    public static Assets getSingleton() {
        if(singleton == null) singleton = new Assets();
        return singleton;
    }

    public TextureAtlas getHumanEntityCards() {
        return humanEntityCards;
    }

    public Skin getSkin() {
        return skin;
    }
}
