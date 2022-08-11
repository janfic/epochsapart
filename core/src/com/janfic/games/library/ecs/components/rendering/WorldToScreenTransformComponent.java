package com.janfic.games.library.ecs.components.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.components.physics.PositionComponent;

public class WorldToScreenTransformComponent implements Component {
    public WorldToScreenTransform transform;

    public interface WorldToScreenTransform {
        public PositionComponent worldToScreen(PositionComponent positionComponent, PositionComponent screenComponent);
    }

    public static class IsometricWorldTransform implements WorldToScreenTransform {

        private float tileWidth, tileHeight;

//        public IsometricWorldTransform(float tileWidth, float tileHeight) {
//            this.tileHeight = tileHeight;
//            this.tileWidth = tileWidth;
//        }

        @Override
        public PositionComponent worldToScreen(PositionComponent positionComponent, PositionComponent screenComponent) {
            Vector3 pos = positionComponent.position;
            screenComponent.position.set((2 * pos.x + pos.z) / 2f, (2 * pos.x - pos.z) / 2 + pos.y,pos.z + pos.x + pos.y);
            return screenComponent;
        }
    }
}
