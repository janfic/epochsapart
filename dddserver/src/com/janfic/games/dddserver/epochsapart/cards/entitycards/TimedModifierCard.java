package com.janfic.games.dddserver.epochsapart.cards.entitycards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.janfic.games.dddserver.epochsapart.Assets;

public abstract class TimedModifierCard extends ModifierCard {
    float timer, currentTime;
    ProgressBar bar;
    Skin skin;
    public TimedModifierCard(String name, float timer, float currentTime, boolean isVerticleBar) {
        super(name);
        skin = Assets.getSingleton().getSkin();;
        this.timer = timer;
        this.currentTime = currentTime;
        this.bar = new ProgressBar(0, timer, timer / 31f, isVerticleBar, skin);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        currentTime += delta;
        bar.setValue(currentTime);
    }

    public ProgressBar getBar() {
        return bar;
    }

    public boolean isTimerOver() {
        return currentTime >= timer;
    }
}
