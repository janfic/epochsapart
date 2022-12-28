package com.janfic.games.dddserver.epochsapart;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.entities.HexActor;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.world.HexGrid;
import com.janfic.games.library.utils.gamebuilder.GameState;

import java.util.ArrayList;
import java.util.List;

public class EpochsApartGameState extends GameState {

    HexGrid grid;
    List<HexActor> hexActors;
    List<EpochsApartMiniGame> miniGames;

    public EpochsApartGameState() {
        grid = new HexGrid();
        hexActors = new ArrayList<>();
        miniGames = new ArrayList<>();
        addActor(grid);
    }

    public EpochsApartGameState(int radius) {
        grid = new HexGrid(radius);
        hexActors = new ArrayList<>();
        miniGames = new ArrayList<>();
        addActor(grid);
    }

    @Override
    public void write(Json json) {
        json.setTypeName("class");
        json.writeType(this.getClass());
        json.setTypeName(null);
        json.writeValue("grid", grid);
        json.writeValue("hexEntities", hexActors);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        clear();
        grid = json.readValue("grid", HexGrid.class, jsonData);
        hexActors = json.readValue("hexEntities", List.class, HexActor.class, jsonData);
        addActor(grid);
        for (HexActor hexActor : hexActors) {
            addActor(hexActor);
        }
    }

    public List<EpochsApartMiniGame> getMiniGames() {
        return miniGames;
    }

    public HexActor getEntityByID(long id) {

        for (HexActor hexActor : hexActors) {
            if (hexActor.getID() == id) {
                return hexActor;
            }
        }
        return null;
    }

    public HexGrid getGrid() {
        return grid;
    }

    public List<HexActor> getHexActors() {
        return hexActors;
    }

    public void addHexEntity(HexActor hexActor) {
        hexActors.add(hexActor);
        addActor(hexActor);
    }

    public void addMiniGame(EpochsApartMiniGame miniGame) {
        miniGames.add(miniGame);
    }

    @Override
    public void reset() {
        hexActors.clear();
        miniGames.clear();
        clear();
    }

    @Override
    public void repopulate(GameState state) {
        if (!(state instanceof EpochsApartGameState)) return;
        EpochsApartGameState otherState = (EpochsApartGameState) state;
        this.grid = otherState.getGrid();
        this.miniGames.addAll(otherState.miniGames);
        this.hexActors.addAll(otherState.hexActors);
        for (HexActor actor : hexActors) {
            addActor(actor);
        }
    }
}
