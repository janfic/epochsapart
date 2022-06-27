package com.janfic.games.library.graphics.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import javax.swing.border.Border;

public class BorderShader implements Shader {

    ShaderProgram program;

    Color color;
    int u_projTrans;
    int u_worldTrans;
    int u_borderColor;

    public BorderShader(Color color) {
        this.color = color;
    }

    @Override
    public void init() {
        program = new ShaderProgram(Gdx.files.local("shaders/borderShader.vertex.glsl"), Gdx.files.local("shaders/borderShader.fragment.glsl"));

        u_projTrans = program.getUniformLocation("u_projTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_borderColor = program.getUniformLocation("u_borderColor");

        if (program.getLog().length() != 0)
            System.out.println(program.getLog());
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
        program.bind();
        program.setUniformMatrix(u_projTrans, camera.combined);
        program.setUniformf(u_borderColor, color);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
    }

    @Override
    public void render(Renderable renderable) {
        program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
        renderable.meshPart.render(program);
    }

    @Override
    public void end() {

    }

    @Override
    public void dispose() {
        program.dispose();
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
