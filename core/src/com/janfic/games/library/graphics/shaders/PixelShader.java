package com.janfic.games.library.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PixelShader implements Shader {

    Camera camera;
    RenderContext context;
    ShaderProgram ditherProgram;

    int pixelSize;
    int dither_u_projectionViewMatrix, dither_u_worldTrans, dither_u_pixelSize;
    int pixel_u_projectionViewMatrix, pixel_u_worldTrans, pixel_u_pixelSize;


    @Override
    public void init() {
        final String ditherVERTEX = Gdx.files.local("ditherShader.vertex.glsl").readString();
        final String ditherFRAGMENT = Gdx.files.local("ditherShader.fragment.glsl").readString();

        ditherProgram = new ShaderProgram(ditherVERTEX, ditherFRAGMENT);
        pixelSize = 5;

        dither_u_projectionViewMatrix = ditherProgram.getUniformLocation("u_projectionViewMatrix");
        dither_u_worldTrans = ditherProgram.getUniformLocation("u_worldTrans");
        dither_u_pixelSize = ditherProgram.getUniformLocation("u_pixelSize");

        if (ditherProgram.getLog().length() != 0)
            System.out.println(ditherProgram.getLog());
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        ditherProgram.bind();
        ditherProgram.setUniformMatrix(dither_u_projectionViewMatrix, camera.combined);
        ditherProgram.setUniformf(dither_u_pixelSize, (float)pixelSize);
    }

    @Override
    public void render(Renderable renderable) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);

        ditherProgram.bind();
        ditherProgram.setUniformMatrix(dither_u_worldTrans, renderable.worldTransform);
        ColorAttribute colorAttribute = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
        ditherProgram.setUniformf("u_diffuseColor", colorAttribute.color);
        renderable.meshPart.render(ditherProgram);
    }

    @Override
    public void end() {

    }

    @Override
    public void dispose() {
        ditherProgram.dispose();
    }
}
