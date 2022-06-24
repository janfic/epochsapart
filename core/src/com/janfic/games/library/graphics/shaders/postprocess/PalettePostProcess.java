package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PalettePostProcess extends PostProcess {

    public Palette palette;
    public boolean useHSL;
    private int u_rgb_palette_texture, u_hsl_palette_texture, u_palette_size, u_useHSL, u_enableDithering;

    public PalettePostProcess(Palette palette, boolean useHSL) {
        super(new ShaderProgram(Gdx.files.local("shaders/paletteShader.vertex.glsl"), Gdx.files.local("shaders/paletteShader.fragment.glsl")));
        this.palette = palette;
        this.useHSL = useHSL;
        u_palette_size = getProgram().getUniformLocation("u_palette_size");
        u_rgb_palette_texture = getProgram().getUniformLocation("u_rgb_palette_texture");
        u_hsl_palette_texture = getProgram().getUniformLocation("u_hsl_palette_texture");
        u_useHSL = getProgram().getUniformLocation("u_useHSL");
    }

    @Override
    protected void setUniforms(Camera camera, RenderContext renderContext) {
        super.setUniforms(camera, renderContext);
        getProgram().setUniformf(u_useHSL, useHSL ? 1.0f : 0.0f);
        getProgram().setUniformf(u_palette_size, (float) palette.size());
        getProgram().setUniformi(u_rgb_palette_texture, renderContext.textureBinder.bind(palette.getRGBTexture()));
        getProgram().setUniformi(u_hsl_palette_texture, renderContext.textureBinder.bind(palette.getHSLTexture()));
    }

}
