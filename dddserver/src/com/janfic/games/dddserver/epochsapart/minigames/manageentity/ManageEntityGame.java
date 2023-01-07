package com.janfic.games.dddserver.epochsapart.minigames.manageentity;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.minigames.inventory.InventoryGameState;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameRule;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.List;

public class ManageEntityGame extends EpochsApartMiniGame<ManageEntityGameState> {

    public ManageEntityGame() {
        setGameState(new ManageEntityGameState());
        getGameState().setGame(this);
    }

    public ManageEntityGame(List<HexEntity> entityList) {
        super(entityList);
        setGameState(new ManageEntityGameState(entityList.get(0)));
        getGameState().setGame(this);
        GameRule<ManageEntityGameState> applyModifierCard = new GameRule<>(
                "Apply Modifier Cards Rule",
                "Players can move modifiers to their entity cards",
                "Something went wrong",
                (manageEntityStateChange, gameState) -> {
                    if(manageEntityStateChange instanceof ApplyModifierCard) return true;
                    return false;
                }
        );
        addRule(applyModifierCard);
    }

    @Override
    public void setup() {

    }

    @Override
    public GameStateChange<EpochsApartGameState> getResults() {
        return null;
    }

    @Override
    public void populate(GameClient<EpochsApartGameState> gameClient) {

    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("gameState", gameState);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        gameState = json.readValue("gameState", ManageEntityGameState.class, jsonData);
        gameState.repopulate(gameState);
    }
}
