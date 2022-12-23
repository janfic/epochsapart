package com.janfic.games.dddserver.epochsapart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.entities.Player;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.world.HexGrid;
import com.janfic.games.library.utils.gamebuilder.GameServerAPI;
import com.janfic.games.library.utils.gamebuilder.GameState;

import java.util.ArrayList;
import java.util.List;

public class EpochsApartGameState extends GameState {

    HexGrid grid;

    List<HexEntity> hexEntities;

    List<EpochsApartMiniGame> miniGames;

    public EpochsApartGameState() {
        grid = new HexGrid();
        hexEntities = new ArrayList<>();
        addActor(grid);
    }
    public EpochsApartGameState(int radius) {
        grid = new HexGrid(radius);
        hexEntities = new ArrayList<>();
        addActor(grid);
    }

    @Override
    public void write(Json json) {
        json.setTypeName("class");
        json.writeType(this.getClass());
        json.setTypeName(null);
        json.writeValue("grid", grid);
        json.writeValue("hexEntities", hexEntities);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        clear();
        grid = json.readValue("grid", HexGrid.class, jsonData);
        hexEntities = json.readValue("hexEntities", List.class, HexEntity.class, jsonData);
        addActor(grid);
        for (HexEntity hexEntity : hexEntities) {
            addActor(hexEntity);
        }
    }

    public List<EpochsApartMiniGame> getMiniGames() {
        return miniGames;
    }

    public HexEntity getEntityByID(long id) {

        for (HexEntity hexEntity : hexEntities) {
            if(hexEntity.getID() == id) {
                return hexEntity;
            }
        }
        return null;
    }

    public HexGrid getGrid() {
        return grid;
    }

    public List<HexEntity> getHexEntities() {
        return hexEntities;
    }

    public void addHexEntity(HexEntity hexEntity) {
        hexEntities.add(hexEntity);
        addActor(hexEntity);
    }
}
