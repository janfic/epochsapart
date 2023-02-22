package com.janfic.games.dddserver.worldsim;

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
        //if(true) return super.addToMesh(attributes, vertices, indices, vertexOffset, indexOffset, renderType, polyhedron);
        Material topMaterial = materials.get(materials.size() - 1);
        int[] offsets = new int[2];

        // Top Face
        int[] delta;
        float bottom = 0;
        for (Material material : materials) {
            delta = addVerticalFace(material, bottom + material.amount, attributes, vertices, indices, vertexOffset + offsets[0], indexOffset + offsets[1], renderType, polyhedron);
            offsets[0] += delta[0];
            offsets[1] += delta[1];
            delta = addSides(material, bottom,bottom + material.amount, attributes, vertices, indices, vertexOffset + offsets[0], indexOffset + offsets[1], renderType, polyhedron);
            offsets[0] += delta[0];
            offsets[1] += delta[1];
            bottom += material.amount;
        }

        //Sides
        return offsets;
    }

    public int[] addVerticalFace(Material material, float height, VertexAttributes attributes, float[] vertices, short[] indices, int vertexOffset, int indexOffset, int renderType, Polyhedron polyhedron) {
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
            addVertex(attributes, vertices, offset, v, norm, material.color);
            offsets[0] += vertexSize;
        }
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
        return offsets;
    }

    public int[] addSides(Material material, float bottomHeight, float height, VertexAttributes attributes, float[] vertices, short[] indices, int vertexOffset, int indexOffset, int renderType, Polyhedron polyhedron) {
        Vector3 polygonCenter = polyhedron.center.cpy();
        int[] offsets = new int[2];
        int vertexSize = attributes.vertexSize / 4;
        Vector3 topCenter = new Vector3();
        for (int i = 0; i < this.vertices.size(); i++) {
            Vertex v = this.vertices.get(i).cpy();
            Vector3 vNorm = v.cpy().sub(polygonCenter).nor();
            v.add(vNorm.scl(height));
            topCenter.add(v);
        }
        topCenter.scl(1f / this.vertices.size());
        Vector3 middleCenter = new Vector3();
        middleCenter.add(topCenter);
        middleCenter.add(this.center);
        middleCenter.scl(1 / 2f);
        int index = 0;
        for (int i = 0; i < this.edges.size(); i++) {
            Edge edge = edges.get(i);
            Vertex a = edge.a.cpy();
            Vertex b = edge.b.cpy();
            Vector3 aNorm = a.cpy().sub(polygonCenter).nor();
            Vector3 bNorm = b.cpy().sub(polygonCenter).nor();
            Vertex c = new Vertex(a.cpy().add(aNorm.cpy().scl(height)));
            Vertex d = new Vertex(b.cpy().add(bNorm.cpy().scl(height)));
            a.add(aNorm.cpy().scl(bottomHeight));
            b.add(bNorm.cpy().scl(bottomHeight));
            List<Vertex> sideVectors = new ArrayList<>();
            sideVectors.add(a);
            sideVectors.add(b);
            sideVectors.add(c);
            sideVectors.add(d);
            Vector3 sideCenter = new Vector3();
            sideCenter.add(a);
            sideCenter.add(b);
            sideCenter.add(c);
            sideCenter.add(d);
            sideCenter.scl(1 / 4f);
            Vector3 norm = sideCenter.sub(middleCenter).nor();
            sortVerticesClockwise(sideVectors, norm);
            int j = (index) * vertexSize;
            int offset = j + vertexOffset;
            int iOffset = i * 6 + indexOffset;
            short aIndex = (short) (offset / (vertexSize));

            for(int k = 0; k < 4; k++) {
                addVertex(attributes, vertices, offset, sideVectors.get(k), norm, material.color);
                offsets[0] += (vertexSize);
                offset += (vertexSize);
            }
            index += 4;

            indices[iOffset + 0] = aIndex;
            indices[iOffset + 1] = (short) (aIndex + 1);
            indices[iOffset + 2] = (short) (aIndex + 2);
            indices[iOffset + 3] = aIndex;
            indices[iOffset + 4] = (short) (aIndex + 2);
            indices[iOffset + 5] = (short) (aIndex + 3);
            offsets[1] += 6;
        }
        return offsets;
    }

    @Override
    public int getMeshVertexCount() {
        return super.getMeshVertexCount() * (materials.size() * 4);
    }

    @Override
    public int getMeshIndexCount(int renderType) {
        return super.getMeshIndexCount(renderType) * (materials.size() * 4);
    }

    @Override
    public float getHeight() {
        float height = 0;
        for (Material material : materials) {
            height += material.amount;
        }
        return height;
    }

    public void update(float delta) {

    }
}
