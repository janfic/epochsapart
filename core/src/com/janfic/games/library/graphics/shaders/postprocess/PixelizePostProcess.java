package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class PixelizePostProcess extends PostProcess{

    public int pixelSize;
    final int u_pixelSize, u_screenSize;

    public PixelizePostProcess(int pixelSize) {
        super(new ShaderProgram(Gdx.files.local("shaders/pixelShader.vertex.glsl"), Gdx.files.local("shaders/pixelShader.fragment.glsl")));
        this.pixelSize = pixelSize;
        u_pixelSize = getProgram().getUniformLocation("u_pixelSize");
        u_screenSize = getProgram().getUniformLocation("u_screenSize");
    }

    @Override
    protected void setUniforms(Camera camera, RenderContext renderContext) {
        super.setUniforms(camera, renderContext);
        getProgram().setUniformf(u_pixelSize, (float)pixelSize);
        getProgram().setUniformf(u_screenSize, new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }
}
