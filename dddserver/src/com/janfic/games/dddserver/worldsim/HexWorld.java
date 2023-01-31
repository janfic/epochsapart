package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.generators.noise_parameters.interpolation.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;

public class HexWorld {

    public Polyhedron polyhedron;

    float posX, posY;
    int level;
    public float height;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.height = height;
        this.level = level;
        polyhedron = new RegularIcosahedron(height);

    }

    public void dual() {
        polyhedron = Polyhedron.dual(polyhedron);
    }

    public void truncate() {
        polyhedron = Polyhedron.uniformTruncate(polyhedron);
    }

    public void sphere() {
        polyhedron = Polyhedron.sphereProject(polyhedron, height / 2);
    }

    public void reset() {
        polyhedron = new RegularIcosahedron(height);
    }

    public void generateTerrain() {
        PerlinNoiseGenerator generator = PerlinNoiseGenerator.newBuilder().setSeed(3301).setInterpolation(Interpolation.COSINE).build();
        float scale = 1 / 4f;
        for (Face face : polyhedron.faces) {
            float f = (float) generator.evaluateNoise(face.center.x * scale, face.center.y * scale, face.center.z * scale);
            f = (f + 1) / 2f;
            face.setHeight(f);
            if(f > 0.6f) {
                face.setColor(Color.GREEN);
            }
            else if (f > 0.5f) {
                face.setColor(Color.YELLOW);
            }
            else {
                face.setColor(Color.BLUE);
            }
        }
    }
}
