package com.janfic.games.dddserver.epochsapart.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.Player;
import com.janfic.games.dddserver.epochsapart.gamestatechanges.MoveHexEntityStateChange;
import com.janfic.games.library.utils.gamebuilder.GameClientAPI;
import com.janfic.games.library.utils.gamebuilder.GameMessage;
import com.janfic.games.library.utils.gamebuilder.GameServerAPI;

import java.util.*;

public class HexGrid extends Group implements Json.Serializable {
    List<HexTile> tiles;
    Random random;
    private Map<Vector3, HexTile> grid;

    public HexGrid() {
        tiles = new ArrayList<>();
        grid = new HashMap<>();
        random = new Random();
    }

    public HexGrid(int radius) {
        this();
        for (int q = -radius; q <= radius; q++) {
            for (int r = -radius; r <= radius; r++) {
                for (int s = -radius; s <= radius; s++) {
                    if (q + r + s == 0) {
                        int h = random.nextInt(3);
                        HexTile t = new HexTile(q, r, s, 0);
                        tiles.add(t);
                        grid.put(new Vector3(q, r, s), t);
                        addActor(t);
                    }
                }
            }
        }
    }

    HexTile lastHovered;

    @Override
    public void act(float delta) {
        super.act(delta);
        getChildren().sort((a, b) -> {
            if (a instanceof HexTile && b instanceof HexTile) {
                HexTile ha = (HexTile) a;
                HexTile hb = (HexTile) b;
                int r = (int) Math.signum(hb.getHexPosition().y - ha.getHexPosition().y);
                if(r == 0) {
                    r = (int) Math.signum(hb.getLayer() - ha.getLayer());
                }
                return r;
            }
            return 0;
        });
        Vector2 local = screenToLocalCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        HexTile t = grid.get(screenToHex(local));
        if(lastHovered != null) lastHovered.setHovered(false);
        if(t != null) {
            t.setHovered(true);
            lastHovered = t;
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && t != null) {
            EpochsApartGameState state = (EpochsApartGameState) GameServerAPI.getSingleton().getGameClient().getGameState();
            Player p = (Player) state.getEntityByID(GameServerAPI.getSingleton().getGameClient().getID());
            MoveHexEntityStateChange moveHexEntityStateChange = new MoveHexEntityStateChange(p.getID(), t.getHexPosition().cpy().sub(p.getHexPosition()));
            GameServerAPI.getSingleton().sendMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, new Json().toJson(moveHexEntityStateChange));
        }
    }

    // TODO: Reduce serialization size
    @Override
    public void write(Json json) {
        json.writeValue("tiles", tiles);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.tiles = json.readValue("tiles", List.class, HexTile.class, jsonData);
        grid = new HashMap<>();
        for (HexTile tile : tiles) {
            grid.put(tile.getHexPosition(), tile);
            addActor(tile);
        }
    }

    public Vector3 screenToHex(Vector2 stageCoords) {
        float q = 0, r = 0, s = 0;

        r = stageCoords.y / (3 * HexTile.HEX_HEIGHT / 4f);
        q = (-stageCoords.x - (r * HexTile.HEX_WIDTH / 2f)) / HexTile.HEX_WIDTH;

        q = (float) Math.floor(q + 0.5f);
        r = (float) Math.floor(r + 0.5f);

        s = -q - r;
        if(s==0) s = 0;

        return new Vector3(q, r, s);
    }
}
