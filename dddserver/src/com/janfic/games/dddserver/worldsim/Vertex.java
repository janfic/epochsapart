package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Vertex extends Vector3 {

    List<Edge> edges;
    List<Face> faces;

    public Vertex(Vector3 vector3) {
        super(vector3);
        edges = new ArrayList<>();
        faces = new ArrayList<>();
    }

    public Vertex(float x, float y, float z) {
        super(x,y,z);
        edges = new ArrayList<>();
        faces = new ArrayList<>();
    }

    public Vertex() {
        this(0,0,0);
    }

    @Override
    public Vertex cpy() {
        Vertex v = new Vertex(x,y,z);
        return v;
    }

    public void addEdge(Edge e) {
        if(edges.contains(e)) return;
        edges.add(e);
    }

    public void addFace(Face f) {
        if(faces.contains(f)) return;
        faces.add(f);
    }

    public static float[] makeArray(List<Vertex> vertices) {
        float[] r = new float[vertices.size() * 3];
        for (int i = 0; i < vertices.size(); i++) {
            int j = i * 3;
            r[j] = vertices.get(i).x;
            r[j + 1] = vertices.get(i).y;
            r[j + 2] = vertices.get(i).z;
        }
        return r;
    }
}
