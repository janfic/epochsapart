package com.janfic.games.dddserver.epochsapart.cards.entitycards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class TimedModifierCard extends ModifierCard{
    float timer, currentTime;
    ProgressBar bar;
    Skin skin = new Skin(Gdx.files.internal("ui/skins/default/skin/uiskin.json"));
    public TimedModifierCard(String name, float timer, float currentTime) {
        this.timer = timer;
        this.currentTime = currentTime;
        this.bar = new ProgressBar(0, timer, 0.01f, false, skin);
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
