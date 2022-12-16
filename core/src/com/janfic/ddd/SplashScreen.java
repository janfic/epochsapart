package com.janfic.ddd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SplashScreen extends Stage implements Screen {

    Image splash;
    Image topChomp, bottomChomp;
    Color backgroundColor;

    float dy = 256 + 128;
    float ay = 4;
    float st = -0.5f;

    public SplashScreen() {
        super(new FitViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f));
        splash = new Image(new Texture("deepdivedev/ddd.png"));
        topChomp = new Image(new Texture("deepdivedev/top_chomp.png"));
        bottomChomp = new Image(new Texture("deepdivedev/bottom_chomp.png"));
        backgroundColor = new Color(0x231132FF);
        this.addActor(splash);
        this.addActor(topChomp);
        this.addActor(bottomChomp);

    }

    @Override
    public void show() {
        dy = splash.getHeight() * 3f;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(backgroundColor);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float scale = 2f;
        float w = splash.getWidth() * scale;
        float h = splash.getHeight() * scale;
        float tcw = topChomp.getWidth() * scale;
        float tch = topChomp.getHeight() * scale;
        float bcw = bottomChomp.getWidth() * scale;
        float bch = bottomChomp.getHeight() * scale;

        if(st < 2f && dy > 45 * scale) {
            st += delta / 2f;
        }
        else if( dy < 45 * scale ) {
            if(st > 1) st = 1;
            st -= delta * scale;
            dy -= ay * delta;
            ay = ay + scale * 2;
        }
        else {
            dy -= ay * delta;
            ay = ay + scale * 2;
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
