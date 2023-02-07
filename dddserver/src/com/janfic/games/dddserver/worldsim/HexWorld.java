package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.generators.noise_parameters.interpolation.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
        polyhedron = Polyhedron.uniformTruncate(polyhedron);
        for (int i = 0; i < 7; i++) {
            polyhedron = Polyhedron.dual(polyhedron);
            polyhedron = Polyhedron.uniformTruncate(polyhedron);
            if(i==3) {
                polyhedron = Polyhedron.sphereProject(polyhedron, height / 2);
            }
        }
        generateTerrain(1/ 8f, f -> f * 2f, 4, 1, 20);
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

    Map<Float, Color> colorRamp = new HashMap<>();

    public void generateTerrain(float baseScale, Function<Float, Float> octaveFunction, int octaves, float amplitude, int steps) {
        PerlinNoiseGenerator generator = PerlinNoiseGenerator.newBuilder().setSeed(MathUtils.random(Integer.MAX_VALUE - 1)).setInterpolation(Interpolation.COSINE).build();
        float noiseScale = baseScale;
        float stepSize = 1f / steps;

        for (Face face : polyhedron.faces) {
            float height = 0;
            noiseScale = baseScale;
            for (int i = 0; i < octaves; i++) {
                float f = (float) generator.evaluateNoise(face.center.x * noiseScale, face.center.y * noiseScale, face.center.z * noiseScale);
                f = (f + 1) / 2f;
                f /= (i+1);
                height += f;
                noiseScale = octaveFunction.apply(noiseScale);
            }
            height = (float) (Math.round(height * steps) * stepSize);
            face.setHeight(height);
        }


        for (Face face : polyhedron.faces) {
            if(face.height >= stepSize * (steps + 3)) {
                face.setColor(new Color(0, face.height / 2f,0,1));
            }
            else if (face.height >= stepSize * (steps + 1)) {
                float val = (face.height - (stepSize * (steps + 1))) / stepSize;
                face.setColor(new Color(0.8f + val / 10, 0.8f + val / 10, 0.2f - val / 10f,1));
            }
            else {
                float val =  (face.height - stepSize * steps ) / stepSize;
                face.setColor(Color.BLUE);
                face.setHeight(stepSize * (steps));

            }
        }
    }
}
