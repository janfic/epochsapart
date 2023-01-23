package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision._btMprSimplex_t;

import java.util.*;

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
                e.addToVertices();
                es.add(e);
            }
        }

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

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();

        Polyhedron copy = polyhedron.copy();


        List<Vertex> vs = new ArrayList<>();
        List<Edge> es = new ArrayList<>();
        List<Face> fs = new ArrayList<>();

        // Split Edges
        System.out.print("1)");
        start = System.currentTimeMillis();
        Set<Edge> marked = new HashSet<>();
        HashMap<Vertex, List<Vertex>> vertexFaceMap = new HashMap<>();
        for (Vertex vertex : copy.vertices) {
            vertexFaceMap.put(vertex, new ArrayList<>());
            for (Edge edge : vertex.edges) {
                Vertex v = edge.getVertexOnEdge(1 / 3f);
                Vertex w = edge.getVertexOnEdge(2 / 3f);
                if(!marked.contains(edge)) {
                    vs.add(v);
                    vs.add(w);
                    Edge e = new Edge(v,w);
                    e.addToVertices();
                    es.add(e);
                    marked.add(edge);
                }
                // Gather Vertices
                if(vertex == edge.a) {
                    vertexFaceMap.get(vertex).add(vs.get(vs.indexOf(v)));
                }
                else {
                    vertexFaceMap.get(vertex).add(vs.get(vs.indexOf(w)));
                }
            }
        }
        end = System.currentTimeMillis();
        System.out.println(" " + (end - start) / 1000f);

        System.out.print("2)");
        start = System.currentTimeMillis();
        // Construct Edges
        for (Map.Entry<Vertex, List<Vertex>> vertexListEntry : vertexFaceMap.entrySet()) {
            List<Vertex> value = vertexListEntry.getValue();
            Face f = Face.makeFaceFromVertices(value, es);
            List<Edge> ves = new ArrayList<>();
            for (int i = 0; i < value.size(); i++) {
                Vertex a = value.get(i);
                List<Vertex> vertexListEntryValue = vertexListEntry.getValue();
                for (int j = 0; j < vertexListEntryValue.size(); j++) {
                    if(i == j) continue;
                    Vertex b = vertexListEntryValue.get(j);
                    Edge e = new Edge(a, b);
                    if(!ves.contains(e)) ves.add(e);
                }
            }
            ves.sort((a, b) -> (int) Math.signum(a.dist() - b.dist()));
            for (int i = 0; i < value.size(); i++) {
                ves.get(i).addToVertices();
                es.add(ves.get(i));
            }
            fs.add(f);
        }
        end = System.currentTimeMillis();
        System.out.println(" " + (end - start) / 1000f);

        System.out.print("3)");
        start = System.currentTimeMillis();
        // Construct Faces
        for (Face face : copy.faces) {
            List<Vertex> vertexList = new ArrayList<>();
            for (Edge edge : face.edges) {
                Vertex a = edge.getVertexOnEdge(1 / 3f);
                Vertex b = edge.getVertexOnEdge(2 / 3f);
                vertexList.add(vs.get(vs.indexOf(a)));
                vertexList.add(vs.get(vs.indexOf(b)));
            }
            Face f = Face.makeFaceFromVertices(vertexList, es);
            fs.add(f);
        }

        end = System.currentTimeMillis();
        System.out.println(" " + (end - start) / 1000f);

        System.out.print("4)");
        start = System.currentTimeMillis();
        Polyhedron r = new Polyhedron(vs, es, fs);
        end = System.currentTimeMillis();
        System.out.println(" " + (end - start) / 1000f);
        System.out.print("5)");
        start = System.currentTimeMillis();
        r.setUp(copy.up.cpy());
        r.calculateCenter();
        end = System.currentTimeMillis();
        System.out.println(" " + (end - start) / 1000f);
        System.out.print("6)");
        start = System.currentTimeMillis();
        r.calculateNeighbors();
        end = System.currentTimeMillis();
        System.out.println(" " + (end - start) / 1000f);
        return r;
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
        System.out.print("c)");
        long start = System.currentTimeMillis();
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
            e.addToVertices();
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
        long end = System.currentTimeMillis();
        System.out.println(" " + (end - start) / 1000f);
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
            for (Edge edge : face.edges) {
                face.neighbors.add(edge.getOtherFace(face));
            }
        }
    }

    public void calculateFaces() {
        faces.clear();

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
