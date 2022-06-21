package com.janfic.games.library.ecs.components.rendering;

import com.badlogic.ashley.core.Component;
import com.janfic.games.library.graphics.shaders.postprocess.PostProcess;

import java.util.List;

public class PostProcessorsComponent implements Component {
    public List<PostProcess> processors;
}
