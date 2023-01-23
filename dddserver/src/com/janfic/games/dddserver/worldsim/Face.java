package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Face {
    List<Vector3> vertices;
    List<Edge> edges;
    Set<Edge> edgesSet;
    Vector3 center;
    List<Face> neighbors;

    public Face(List<Vector3> vertices, List<Edge> edges) {
        this.edges = edges;
        this.vertices = vertices;
        this.edgesSet = new HashSet<>();
        edgesSet.addAll(edges);
        Vector3 center = new Vector3();
        for (Vector3 vertex : vertices) {
            center.add(vertex);
        }
        this.center = center.scl(1f /vertices.size());
        System.out.println(center);
        neighbors = new ArrayList<>();
    }

    public boolean isNeighbor(Face f) {
        Set<Edge> intersection = new HashSet<>(this.edgesSet);
        intersection.retainAll(f.edges);
        return intersection.size() > 0;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public List<Face> getNeighbors() {
        return neighbors;
    }
}