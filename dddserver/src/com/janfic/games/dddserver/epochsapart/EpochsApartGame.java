package com.janfic.games.dddserver.epochsapart;

import com.badlogic.gdx.math.Vector3;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.MoveHexEntityStateChange;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.PlayerJoinGameStateChange;
import com.janfic.games.library.utils.gamebuilder.Game;
import com.janfic.games.library.utils.gamebuilder.GameRule;
import com.janfic.games.library.utils.gamebuilder.realtime.RealTimeGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EpochsApartGame extends RealTimeGame<EpochsApartGameState> {

    public EpochsApartGame(List<String> args) {
        this(new EpochsApartGameState(Integer.parseInt(args.get(0))));
    }

    public EpochsApartGame(EpochsApartGameState gameState) {
        super(gameState);
        GameRule<EpochsApartGameState> playerJoin = new GameRule<>(
                "Player Join Rule",
                        "Add a player to the current game",
                "Unable to add this player, posibly a id collision",
                (epochsApartStateChange, epochsApartGameState) -> {
                    if(!(epochsApartStateChange instanceof PlayerJoinGameStateChange)) return false;
                    HexEntity entity = epochsApartGameState.getEntityByID(epochsApartStateChange.getID());
                    return entity == null;
                }
        );
        GameRule<EpochsApartGameState> hexEntityMovement = new GameRule<>(
                "Hex Entity Movement",
                "Hex Entities can only move one tile at a time.",
                "Hex Entity was moving to a non adjacent tile.",
                (epochsStateChange, epochsApartGameState) -> {
                    if(!(epochsStateChange instanceof MoveHexEntityStateChange)) return false;
                    MoveHexEntityStateChange moveStateChange = (MoveHexEntityStateChange) epochsStateChange;
                    HexEntity entity = epochsApartGameState.getEntityByID(moveStateChange.getHexID());
                    if(entity == null) return false;
                    Vector3 hexPosition = entity.getHexPosition();
                    Vector3 delta = moveStateChange.getDelta();
                    if(delta.x + delta.y + delta.z != 0) return false;
                    return !(Math.max(Math.abs(delta.x), Math.max(Math.abs(delta.y), Math.abs(delta.z))) > 1);
                }
        );
        addRule(playerJoin);
        addRule(hexEntityMovement);
    }
}
