package com.janfic.games.library;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.ECSEngine;
import com.janfic.games.library.graphics.shaders.PixelShader;

public class JanFixelDriver extends ApplicationAdapter {

	ECSEngine engine;

	Model model;
	ModelInstance instance;
	Shader shader;
	Environment environment;
	Vector3 lightPosition;

	ModelBatch modelBatch;
	SpriteBatch spriteBatch;

	OrthographicCamera orthoCamera;
	PerspectiveCamera camera;
	CameraInputController controller;

	RenderContext renderContex;
	FrameBuffer fbo1, fbo2;

	int DIFFUSE_ATTACHMENT = 0;
	int DEPTH_ATTACHMENT = 1;

	Vector2 screenSize;

	ShaderProgram pixelizeProgram, ditherProgram;
	Mesh mesh;

	@Override
	public void create () {
		//engine = new ECSEngine();
//		model = new ModelBuilder().createSphere(2, 2, 2, 100, 100,
//				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
//				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);\
		model = new ModelBuilder().createBox(2, 2, 2,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		instance = new ModelInstance(model);
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(-0.1f, -0.8f, -0.2f)));

		orthoCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthoCamera.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
		orthoCamera.update();

		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(4, 4, 4);
		camera.lookAt(0, 0, 0);
		camera.near = 1f;
		camera.far = 300f;
		camera.update();

		controller = new CameraInputController(camera);
		Gdx.input.setInputProcessor(controller);

		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();

		fbo1 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		ShaderProgram.pedantic = false;
		pixelizeProgram = new ShaderProgram(Gdx.files.local("pixelShader.vertex.glsl"), Gdx.files.local("pixelShader.fragment.glsl"));
		ditherProgram = new ShaderProgram(Gdx.files.local("ditherShader.vertex.glsl"), Gdx.files.local("ditherShader.fragment.glsl"));
		if (!pixelizeProgram.isCompiled())
			System.out.println(pixelizeProgram.getLog());

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

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

		renderContex = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN));

		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder1 = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		frameBufferBuilder1.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
		frameBufferBuilder1.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);

		fbo1 = frameBufferBuilder1.build();

		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder2 = new GLFrameBuffer.FrameBufferBuilder(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		frameBufferBuilder2.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);
		frameBufferBuilder2.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT);

		fbo2 = frameBufferBuilder2.build();

		shader = new PixelShader();
		shader.init();


		lightPosition = new Vector3(10,10,0);
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		renderContex.begin();
		fbo1.begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(camera);
		modelBatch.setCamera(camera);
		modelBatch.render(instance, environment);
		modelBatch.end();
		fbo1.end();
		renderContex.end();

		renderContex.begin();
		fbo2.begin();
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		ditherProgram.bind();
		ditherProgram.setUniformi("u_texture", renderContex.textureBinder.bind(fbo1.getTextureAttachments().get(DIFFUSE_ATTACHMENT)));
		ditherProgram.setUniformi("u_depth_buffer", renderContex.textureBinder.bind(fbo1.getTextureAttachments().get(DEPTH_ATTACHMENT)));
		ditherProgram.setUniformf("u_pixelSize", 3.0f);
		ditherProgram.setUniformMatrix("u_projTrans", orthoCamera.combined);
		mesh.render(ditherProgram, GL20.GL_TRIANGLES);
		fbo2.end();
		renderContex.end();

		pixelizeProgram.bind();
		pixelizeProgram.setUniformi("u_texture", renderContex.textureBinder.bind(fbo2.getTextureAttachments().get(DIFFUSE_ATTACHMENT)));
		pixelizeProgram.setUniformi("u_depth_buffer", renderContex.textureBinder.bind(fbo2.getTextureAttachments().get(DEPTH_ATTACHMENT)));
		pixelizeProgram.setUniformMatrix("u_projTrans", orthoCamera.combined);
		pixelizeProgram.setUniformf("u_screenSize", screenSize);
		pixelizeProgram.setUniformf("u_pixelSize", 3.0f);
		mesh.render(pixelizeProgram, GL20.GL_TRIANGLES);
	}

	@Override
	public void dispose () {
	}


}
