package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.*;
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

        Map<Vector3, Set<Vertex>> map = new HashMap<>();

        for (Face face : copy.faces) {
            vs.add(new Vertex(face.center));
        }

        for (Vertex vertex : copy.vertices) {
            Set<Vertex> vertexSet = new HashSet<>();
            map.put(vertex, vertexSet);
            for (Face face : vertex.faces) {
                vertexSet.add(new Vertex(face.center));
            }
        }

        for (Map.Entry<Vector3, Set<Vertex>> entry : map.entrySet()) {
            Vector3 key = entry.getKey();
            Set<Vertex> vertexSet = entry.getValue();
            Face face = Face.makeFaceFromVertices(new ArrayList<>(vertexSet), es, vertexSet.size());
            for (Edge edge : face.edges) {
                if(!es.contains(edge))
                    es.add(edge);
            }
            fs.add(face);
        }

        Polyhedron r = new Polyhedron(vs, es, fs);
        r.calculateCenter();
        r.calculateNeighbors();
        r.setUp(copy.up.cpy());
        return r;
    }

    public static Polyhedron uniformTruncate(Polyhedron polyhedron) {
        Polyhedron copy = polyhedron.copy();

        List<Vertex> vs = new ArrayList<>();
        List<Edge> es = new ArrayList<>();
        List<Face> fs = new ArrayList<>();

        Map<Vector3, Set<Vertex>> map = new HashMap<>();

        // Create Edges
        for (Edge edge : copy.edges) {
            Vertex a = edge.a;
            Vertex b = edge.b;
            Face f = edge.faces.get(0);
            Face g = edge.faces.get(1);
            Vector3 c = f.center;
            Vector3 d = g.center;

            Vertex s = edge.getVertexOnEdge(1  / 3f);
            Vertex t = edge.getVertexOnEdge(2  / 3f);

            vs.add(s);
            vs.add(t);

            Edge e = new Edge(s, t);
            es.add(e);

            if(!map.containsKey(a))
                map.put(a, new HashSet<>());
            if(!map.containsKey(b))
                map.put(b, new HashSet<>());
            if(!map.containsKey(c))
                map.put(c, new HashSet<>());
            if(!map.containsKey(d))
                map.put(d, new HashSet<>());

            map.get(a).add(s);
            map.get(b).add(t);
            map.get(c).add(s);
            map.get(c).add(t);
            map.get(d).add(s);
            map.get(d).add(t);
        }


        for (Map.Entry<Vector3, Set<Vertex>> entry : map.entrySet()) {
            Vector3 key = entry.getKey();
            Set<Vertex> vertexSet = entry.getValue();
            Face face = Face.makeFaceFromVertices(new ArrayList<>(vertexSet), es, vertexSet.size());
            for (Edge edge : face.edges) {
                if(!es.contains(edge))
                    es.add(edge);
            }
            fs.add(face);
        }

        Polyhedron r = new Polyhedron(vs, es, fs);
        r.setUp(copy.up.cpy());
        r.calculateCenter();
        r.calculateNeighbors();
        return r;
    }

    public static Polyhedron sphereProject(Polyhedron polyhedron, float radius) {
        Polyhedron copy = polyhedron.copy();

        Map<Vertex, Vertex> map = new HashMap<>();

        List<Vertex> vertexList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        List<Face> faceList = new ArrayList<>();

        Vector3 center = copy.center;
        for (Vertex vertex : copy.vertices) {
            Vector3 dir = vertex.cpy().sub(center).nor();
            Vertex newDelta = new Vertex(center.cpy().add(dir.scl(radius)));
            map.put(vertex, newDelta);
            vertexList.add(newDelta);
        }

        for (Edge edge : copy.edges) {
            Edge e = new Edge(map.get(edge.a), map.get(edge.b));
            edgeList.add(e);
        }

        for (Face face : copy.faces) {
            List<Vertex> verts = new ArrayList<>();
            for (Vertex vertex : face.vertices) {
                verts.add(map.get(vertex));
            }
            List<Edge> edges = new ArrayList<>();
            for (Edge edge : face.edges) {
                Edge e = new Edge(map.get(edge.a), map.get(edge.b));
                e = edgeList.get(edgeList.indexOf(e));
                edges.add(e);
            }

            faceList.add(new Face(verts, edges));
        }


        Polyhedron r = new Polyhedron(vertexList, edgeList, faceList);
        r.calculateCenter();
        r.calculateNeighbors();
        r.setUp(copy.up.cpy());
        return r;
    }

    public Mesh makeMesh(Color color, int renderType) {
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

        if(renderType == GL20.GL_LINES) {
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
        }
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
        long start = System.currentTimeMillis();
        List<Vertex> vs = new ArrayList<>(vertices);
        List<Edge> es = new ArrayList<>(edges);
        List<Face> fs = new ArrayList<>(faces);

        /**
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
         **/

        Polyhedron polyhedron = new Polyhedron(vs, es, fs);
        polyhedron.calculateCenter();
        polyhedron.setUp(this.up.cpy());
        polyhedron.calculateNeighbors();
        long end = System.currentTimeMillis();
        //System.out.println(" " + (end - start) / 1000f);
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
