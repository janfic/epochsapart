package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edge implements Comparable<Edge>{
    public Vertex a, b;
    public List<Face> faces;
    public Edge(Vertex a, Vertex b) {
        this.a = a;
        this.b = b;
        faces = new ArrayList<>();
    }

    public float dist() {
        return a.dst(b);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Edge)) return super.equals(obj);
        Edge o = (Edge) obj;
        return (o.a.equals(this.a) && o.b.equals(this.b)) || (o.b.equals(this.a) && o.a.equals(this.b));
    }

    public Vertex getVertexOnEdge(float percentAToB) {
        Vector3 delta = b.cpy().sub(a);
        return new Vertex(a.cpy().add(delta.cpy().scl(percentAToB)));
    }

    public void addToVertices() {
        a.addEdge(this);
        b.addEdge(this);
    }

    public void addFace(Face face) {
        if(faces.contains(face)) return;
        faces.add(face);
    }

    public Face getOtherFace(Face face) {
        if(faces.get(0) == face) return faces.get(1);
        return faces.get(0);
    }

    @Override
    public int compareTo(Edge edge) {
        if(edge.equals(this)) return 0;
        return (int) Math.signum(this.dist() - edge.dist());
    }

    @Override
    public int hashCode() {
        return a.hashCode() + b.hashCode();
    }
}
