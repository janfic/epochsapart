package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.*;

import java.util.*;

public class Face {
    List<Vertex> vertices;
    List<Edge> edges;
    Set<Edge> edgesSet;
    Vector3 center;
    List<Face> neighbors;

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
        this.center = center.scl(1f /vertices.size());
        neighbors = new ArrayList<>();
        for (Edge edge : this.edges) {
            edge.addFace(this);
        }
        for( Vertex v : this.vertices) {
            v.addFace(this);
        }
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
        if(!(obj instanceof Face)) return super.equals(obj);
        Face other = (Face) obj;
        Set<Edge> intersection = new HashSet<>(edgesSet);
        intersection.retainAll(other.edgesSet);
        return intersection.size() == edgesSet.size();
    }

    /**
     * Will attempt to make a face with shortest n edges.
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
            if(edgeMap.containsKey(possibleEdge)) {
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
            if(n.dot(cross) > 0) {
                angle = -angle;
            }
            if(angle < 0) angle += (Math.PI * 2);
            if(Float.isNaN(angle) && v != x) {
                angle = (float) Math.PI;
            }
            return angle;
        }

        @Override
        public int compare(Vector3 a, Vector3 b) {
            if(a.equals(b)) return 0;
            return (int) Math.signum(getAngle(b) - getAngle(a));
        }
    }

    private static Vector3 projectToPlane(Vector3 center, Vector3 normal, Vector3 x) {
        Vector3 cx = x.cpy().sub(center);
        return cx.cpy().sub(normal.cpy().scl(cx.cpy().dot(normal)));
    }

    private static void insert(List<Edge> edges, Edge e) {
        int i = 0;
        while(i < edges.size() && e.dist() > edges.get(i).dist()) {
            i++;
        }
        edges.add(i, e);
    }
}