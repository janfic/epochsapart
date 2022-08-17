package com.janfic.games.library.ecs.components.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class WorldToScreenTransformComponent implements Component {
    public WorldToScreenTransform transform;

    public interface WorldToScreenTransform {
        public Vector3 worldToScreen(Vector3 position, Vector3 out);
    }

    public static class IsometricWorldTransform implements WorldToScreenTransform {

        public int tileWidth, tileDepth, tileHeight;
        public IsometricWorldTransform(int tileWidth, int tileDepth, int tileHeight) {
            this.tileWidth = tileWidth;
            this.tileDepth = tileDepth;
            this.tileHeight = tileHeight;
        }

        @Override
        public Vector3 worldToScreen(Vector3 position, Vector3 out) {
            out.set( (-position.x + position.z) * tileWidth, ((position.x + position.z) ) * tileDepth + position.y * tileHeight, position.x + position.z + position.y);
            return out;
        }
    }
}
