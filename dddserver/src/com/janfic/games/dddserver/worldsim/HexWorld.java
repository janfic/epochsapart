package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.generators.noise_parameters.interpolation.Interpolation;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;

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
        polyhedron = Polyhedron.sphereProject(polyhedron, height / 2);

        System.out.println("generating");
        for (int i = 0; i < 7; i++) {
            polyhedron = Polyhedron.dual(polyhedron);
            polyhedron = Polyhedron.uniformTruncate(polyhedron);
            if(i == 3) polyhedron = Polyhedron.sphereProject(polyhedron, height / 2);
        }

        generateTerrain();
        System.out.println("complete");
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

    public void generateTerrain(float baseScale, Function<Float, Float> octaveFunction, int octaves) {
        PerlinNoiseGenerator generator = PerlinNoiseGenerator.newBuilder().setSeed(3301).setInterpolation(Interpolation.COSINE).build();
        float scale = baseScale;
        float steps = 10;

        int i = 0;
        do {
            for (Face face : polyhedron.faces) {
                float f = (float) generator.evaluateNoise(face.center.x * scale, face.center.y * scale, face.center.z * scale);
                f = (f + 1) / 2f;
                face.setHeight(face.height + f);
            }
            scale = octaveFunction.apply(scale);
            i++;
        } while (i < octaves);


        for (Face face : polyhedron.faces) {
            float h = face.height / octaves;

            h = MathUtils.ceil(h * steps) / steps;
            face.setHeight(h);
            if(face.height > 0.5f) {
                face.setColor(new Color(0, h + 0.1f,0,1));
            }
            else if (face.height > 0.4f) {
                face.setColor(Color.YELLOW);
            }
            else if (face.height > 0.3f) {
                face.setColor(Color.SKY);
                face.setHeight(0.4f);
            }
            else {
                face.setColor(Color.BLUE);
            }
        }
    }
}
