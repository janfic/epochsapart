package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.Arrays;

public class DitherPostProcess extends PostProcess{
    int pixelSize;
    final int u_pixelSize;

    public DitherPostProcess(int pixelSize) {
        super(new ShaderProgram(Gdx.files.local("ditherShader.vertex.glsl"), Gdx.files.local("ditherShader.fragment.glsl")));
        this.pixelSize = pixelSize;
        u_pixelSize = getProgram().getUniformLocation("u_pixelSize");
    }

    @Override
    protected void setUniforms(Camera camera) {
        super.setUniforms(camera);
        getProgram().setUniformf(u_pixelSize, (float)pixelSize);
    }
}
