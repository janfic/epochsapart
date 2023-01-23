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
    public static Face makeFaceFromVertices(List<Vertex> vertices, List<Edge> edgePool) {
        List<Edge> es = new ArrayList<>(), ke = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            Vertex a = vertices.get(i);
            for (int j = 0; j < vertices.size(); j++) {
                if(j == i) continue;;
                Vertex b = vertices.get(j);
                Edge e = new Edge(a, b);

                if(!ke.contains(e)) {
                    if (edgePool.contains(e)) {
                        ke.add(edgePool.get(edgePool.indexOf(e)));
                    }
                    else {
                        ke.add(e);
                    }
                }

            }
        }

        ke.sort((a, b) -> (int) Math.signum(a.dist() - b.dist()));
        for (int i = 0; i < vertices.size(); i++) {
            Edge edge = ke.get(i);
            es.add(edge);
            if(!edgePool.contains(edge)) {
                edgePool.add(edge);
            }
        }
        return new Face(vertices, es);
    }
}