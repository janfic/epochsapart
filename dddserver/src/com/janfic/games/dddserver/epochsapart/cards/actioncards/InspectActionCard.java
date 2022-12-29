package com.janfic.games.dddserver.epochsapart.cards.actioncards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

public class InspectActionCard extends ActionCard {

    public InspectActionCard() {
        super("Inspect");
        setFace(new TextureRegion(new Texture("cards/actioncards/action_inspect.png")));
        setBack(Card.playingCards.get(54));
        setFaceUp(true);
    }

    @Override
    public GameStateChange<EpochsApartGameState> performAction(GameClient client, EpochsApartGameState state) {
        return null;
    }
}
