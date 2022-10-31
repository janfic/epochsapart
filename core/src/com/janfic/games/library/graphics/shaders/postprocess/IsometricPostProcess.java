package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.janfic.games.library.ecs.components.rendering.ShaderProgramComponent;

public class IsometricPostProcess extends PostProcess{
    public IsometricPostProcess() {
        super(new ShaderProgram(Gdx.files.local("shaders/isometricShader.vertex.glsl"), Gdx.files.local("shaders/isometricShader.fragment.glsl")));

    }
}
