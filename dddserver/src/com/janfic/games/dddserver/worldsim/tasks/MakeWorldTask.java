package com.janfic.games.dddserver.worldsim.tasks;

import com.badlogic.gdx.math.MathUtils;
import com.janfic.games.dddserver.worldsim.HexWorld;
import com.janfic.games.dddserver.worldsim.Polyhedron;
import com.janfic.games.library.utils.ColorRamp;
import com.janfic.games.library.utils.multithreading.Task;

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
        world.reset();
        world.truncate();

        int seed = MathUtils.random(100000);
        for (int i = 0; i < detailLevel; i++) {
            System.out.println(i);
            world.dual();
            world.truncate();
            if (i == 4) {
                world.sphere();
            }
            if( i > 3) {
                world.generateTerrain(world.polyhedron, seed, 1 / 20f, f -> f * 2, 7, 1);
                world.normalizeTerrain(world.polyhedron, world.polyhedron.getMinHeight(), world.polyhedron.getMaxHeight(), 0, max, steps);
                world.colorTerrain(world.polyhedron, 0.45f * max, ramp);
                world.save();
            }
        }

        setProgress(1);
    }
}
