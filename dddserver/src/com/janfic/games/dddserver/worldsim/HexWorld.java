package com.janfic.games.dddserver.worldsim;

import com.janfic.games.dddserver.worldsim.materials.Obsidian;
import com.janfic.games.dddserver.worldsim.materials.Stone;
import com.janfic.games.library.utils.ColorRamp;
import de.articdive.jnoise.generators.noise_parameters.interpolation.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HexWorld extends RegularIcosahedron<Tile> {

    public List<Polyhedron<Tile>> polyhedra;
    public float height;

    public HexWorld(float height) {
        super(height);
        this.height = height;
        polyhedra = new ArrayList<>();
    }

    public Polyhedron<Tile>  getPolyhedronFromDistance(float distanceToCenter) {
        float rad = height / 2;
        float dif = (distanceToCenter - rad) - rad;
        float init = 2;
        int d = (int) init;
        int index = polyhedra.size() - 1;
        while(index < polyhedra.size() && index >= 0) {
            if(d * 3 > dif || index == 0) {
                break;
            }
            else {
                d *= 3;
                index--;
            }
        }
        return polyhedra.get(index);
    }

    public void normalizeTerrain( float minHeight, float maxHeight, float newMin, float newMax, int steps) {
        float stepSize = 1f / steps;
        Function<Float, Float> transform = h -> ((h - minHeight) * (newMax - newMin)) / (maxHeight - minHeight) + newMin;
        for (Face face : faces) {
            face.height = transform.apply(face.height);
            float r = face.height % stepSize;
            if (r - stepSize / 2 > 0) {
                face.height += stepSize - r;
            } else {
                face.height -= r;
            }
        }
    }

    public void colorTerrain( float waterLevel, ColorRamp ramp) {
        for (Tile face : faces) {
            face.setColor(ramp.getColor(face.height));
            if (face.height <= waterLevel) {
                face.setHeight(waterLevel);
            }
        }
    }

    public void save() {
        polyhedra.add(copy());
    }

    public void generateTerrain(int seed, float baseScale, Function<Float, Float> octaveFunction, int octaves, float amplitude) {
        PerlinNoiseGenerator generator = PerlinNoiseGenerator.newBuilder().setSeed(seed).setInterpolation(Interpolation.COSINE).build();
        float noiseScale = baseScale;
        float minHeight = 0;
        float maxHeight = Float.MAX_VALUE;
        float average = 0;
        for (Tile face : faces) {
            float height = 0;
            noiseScale = baseScale;
            for (int i = 0; i < octaves; i++) {
                float f = (float) generator.evaluateNoise(face.center.x * noiseScale, face.center.y * noiseScale, face.center.z * noiseScale);
                f = (f + 1) / 2f;
                f /= (i + 1);
                height += f;
                noiseScale = octaveFunction.apply(noiseScale);
            }

            face.addMaterial(new Stone(height));
            face.addMaterial(new Obsidian(0.05f));
            if (minHeight > height) minHeight = height;
            if (maxHeight < height) maxHeight = height;
            average += height;
        }
        average /= faces.size();
    }
}
