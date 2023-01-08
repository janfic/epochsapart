package com.janfic.games.dddserver.epochsapart;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.entities.HexActor;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.world.HexGrid;
import com.janfic.games.library.utils.gamebuilder.GameState;

import java.util.ArrayList;
import java.util.List;

public class EpochsApartGameState extends GameState<EpochsApartGame> {

    HexGrid grid;
    List<HexActor> hexActors;
    List<EpochsApartMiniGame> miniGames;
    public int nextMiniGameID = 0;

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
        json.writeValue("miniGames", miniGames);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        clear();
        grid = json.readValue("grid", HexGrid.class, jsonData);
        hexActors = json.readValue("hexEntities", List.class, HexActor.class, jsonData);
        miniGames = json.readValue("miniGames", List.class, EpochsApartMiniGame.class, jsonData);
        addActor(grid);
        for (HexActor hexActor : hexActors) {
            addActor(hexActor);
        }
    }

    public List<EpochsApartMiniGame> getMiniGames() {
        return miniGames;
    }

    public void update(float delta) {
        for (EpochsApartMiniGame miniGame : miniGames) {
            miniGame.update(delta);
        }
        for (HexActor hexActor : hexActors) {
            hexActor.update(delta);
        }
    }

    public EpochsApartMiniGame getMiniGameByID(int id) {
        for (EpochsApartMiniGame miniGame : miniGames) {
            if(miniGame.getMiniGameID() == id) {
                return miniGame;
            }
        }
        return null;
    }

    public void removeMiniGame(EpochsApartMiniGame miniGame) {
        miniGames.remove(miniGame);
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

    public List<EpochsApartMiniGame> getMiniGamesForHexEntity( long hexID) {
        List<EpochsApartMiniGame> r = new ArrayList<>();
        for (EpochsApartMiniGame miniGame : miniGames) {
            if(miniGame.isEntityInvolved(hexID)) {
                r.add(miniGame);
            }
        }
        return r;
    }

    @Override
    public void reset() {
        hexActors.clear();
        miniGames.clear();
        grid.clear();
        clear();
    }

    @Override
    public void repopulate(GameState state) {
        System.out.println("REPOPULATE");
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
