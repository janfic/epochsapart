package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

public class Face {
    List<Vertex> vertices;
    List<Edge> edges;
    Set<Edge> edgesSet;
    Vector3 center;
    List<Face> neighbors;
    Mesh mesh;
    boolean isDirty = true;

    public Face(List<Vertex> vertices, List<Edge> edges) {
        this.edges = edges;
        this.vertices = vertices;
        this.edgesSet = new HashSet<>();
        edgesSet.addAll(edges);
        Vector3 center = new Vector3();
        for (Vertex vertex : vertices) {
            center.add(vertex);
            vertex.addFace(this);
        }
        this.center = center.scl(1f / vertices.size());
        neighbors = new ArrayList<>();
        for (Edge edge : this.edges) {
            edge.addFace(this);
        }
        for (Vertex v : this.vertices) {
            v.addFace(this);
        }
    }

    /**
     * Will attempt to make a face with shortest n edges.
     *
     * @param vertices with size n
     * @return new n sided face
     */
    public static Face makeFaceFromVertices(List<Vertex> vertices, Map<Edge, Edge> edgeMap, int n) {
        List<Edge> newEdges = new ArrayList<>();
        List<Edge> possibleEdges = new LinkedList<>();

        for (int i = 0; i < vertices.size(); i++) {
            Vertex a = vertices.get(i);
            Vertex b = vertices.get((i + 1) % vertices.size());
            Edge e = new Edge(a, b);
            possibleEdges.add(e);
        }

        for (int i = 0; i < possibleEdges.size(); i++) {
            Edge possibleEdge = possibleEdges.get(i);
            if (edgeMap.containsKey(possibleEdge)) {
                possibleEdge = edgeMap.get(possibleEdge);
            }
            newEdges.add(possibleEdge);
        }

        return new Face(vertices, newEdges);
    }

    public static void sortVerticesClockwise(List<Vertex> vertices, Vector3 normal) {
        Vector3 center = Vertex.getAverage(vertices);
        normal.nor();

        Vertex x = vertices.get(0);

        ClockwiseSorter sorter = new ClockwiseSorter(center, normal, x);
        vertices.sort(sorter);
    }

    private static Vector3 projectToPlane(Vector3 center, Vector3 normal, Vector3 x) {
        Vector3 cx = x.cpy().sub(center);
        return cx.cpy().sub(normal.cpy().scl(cx.cpy().dot(normal)));
    }

    private static void insert(List<Edge> edges, Edge e) {
        int i = 0;
        while (i < edges.size() && e.dist() > edges.get(i).dist()) {
            i++;
        }
        edges.add(i, e);
    }

    public boolean isNeighbor(Face f) {
        Set<Edge> intersection = new HashSet<>(this.edgesSet);
        intersection.retainAll(f.edgesSet);
        return intersection.size() > 0;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Face> getNeighbors() {
        return neighbors;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Face)) return super.equals(obj);
        Face other = (Face) obj;
        Set<Edge> intersection = new HashSet<>(edgesSet);
        intersection.retainAll(other.edgesSet);
        return intersection.size() == edgesSet.size();
    }

    public void clean(int renderType, Polyhedron polyhedron) {
        isDirty = false;
        mesh = makeMesh( renderType,  polyhedron);
    }

    public boolean isDirty() {
        return isDirty;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Mesh makeMesh(int renderType, Polyhedron polyhedron) {
        Vector3 center = polyhedron.center.cpy();
        Mesh mesh = new Mesh(true, true, vertices.size(), vertices.size() * 10,
                new VertexAttributes(
                        new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
                )
        );
        float[] verts = new float[vertices.size() * mesh.getVertexSize() / 4];
        int posOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset / 4;
        int norOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset / 4;
        int colOffset = mesh.getVertexAttribute(VertexAttributes.Usage.ColorUnpacked).offset / 4;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            Vector3 norm = this.center.cpy().sub(center).nor();
            int j = i * mesh.getVertexSize() / 4;
            verts[j + posOffset] = v.x;
            verts[j + posOffset + 1] = v.y;
            verts[j + posOffset + 2] = v.z;
            verts[j + norOffset] = norm.x;
            verts[j + norOffset + 1] = norm.y;
            verts[j + norOffset + 2] = norm.z;
            verts[j + colOffset] = 1;
            verts[j + colOffset + 1] = 1;
            verts[j + colOffset + 2] = 1;
            verts[j + colOffset + 3] = 1;
        }
        mesh.setVertices(verts, 0, verts.length);
        if (renderType == GL20.GL_LINES) {
            short[] indices = new short[edges.size() * 2];
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                int a = vertices.indexOf(edge.a);
                int b = vertices.indexOf(edge.b);
                int j = i * 2;
                indices[j] = (short) a;
                indices[j + 1] = (short) b;
            }
            mesh.setIndices(indices, 0, indices.length);
        }
        if (renderType == GL20.GL_TRIANGLES) {
            int amount = (vertices.size() - 2);
            short[] indices = new short[amount * 3];
            int index = 0;
            Vertex a = vertices.get(0);
            for (int j = 1; j < vertices.size() - 1; j++) {
                Vertex b = vertices.get(j);
                Vertex c = vertices.get(j + 1);
                Plane plane = new Plane(a, b, c);
                if (!plane.isFrontFacing(this.center.cpy().sub(polyhedron.center))) {
                    indices[index++] = (short) vertices.indexOf(a);
                    indices[index++] = (short) vertices.indexOf(b);
                    indices[index++] = (short) vertices.indexOf(c);
                } else {
                    indices[index++] = (short) vertices.indexOf(a);
                    indices[index++] = (short) vertices.indexOf(c);
                    indices[index++] = (short) vertices.indexOf(b);
                }
            }
            mesh.setIndices(indices);
        }
        return mesh;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    private static class ClockwiseSorter implements Comparator<Vector3> {

        Vector3 n, c;
        Vector3 x, p;

        public ClockwiseSorter(Vector3 center, Vector3 normal, Vector3 x) {
            this.c = center;
            this.n = normal.cpy().nor();
            this.p = projectToPlane(c, n, x);
            this.x = x;
        }

        public float getAngle(Vector3 v) {
            Vector3 q = projectToPlane(c, n, v);
            float angle = (float) Math.acos(
                    q.dot(p) /
                            (q.len() * p.len())
            );
            Vector3 cross = q.cpy().crs(p);
            if (n.dot(cross) > 0) {
                angle = -angle;
            }
            if (angle < 0) angle += (Math.PI * 2);
            if (Float.isNaN(angle) && v != x) {
                angle = (float) Math.PI;
            }
            return angle;
        }

        @Override
        public int compare(Vector3 a, Vector3 b) {
            if (a.equals(b)) return 0;
            return (int) Math.signum(getAngle(a) - getAngle(b));
        }
    }
}