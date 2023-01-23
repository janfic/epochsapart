package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Polyhedron {
    List<Vector3> vertices;
    List<Edge> edges;
    List<Face> faces;
    Vector3 center;
    Vector3 up;
    Matrix4 transform;

    public Polyhedron(List<Vector3> v, List<Edge> e, List<Face> f) {
        vertices = new ArrayList<>(v);
        edges = new ArrayList<>(e);
        faces = new ArrayList<>(f);
        transform = new Matrix4();
        calculateCenter();
    }

    public Polyhedron() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        faces = new ArrayList<>();
        transform = new Matrix4();
    }

    public static Polyhedron dual(Polyhedron polyhedron) {
        Polyhedron r = new Polyhedron();

        return r;
    }

    public static Polyhedron uniformTruncate(Polyhedron polyhedron) {
        return null;
    }

    public Mesh makeMesh() {
        Mesh mesh = new Mesh(true, true, vertices.size(), edges.size() * 2,
                new VertexAttributes(
                        new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
                )
        );
        float[] verts = new float[vertices.size() * mesh.getVertexSize() / 4];
        float cr =  (float) Math.random();
        float cg =  (float) Math.random();
        float cb =  (float) Math.random();
        for (int i = 0; i < vertices.size(); i++) {
            int j = i * 7;
            verts[j] = vertices.get(i).x;
            verts[j + 1] = vertices.get(i).y;
            verts[j + 2] = vertices.get(i).z;
            verts[j + 3] = cr;
            verts[j + 4] = cg;
            verts[j + 5] = cb;
            verts[j + 6] = 1;
        }
        mesh.setVertices(verts, 0, vertices.size() * (3 + 4));
        short[] indices = new short[edges.size() * 2];
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            int a = vertices.indexOf(edge.a);
            int b = vertices.indexOf(edge.b);
            int j = i * 2;
            indices[j] = (short) a;
            indices[j + 1] = (short) b;
        }
        mesh.setIndices(indices);
        mesh.transform(transform);
        return mesh;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public Polyhedron copy() {
        List<Vector3> vs = new ArrayList<>();
        List<Edge> es = new ArrayList<>();
        List<Face> fs = new ArrayList<>();

        for (Vector3 vertex : vertices) {
            vs.add(vertex.cpy());
        }

        for (Edge edge : edges) {
            Vector3 a = vs.get(vs.indexOf(edge.a));
            Vector3 b = vs.get(vs.indexOf(edge.b));

            Edge e = new Edge(a, b);
            es.add(e);
        }

        for (Face face : faces) {
            List<Vector3> cV = new ArrayList<>();
            for (Vector3 vertex : face.vertices) {
                cV.add(vs.get(vs.indexOf(vertex)));
            }
            List<Edge> cE = new ArrayList<>();
            for (Edge edge : face.edges) {
                cE.add(es.get(es.indexOf(edge)));
            }
            Face f = new Face(cV, cE);
            fs.add(f);
        }

        Polyhedron polyhedron = new Polyhedron(vs, es, fs);
        polyhedron.calculateCenter();
        return polyhedron;
    }

    public Vector3 calculateCenter() {
        center = new Vector3();
        for (Vector3 vertex : vertices) {
            center.add(vertex);
        }
        center.scl(1f / vertices.size());
        return center;
    }

    public Vector3 getCenter() {
        return center;
    }

    public void setUp(Vector3 up) {
        this.up = up;
    }

    public Vector3 getUp() {
        return up;
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
    }

    public void addTransform(Matrix4 delta) {
        this.transform.mul(delta);
    }

    public Matrix4 getTransform() {
        return transform;
    }
}
