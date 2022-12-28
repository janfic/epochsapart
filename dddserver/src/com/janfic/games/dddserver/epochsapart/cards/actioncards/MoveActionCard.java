package com.janfic.games.dddserver.epochsapart.cards.actioncards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.cards.ActionCard;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.entities.Player;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.MoveHexEntityStateChange;
import com.janfic.games.dddserver.epochsapart.world.HexTile;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameMessage;
import com.janfic.games.library.utils.gamebuilder.GameServerAPI;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.Random;

public class MoveActionCard extends ActionCard {
    public MoveActionCard() {
        super("Walk");
        setFace(new TextureRegion(new Texture("cards/actioncards/action_walk.png")));
        setBack(Card.playingCards.get(54));
        setFaceUp(true);
    }

    @Override
    public GameStateChange<EpochsApartGameState> performAction(GameClient client, EpochsApartGameState state) {
        Player p = (Player) state.getEntityByID(GameServerAPI.getSingleton().getGameClient().getID());
        HexTile t = state.getGrid().getLastHovered();
        if( p!= null && t != null ) {
            return new MoveHexEntityStateChange(p.getID(), t.getHexPosition().cpy().sub(p.getHexPosition()));
        }
        return null;
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.setTypeName("class");
        json.writeType(MoveActionCard.class);
        json.setTypeName(null);
    }
}
