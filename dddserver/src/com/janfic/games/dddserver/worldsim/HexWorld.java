package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.janfic.games.library.utils.ColorRamp;
import de.articdive.jnoise.generators.noise_parameters.interpolation.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HexWorld {

    public Polyhedron polyhedron;
    public List<Polyhedron> polyhedra;
    public float height;
    float posX, posY;
    int level;
    ColorRamp ramp;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.height = height;
        this.level = level;

        ramp = new ColorRamp();
        float max = 4;
        float steps = 10;
        ramp.addColor(0 * max, Color.BLUE);
        ramp.addColor(0.25f * max, Color.BLUE);
        ramp.addColor(0.45f * max, Color.SKY);
        ramp.addColor(0.50f * max, Color.YELLOW);
        ramp.addColor(0.55f * max, Color.FOREST);
        ramp.addColor(0.70f * max, Color.OLIVE);
        ramp.addColor(0.85f * max, Color.SLATE);
        ramp.addColor(0.95f * max, Color.WHITE);

        polyhedra = new ArrayList<>();
        polyhedron = new RegularIcosahedron(height);
        polyhedron = Polyhedron.uniformTruncate(polyhedron);
        polyhedra.add(polyhedron);
        for (int i = 0; i < 8; i++) {
            polyhedron = Polyhedron.dual(polyhedron);
            polyhedron = Polyhedron.uniformTruncate(polyhedron);
            if (i == 3) {
                polyhedron = Polyhedron.sphereProject(polyhedron, height / 2);
            }
            if(i > 3) {
                generateTerrain(polyhedron, 1, 1 / 6f, f -> f * 2, 4, 1);
                normalizeTerrain(polyhedron, polyhedron.getMinHeight(), polyhedron.getMaxHeight(), 0, max, (int) steps);
                colorTerrain(polyhedron, 0.45f * max);
                polyhedra.add(polyhedron);
            }
        }
    }

    public Polyhedron getPolyhedronFromDistance(float distanceToCenter) {
        float rad = height / 2;
        float dif = (distanceToCenter - rad) - rad;
        float init = 3;
        int d = (int) init;
        System.out.println(polyhedra.size());
        int index = polyhedra.size() - 1;
        while(index < polyhedra.size() && index >= 0) {
            if(d * 3 > dif) {
                d *= 3;
                index--;
            }
            else {
                break;
            }
        }
        return polyhedra.get(index+1);
    }

    private void normalizeTerrain(Polyhedron polyhedron, float minHeight, float maxHeight, float newMin, float newMax, int steps) {
        float stepSize = 1f / steps;
        Function<Float, Float> transform = h -> ((h - minHeight) * (newMax - newMin)) / (maxHeight - minHeight) + newMin;
        for (Face face : polyhedron.faces) {
            face.height = transform.apply(face.height);
            float r = face.height % stepSize;
            if (r - stepSize / 2 > 0) {
                face.height += stepSize - r;
            } else {
                face.height -= r;
            }
        }
    }

    private void colorTerrain(Polyhedron polyhedron, float waterLevel) {
        for (Face face : polyhedron.faces) {
            face.setColor(ramp.getColor(face.height));
            if (face.height <= waterLevel) {
                face.setHeight(waterLevel);
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


    public void generateTerrain(Polyhedron polyhedron, int seed, float baseScale, Function<Float, Float> octaveFunction, int octaves, float amplitude) {
        PerlinNoiseGenerator generator = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.COSINE).build();
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
                f /= (i + 1);
                height += f;
                noiseScale = octaveFunction.apply(noiseScale);
            }

            face.setHeight(height);
            if (minHeight > height) minHeight = height;
            if (maxHeight < height) maxHeight = height;
            average += height;
        }
        average /= polyhedron.faces.size();
    }
}
