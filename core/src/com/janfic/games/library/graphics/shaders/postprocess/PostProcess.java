package com.janfic.games.library.graphics.shaders.postprocess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class PostProcess {

    private ShaderProgram program;
    private FrameBuffer frameBuffer;

    public static final int DIFFUSE_TEXTURE = 0, DEPTH_TEXTURE = 1;
    int u_texture, u_depth_buffer, u_projTrans;

    Mesh mesh;

    public PostProcess(ShaderProgram program) {
        this.program = program;

        if(program.getLog() != null) {
            System.out.println(program.getLog());
        }

        GLFrameBuffer.FrameBufferBuilder builder = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        builder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
        builder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);

        frameBuffer = builder.build();

        u_texture = program.getUniformLocation("u_texture");
        u_projTrans = program.getUniformLocation("u_projTrans");
        u_depth_buffer = program.getUniformLocation("u_depth_buffer");

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float[] verts = new float[] {
                0, height, 0, 0, 0,
                width, height, 0, 1, 0,
                0,0,0,0, 1,
                width, height, 0, 1, 9,
                width, 0, 0, 1, 1,
                0, 0, 0, 0, 1
        };

        mesh = new Mesh(true, 6, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0"));
        mesh.setVertices(verts);
    }


    public void render(FrameBuffer fbo, Camera camera, RenderContext context) {
        context.begin();
        frameBuffer.begin();
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        program.bind();
        program.setUniformi(u_texture, context.textureBinder.bind(fbo.getTextureAttachments().get(DIFFUSE_TEXTURE)));
        program.setUniformi(u_depth_buffer, context.textureBinder.bind(fbo.getTextureAttachments().get(DEPTH_TEXTURE)));
        program.setUniformMatrix(u_projTrans, camera.combined);
        setUniforms(camera, context);
        mesh.render(program, GL20.GL_TRIANGLES);
        frameBuffer.end();
        context.end();
    }

    protected void setUniforms(Camera camera, RenderContext renderContext) {
    }

    public ShaderProgram getProgram() {
        return program;
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }
}
