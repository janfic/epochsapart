package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    }

    public boolean isNeighbor(Face f) {
        Set<Edge> intersection = new HashSet<>(this.edgesSet);
        intersection.retainAll(f.edges);
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
}