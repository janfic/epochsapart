package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class PalettePostProcess extends PostProcess {

    private Palette palette;
    public boolean useHSL;
    private int u_palette_texture, u_palette_size, u_useHSL;

    public PalettePostProcess(Palette palette, boolean useHSL) {
        super(new ShaderProgram(Gdx.files.local("paletteShader.vertex.glsl"), Gdx.files.local("paletteShader.fragment.glsl")));
        this.palette = palette;
        this.useHSL = useHSL;
        u_palette_size = getProgram().getUniformLocation("u_palette_size");
        u_palette_texture = getProgram().getUniformLocation("u_palette_texture");
        u_useHSL = getProgram().getUniformLocation("u_useHSL");
    }

    @Override
    protected void setUniforms(Camera camera, RenderContext renderContext) {
        super.setUniforms(camera, renderContext);
        getProgram().setUniformf(u_useHSL, useHSL ? 0.0f : 1.0f);
        getProgram().setUniformf(u_palette_size, (float) palette.size());
        getProgram().setUniformi(u_palette_texture, renderContext.textureBinder.bind(palette.getTexture()));
    }

}
