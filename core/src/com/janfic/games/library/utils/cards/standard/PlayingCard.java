package com.janfic.games.library.utils.cards.standard;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.janfic.games.library.utils.cards.Card;

import java.util.ArrayList;
import java.util.List;

public class PlayingCard extends Card {

    public static List<TextureRegion> cards;

    private String suit;
    private int value;

    public PlayingCard(String suit, int value){
        super("Playing Card");
        this.suit = suit;
        this.value = value;

        if(cards == null) {
            List<TextureRegion> frames = new ArrayList<>();
            Texture tex = new Texture("cards/playing_cards.png");
            int mw = 8;
            int mh = 7;
            int w = tex.getWidth() / mw;
            int h = tex.getHeight() / mh;
            for (int y = 0; y < mh; y++) {
                for (int x = 0; x < mw; x++) {
                    frames.add(new TextureRegion(tex, x * w, y * h, w, h));
                }
            }
            cards = frames;
        }
        setBounds(0,0, cards.get(0).getRegionWidth(), cards.get(0).getRegionHeight());
        setOrigin(Align.center);
    }


    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return "[" + suit + " "  + value + " "  + (isFaceUp ? "U" : "D" ) + "]";
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(isFaceUp()) {
            batch.draw(cards.get(suitToIndex(getSuit()) * 13 + getValue() + 2), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
        else {
            batch.draw(cards.get(54),  getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }

    public static int suitToIndex(String suit) {
        switch (suit) {
            case "heart":
                return 0;
            case "diamond":
                return 1;
            case "club":
                return 2;
            case "spade":
                return 3;
            default:
                return -1;
        }
    }
}
