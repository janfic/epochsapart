package com.janfic.games.library.ecs.components.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class FrameBufferComponent implements Component {
    public static final int DIFFUSE_ATTACHMENT = 0, DEPTH_ATTACHMENT = 1;
    public FrameBuffer frameBuffer;
}
