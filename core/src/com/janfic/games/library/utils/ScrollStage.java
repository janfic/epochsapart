package com.janfic.games.library.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ScrollStage extends Stage {

    float scrolled;

    public ScrollStage(Viewport viewport) {
        super(viewport);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        scrolled = 0;
    }

    public float getScrolled() {
        return scrolled;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrolled = amountY;
        return super.scrolled(amountX, amountY);
    }
}
