package com.janfic.games.library.utils.cards.standard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.janfic.games.library.utils.cards.Card;
import com.janfic.games.library.utils.cards.CardSlot;

import java.util.function.Consumer;

public class FieldSlot extends CardSlot {
    private final TextureRegion region;

    public FieldSlot(String name, Consumer<Card> addFirst, Consumer<Card> addLast, boolean isPile, int id) {
        super(name, addFirst, addLast, isPile, id);
        this.region = new TextureRegion(new Texture("cards/playing_cards.png"), 0,0, 125,175);
        this.setBounds(0,0, 125, 175);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(region, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }
}
