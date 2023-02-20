package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Tile extends Face {

    List<Material> materials;

    public Tile(List<Vertex> vertices, List<Edge> edges) {
        super(vertices, edges);
        this.materials = new ArrayList<>();
    }

    public void addMaterial(Material material) {
        materials.add(material);
    }

    @Override
    public int[] addToMesh(VertexAttributes attributes, float[] vertices, short[] indices, int vertexOffset, int indexOffset, int renderType, Polyhedron polyhedron) {
        Vector3 center = polyhedron.center.cpy();
        int[] offsets = new int[2];
        int vertexSize = attributes.vertexSize / 4;
        for (int i = 0; i < this.vertices.size(); i++) {
            Vertex v = this.vertices.get(i).cpy();
            Vector3 norm = this.center.cpy().sub(center).nor();
            Vector3 vNorm = v.cpy().sub(center).nor();
            v.add(vNorm.cpy().scl(height));
            int j = i * vertexSize;
            int offset = j + vertexOffset;
            addVertex(attributes, vertices, offset, v, norm, color == null ? Color.WHITE : color);
            offsets[0] += vertexSize;
        }
        if (renderType == GL20.GL_TRIANGLES) {
            Vertex v = this.vertices.get(0);
            int index = 0;
            for (int j = 1; j < this.vertices.size() - 1; j++) {
                Vertex b = this.vertices.get(j);
                Vertex c = this.vertices.get(j + 1);
                Plane plane = new Plane(v, b, c);
                if (!plane.isFrontFacing(this.center.cpy().sub(polyhedron.center))) {
                    indices[indexOffset + index] = (short) (vertexOffset / (vertexSize) + this.vertices.indexOf(v));
                    indices[indexOffset + index + 1] = (short) (vertexOffset / (vertexSize) + this.vertices.indexOf(b));
                    indices[indexOffset + index + 2] = (short) (vertexOffset / (vertexSize) + this.vertices.indexOf(c));
                } else {
                    indices[indexOffset + index] = (short) (vertexOffset / (vertexSize) + this.vertices.indexOf(v));
                    indices[indexOffset + index + 1] = (short) (vertexOffset / (vertexSize) + this.vertices.indexOf(c));
                    indices[indexOffset + index + 2] = (short) (vertexOffset / (vertexSize) + this.vertices.indexOf(b));
                }
                index += 3;
            }
            offsets[1] += index;
        }
        return offsets;
    }

    public void update(float delta) {

    }
}
