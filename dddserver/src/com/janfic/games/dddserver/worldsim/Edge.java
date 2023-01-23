package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Vector3;

public class Edge {
    public Vertex a, b;
    public Edge(Vertex a, Vertex b) {
        this.a = a;
        this.b = b;
    }

    public float dist() {
        return a.dst(b);
    }

    @Override
    public boolean equals(Object obj) {
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
}
