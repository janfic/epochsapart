package com.janfic.games.dddserver.worldsim.materials;

import com.badlogic.gdx.graphics.Color;
import com.janfic.games.dddserver.worldsim.Material;

public class Stone extends Material {
    public Stone(float amount) {
        super("Stone", Color.GRAY, amount);
    }
}
