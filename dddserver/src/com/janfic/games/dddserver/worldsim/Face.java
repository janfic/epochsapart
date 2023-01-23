package com.janfic.games.dddserver.worldsim;

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
    public static Face makeFaceFromVertices(List<Vertex> vertices, List<Edge> es, int n) {
        List<Edge> newEdges = new ArrayList<>();
        List<Edge> possibleEdges = new LinkedList<>();

        for (int i = 0; i < vertices.size(); i++) {
            Vertex a = vertices.get(i);
            for (int j = i + 1; j < vertices.size(); j++) {
                Vertex b = vertices.get(j);
                if(i == j || a == b) continue;
                Edge e = new Edge(a,b);
                if(es.contains(e)) {
                    e = es.get(es.indexOf(e));
                }
                insert(possibleEdges, e);
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            Edge possibleEdge = possibleEdges.get(i);
            if(es.contains(possibleEdge)) {
                possibleEdge = es.get(es.indexOf(possibleEdge));
            }
            newEdges.add(possibleEdge);
        }

        return new Face(vertices, newEdges);
    }

    private static void insert(List<Edge> edges, Edge e) {
        int i = 0;
        while(i < edges.size() && e.dist() > edges.get(i).dist()) {
            i++;
        }
        edges.add(i, e);
    }
}