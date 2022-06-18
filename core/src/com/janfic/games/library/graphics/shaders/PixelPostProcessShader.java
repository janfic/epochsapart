package com.janfic.games.library.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.janfic.games.library.ecs.components.CameraComponent;

public class PixelPostProcessShader implements Shader {

    ShaderProgram program;
    int u_pixelSize, u_screenSize;

    @Override
    public void init() {
        program = new ShaderProgram(Gdx.files.local("pixelShader.vertex.glsl"), Gdx.files.local("pixelShader.fragment.glsl"));

        program.bind();
        u_pixelSize = program.getUniformLocation("u_pixelSize");
        u_screenSize = program.getUniformLocation("u_screenSize");
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

    }

    @Override
    public void render(Renderable renderable) {

    }

    @Override
    public void end() {

    }

    @Override
    public void dispose() {

    }

    public ShaderProgram getProgram() {
        program.bind();
        program.setUniformf(u_pixelSize, 5f);
        program.setUniformf(u_screenSize, new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        return program;
    }
}
