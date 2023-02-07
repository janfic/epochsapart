package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.MathUtils;
import com.janfic.games.library.utils.ColorRamp;
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
    ColorRamp ramp;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.height = height;
        this.level = level;
        polyhedron = new RegularIcosahedron(height);
        polyhedron = Polyhedron.uniformTruncate(polyhedron);
        for (int i = 0; i < 8; i++) {
            polyhedron = Polyhedron.dual(polyhedron);
            polyhedron = Polyhedron.uniformTruncate(polyhedron);
            if(i==3) {
                polyhedron = Polyhedron.sphereProject(polyhedron, height / 2);
            }
        }
        ramp = new ColorRamp();
        ramp.addColor(0 / 20f, Color.BLUE);
        ramp.addColor(5 / 20f, Color.BLUE);
        ramp.addColor(9 / 20f, Color.SKY);
        ramp.addColor(10 / 20f, Color.YELLOW);
        ramp.addColor(11 / 20f, Color.FOREST);
        ramp.addColor(14 / 20f, Color.OLIVE);
        ramp.addColor(17 / 20f, Color.GRAY);
        ramp.addColor(19 / 20f, Color.WHITE);
        generateTerrain(1/ 4f, f -> f + 1 / 8f, 32, 1);
        normalizeTerrain(polyhedron.getMinHeight(), polyhedron.getMaxHeight(), 0, 1, 20);
        colorTerrain();
    }



    private void normalizeTerrain(float minHeight, float maxHeight, float newMin, float newMax, int steps) {
        float stepSize = 1f / steps;
        Function<Float, Float> transform = h -> ((h - minHeight) * (newMax - newMin)) / (maxHeight - minHeight) + newMin;
        for (Face face : polyhedron.faces) {
            face.height = transform.apply(face.height);
            float r = face.height % stepSize;
            if(r - stepSize / 2 > 0) {
                face.height += stepSize - r;
            }
            else {
                face.height -= r;
            }
        }
    }

    private void colorTerrain() {
        for (Face face : polyhedron.faces) {
            face.setColor(ramp.getColor(face.height));
            if(face.height <= 0.45f) {
                face.setHeight(0.45f);
            }
        }
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



    public void generateTerrain(float baseScale, Function<Float, Float> octaveFunction, int octaves, float amplitude) {
        PerlinNoiseGenerator generator = PerlinNoiseGenerator.newBuilder().setSeed(MathUtils.random(Integer.MAX_VALUE - 1)).setInterpolation(Interpolation.COSINE).build();
        float noiseScale = baseScale;
        float minHeight = 0;
        float maxHeight = Float.MAX_VALUE;
        float average = 0;
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

            face.setHeight(height);
            if(minHeight > height) minHeight = height;
            if(maxHeight < height) maxHeight = height;
            average += height;
        }
        average /= polyhedron.faces.size();
    }
}
