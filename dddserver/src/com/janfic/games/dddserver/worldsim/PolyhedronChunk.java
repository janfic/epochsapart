package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class PolyhedronChunk {

    private Polyhedron polyhedron;
    private List<Face> faces;
    private Vector3 chunkPoint;
    private Mesh mesh;

    private float[] vertices;
    private short[] indices;
    private int vertexCount, indexCount;

    private boolean isDirty;
    private int renderType = GL20.GL_TRIANGLES;

    public PolyhedronChunk(Polyhedron polyhedron, Vector3 chunkPoint) {
        faces = new ArrayList<>();
        this.chunkPoint = chunkPoint;
        this.polyhedron = polyhedron;
    }

    public PolyhedronChunk(Polyhedron polyhedron, List<Face> faces, Vector3 chunkPoint) {
        this.chunkPoint = chunkPoint;
        this.faces = faces;
        this.polyhedron = polyhedron;
    }

    public void setRenderType(int renderType) {
        this.renderType = renderType;
    }

    public void addFace(Face face) {
        faces.add(face);
        face.setChunk(this);
    }

    public List<Face> getFaces() {
        return faces;
    }

    public Vector3 getChunkPoint() {
        return chunkPoint;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty() {
        this.isDirty = true;
    }

    public void clean() {
        mesh();
        this.isDirty = false;
    }

    public void buildMesh() {
        VertexAttributes vertexAttributes = new VertexAttributes(
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
        );
        mesh = new Mesh(false, vertexCount, indexCount, vertexAttributes);
        mesh.setVertices(vertices, 0, vertices.length);
        mesh.setIndices(indices, 0, indices.length);
    }

    public Mesh getMesh() {
        if(mesh == null || mesh.getNumVertices() != vertexCount || mesh.getNumIndices() != indexCount) buildMesh();
        return mesh;
    }

    private void mesh() {
        int vertexCount = 0, indexCount = 0;
        for (Face face : faces) {
            indexCount += face.getMeshIndexCount(renderType);
            vertexCount += face.getMeshVertexCount();
        }
        VertexAttributes attributes = new VertexAttributes(
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
        );
        float[] vertices = new float[vertexCount * attributes.vertexSize / 4];
        int vertexOffset = 0, indexOffset = 0;
        short[] indices = new short[indexCount];
        for(Face face : faces) {
            int[] offsets = face.addToMesh(attributes, vertices, indices, vertexOffset, indexOffset, renderType, polyhedron);
            vertexOffset += offsets[0];
            indexOffset += offsets[1];
        }
        this.vertices = vertices;
        this.indices = indices;
        this.indexCount = indexCount;
        this.vertexCount = vertexCount;
    }
}
