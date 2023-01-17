package com.janfic.games.dddserver.epochsapart;

import com.badlogic.gdx.math.Vector3;
import com.janfic.games.dddserver.epochsapart.entities.HexActor;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.*;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.library.utils.gamebuilder.GameRule;
import com.janfic.games.library.utils.gamebuilder.realtime.RealTimeGame;

import java.util.List;

public class EpochsApartGame extends RealTimeGame<EpochsApartGameState> {

    public EpochsApartGame(List<String> args) {
        this(new EpochsApartGameState(Integer.parseInt(args.get(0))));
        getGameState().setGame(this);
    }

    public EpochsApartGame(EpochsApartGameState gameState) {
        super(gameState);
        GameRule<EpochsApartGameState> playerCloseInventory = new GameRule<>(
                "Close Player Inventory",
                "Allows a player to close their own inventory",
                "This player is not in their inventory",
                (epochsApartStateChange, epochsApartGameState) -> {
                    if(!(epochsApartStateChange instanceof CloseSelfMiniGameStateChange)) return false;
                    CloseSelfMiniGameStateChange closeSelfMiniGameStateChange = (CloseSelfMiniGameStateChange) epochsApartStateChange;
                    return !epochsApartGameState.getMiniGamesForHexEntity(closeSelfMiniGameStateChange.hexID).isEmpty();
                }
        );
        GameRule<EpochsApartGameState> playerOpenInventory = new GameRule<>(
                "Open Player Inventory",
                "Allows a player to open their own inventory",
                "This player is already part of a different minigame",
                (epochsApartStateChange, epochsApartGameState) -> {
                    if(!(epochsApartStateChange instanceof OpenInventoryMiniGameStateChange)) return false;
                    OpenInventoryMiniGameStateChange openInventoryMiniGameStateChange = (OpenInventoryMiniGameStateChange) epochsApartStateChange;
                    return epochsApartGameState.getMiniGamesForHexEntity(openInventoryMiniGameStateChange.hexID).isEmpty();
                }
        );
        GameRule<EpochsApartGameState> playerEntityMiniGame = new GameRule<>(
                "Open Player Inventory",
                "Allows a player to open their own inventory",
                "This player is already part of a different minigame",
                (epochsApartStateChange, epochsApartGameState) -> {
                    if(!(epochsApartStateChange instanceof StartManageInventoryGameStateChange)) return false;
                    StartManageInventoryGameStateChange change = (StartManageInventoryGameStateChange) epochsApartStateChange;
                    return epochsApartGameState.getMiniGamesForHexEntity(change.hexID).isEmpty();
                }
        );
        GameRule<EpochsApartGameState> playerJoin = new GameRule<>(
                "Player Join Rule",
                        "Add a player to the current game",
                "Unable to add this player, posibly a id collision",
                (epochsApartStateChange, epochsApartGameState) -> {
                    if(!(epochsApartStateChange instanceof PlayerJoinGameStateChange)) return false;
                    HexActor entity = epochsApartGameState.getEntityByID(epochsApartStateChange.getID());
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
                    HexActor entity = epochsApartGameState.getEntityByID(moveStateChange.getHexID());
                    if(entity == null) return false;
                    Vector3 hexPosition = entity.getHexPosition();
                    Vector3 delta = moveStateChange.getDelta();
                    if(delta.x + delta.y + delta.z != 0) return false;
                    return !(Math.max(Math.abs(delta.x), Math.max(Math.abs(delta.y), Math.abs(delta.z))) > 1);
                }
        );
        GameRule<EpochsApartGameState> miniGameStateChanges = new GameRule<>(
                "Mini-Game State Changes",
                "Allows mini-game state changes to be requested by clients",
                "Something went wrong",
                (epochsStateChange, state) -> {
                    if(!(epochsStateChange instanceof MiniGameStateChange)) return false;
                    MiniGameStateChange miniGameStateChange = (MiniGameStateChange) epochsStateChange;
                    if(gameState.getMiniGameByID(miniGameStateChange.miniGameID) == null) return false;
                    return true;
                }
        );
        addRule(playerJoin);
        addRule(hexEntityMovement);
        addRule(playerCloseInventory);
        addRule(playerOpenInventory);
        addRule(playerEntityMiniGame);
        addRule(miniGameStateChanges);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
