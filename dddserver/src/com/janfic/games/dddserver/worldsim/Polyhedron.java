package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision._btMprSimplex_t;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Polyhedron {
    List<Vertex> vertices;
    List<Edge> edges;
    List<Face> faces;
    Vertex center;
    Vector3 up;
    Matrix4 transform;

    public Polyhedron(List<Vertex> v, List<Edge> e, List<Face> f) {
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
        Polyhedron copy = polyhedron.copy();

        List<Vertex> vs = new ArrayList<>();
        List<Edge> es = new ArrayList<>();
        List<Face> fs = new ArrayList<>();

        for (Face face : copy.faces) {
            vs.add(new Vertex(face.center));
            for (Face neighbor : face.neighbors) {
                Edge e = new Edge(new Vertex(neighbor.center), new Vertex(face.center));
                es.add(e);
            }
        }

        // Added Face references to Vertex
        // X: Make Face.equals() to avoid duplicates in Vertex.faces
        // X: Get centers of adjacent Faces
        // X: Find associated vertices in local scope
        // X: Find associated edges in local scope
        // X: Create Face with centers and their edges

        for (Vertex vertex : copy.vertices) {
            List<Vertex> fv = new ArrayList<>();
            List<Edge> fe = new ArrayList<>();

            // Vertices
            for (Face face : vertex.faces) {
                fv.add(vs.get(vs.indexOf(new Vertex(face.center))));
            }

            // Edges
            for (int i = 0; i < fv.size(); i++) {
                Vertex va = fv.get(i);
                for (Vertex vb : fv) {
                    if(vb.equals(va)) continue;
                    Edge e = new Edge(va, vb);
                    if(es.contains(e) && !fe.contains(e)) {
                        fe.add(es.get(es.indexOf(e)));
                    }
                }
            }

            Face face = new Face(fv, fe);
            fs.add(face);
        }

        Polyhedron r = new Polyhedron(vs, es, fs);
        r.calculateCenter();
        r.calculateNeighbors();
        r.setUp(copy.up.cpy());
        return r;
    }

    public static Polyhedron uniformTruncate(Polyhedron polyhedron) {
        return null;
    }

    public Mesh makeMesh(Color color) {
        Mesh mesh = new Mesh(true, true, vertices.size(), edges.size() * 2,
                new VertexAttributes(
                        new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
                )
        );
        float[] verts = new float[vertices.size() * mesh.getVertexSize() / 4];
        for (int i = 0; i < vertices.size(); i++) {
            int j = i * 7;
            verts[j] = vertices.get(i).x;
            verts[j + 1] = vertices.get(i).y;
            verts[j + 2] = vertices.get(i).z;
            verts[j + 3] = color.r;
            verts[j + 4] = color.g;
            verts[j + 5] = color.b;
            verts[j + 6] = color.a;
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

    public List<Vertex> getVertices() {
        return vertices;
    }

    public Polyhedron copy() {
        List<Vertex> vs = new ArrayList<>();
        List<Edge> es = new ArrayList<>();
        List<Face> fs = new ArrayList<>();

        for (Vertex vertex : vertices) {
            vs.add(vertex.cpy());
        }

        for (Edge edge : edges) {
            Vertex a = vs.get(vs.indexOf(edge.a));
            Vertex b = vs.get(vs.indexOf(edge.b));

            Edge e = new Edge(a, b);
            es.add(e);
        }

        for (Face face : faces) {
            List<Vertex> cV = new ArrayList<>();
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
        polyhedron.setUp(this.up.cpy());
        polyhedron.calculateNeighbors();
        return polyhedron;
    }

    public Vector3 calculateCenter() {
        center = new Vertex();
        for (Vector3 vertex : vertices) {
            center.add(vertex);
        }
        center.scl(1f / vertices.size());
        return center;
    }

    public void calculateNeighbors() {
        for (Face face : faces) {
            for (Face other : faces) {
                if(face == other) continue;
                if(!face.neighbors.contains(other) && face.isNeighbor(other)) {
                    face.neighbors.add(other);
                }
            }
        }
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
