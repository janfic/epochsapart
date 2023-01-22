package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HexWorld {
    List<Vector3> vertices;
    List<Edge> edges;
    List<Face> face;

    float posX, posY;
    int level;

    public HexWorld(float height, float sx, float sy, int level) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        face = new ArrayList<>();
        posX = sx;
        posY = sy;
        this.level = level;

        // Calc
        // Using https://personal.math.ubc.ca/~cass/courses/m308-03b/projects-03b/keating/projectweppage2.htm
        Vector3 b = new Vector3();
        Vector3 a = new Vector3(0, height, 0);

        // Circle ABD
        Vector3 centerABD = new Vector3(0,height / 2, 0);
        float radABD = height / 2;

        // Circle CD
        Vector3 c = new Vector3(0, height / 5f, 0);
        float radCD = radABD * 4 / 5f;
        Vector3 d = new Vector3(0, height / 5, radCD);

        // BD Length
        float bd = b.dst(d);
        System.out.println(bd);

        // GN ( decagon measurement )
        Vector3 g = new Vector3((float) (bd * Math.cos(0)), 0, (float) (bd * Math.sin(0)));
        Vector3 n = new Vector3((float) (bd * Math.cos(Math.PI / 5)), 0, (float) (bd * Math.sin((Math.PI / 5))));
        float gn = g.dst(n);
        System.out.println(gn);

        Vector3 bottom = new Vector3();
        Vector3 top = new Vector3(0,gn + bd + gn,0);
        vertices.add(bottom);

        for (int i = 0; i < 5; i++) {
            float theta = (float) (2 * Math.PI / 5 * i);
            float x = (float) (radCD * Math.cos(theta));
            float y = gn;
            float z = (float) (radCD * Math.sin(theta));
            Vector3 v = new Vector3(x, y , z);
            vertices.add(v);
            edges.add(new Edge(bottom, v));
        }

        for (int i = 1; i < 6; i++) {
            Vector3 v1 = vertices.get(i);
            Vector3 v2 = vertices.get((i % 5) + 1);
            Edge edge = new Edge(v1, v2);
            edges.add(edge);
        }

        for (int i = 0; i < 5; i++) {
            float theta = (float) (2 * Math.PI / 5 * i - Math.PI / 5);
            float x = (float) (radCD * Math.cos(theta));
            float y = gn + bd;
            float z = (float) (radCD * Math.sin(theta));
            Vector3 v = new Vector3(x, y , z);
            vertices.add(v);
            edges.add(new Edge(top, v));
        }

        for (int i =6; i < 11; i++) {
            Vector3 v1 = vertices.get(i);
            Vector3 v2 = vertices.get((i % 5) + 6);
            Edge edge = new Edge(v1, v2);
            edges.add(edge);
        }

        for (int i = 1; i <= 5; i++) {
            Vector3 v1 = vertices.get(i);
            Vector3 v2 = vertices.get(i + 5);
            Vector3 v3 = vertices.get(i % 5 + 6);
            Edge edge = new Edge(v1, v2);
            Edge edge2 = new Edge(v1, v3);
            edges.add(edge);
            edges.add(edge2);
        }


        vertices.add(top);
    }

    public Mesh makeMesh() {
        Mesh mesh = new Mesh(true, true, 20, 100,
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
            verts[j + 3] = 1;
            verts[j + 4] = 1;
            verts[j + 5] = 1;
            verts[j + 6] = 1;
        }
        mesh.setVertices(verts, 0, vertices.size() * (3 + 4));
        short[] indices = new short[edges.size() * 2];
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            int a = vertices.indexOf(edge.a);
            int b = vertices.indexOf(edge.b);
            int j = i * 2;
            indices[j] = (short) a;
            indices[j + 1] = (short) b;
            System.out.println(edge.dist());
        }
        mesh.setIndices(indices);
        return mesh;
    }

    static class Edge {
        Vector3 a, b;
        public Edge(Vector3 a, Vector3 b) {
            this.a = a;
            this.b = b;
        }

        public float dist() {
            return a.dst(b);
        }
    }

    static class Face {
        Vector3[] vertices;
        Edge[] edges;
    }

    public void draw(ShapeRenderer renderer, float x, float y) {
        for (Vector3 vertex : vertices) {
            renderer.setColor(Color.WHITE);
            renderer.circle(vertex.x + x, vertex.z + y, 2);
        }
    }
}
