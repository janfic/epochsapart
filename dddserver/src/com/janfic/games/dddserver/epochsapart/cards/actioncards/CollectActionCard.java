package com.janfic.games.dddserver.epochsapart.cards.actioncards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

public class CollectActionCard extends ActionCard {
    public CollectActionCard() {
        super("Collect");
        setFace(new TextureRegion(new Texture("cards/actioncards/action_collect.png")));
        setBack(Card.playingCards.get(54));
        setFaceUp(true);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //System.out.println(getX() + " " + getY() + " " + getWidth() + " " + getHeight());
        //System.out.println(screenToLocalCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY())));
    }

    @Override
    public GameStateChange<EpochsApartGameState> performAction(GameClient client, EpochsApartGameState state) {
        return null;
    }

}
