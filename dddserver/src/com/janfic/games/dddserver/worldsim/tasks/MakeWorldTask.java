package com.janfic.games.dddserver.worldsim.tasks;

import com.badlogic.gdx.math.MathUtils;
import com.janfic.games.dddserver.worldsim.Face;
import com.janfic.games.dddserver.worldsim.HexWorld;
import com.janfic.games.dddserver.worldsim.Polyhedron;
import com.janfic.games.dddserver.worldsim.Tile;
import com.janfic.games.library.utils.ColorRamp;
import com.janfic.games.library.utils.multithreading.Task;
import com.janfic.games.library.utils.multithreading.TaskManager;

import java.util.function.Function;

public class MakeWorldTask extends Task {

    HexWorld world;
    ColorRamp ramp;
    int steps;
    int detailLevel;
    float max;

    public MakeWorldTask(HexWorld world, ColorRamp ramp, int steps, float max, int detailLevel) {
        super("World Generation", "Generating Hex World");
        this.world = world;
        this.ramp = ramp;
        this.steps = steps;
        this.max = max;
        this.detailLevel = detailLevel;
    }

    @Override
    public void start() {
        world.uniformTruncate();
        world.setFaceCreator(face -> {
            Tile t = new Tile(face.getVertices(), face.getEdges());
            t.setHeight(face.getHeight());
            return t;
        });

        int seed = MathUtils.random(100000);
        for (int i = 0; i < detailLevel; i++) {
            world.dual();
            world.uniformTruncate();
            if (i == 4) {
                world.sphereProject(world.height / 2);
            }
            if( i > 3) {
                world.transformFaces();
                world.generateTerrain( seed, 1 / 20f, f -> f * 2, 7, 1);
                world.normalizeTerrain(world.getMinHeight(), world.getMaxHeight(), 0, max, steps);
                world.colorTerrain( 0.45f * max, ramp);
                world.makeChunks();
            }
        }

        setProgress(1);
    }
}
