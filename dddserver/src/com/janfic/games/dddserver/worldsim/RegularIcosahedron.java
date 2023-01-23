package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class RegularIcosahedron extends Polyhedron {
    public RegularIcosahedron(float height) {
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
        }

        for (int i = 1; i < 6; i++) {
            Vector3 v1 = vertices.get(i);
            Vector3 v2 = vertices.get((i % 5) + 1);
        }

        for (int i = 0; i < 5; i++) {
            float theta = (float) (2 * Math.PI / 5 * i - Math.PI / 5);
            float x = (float) (radCD * Math.cos(theta));
            float y = gn + bd;
            float z = (float) (radCD * Math.sin(theta));
            Vector3 v = new Vector3(x, y , z);
            vertices.add(v);
        }

        for (int i =6; i < 11; i++) {
            Vector3 v1 = vertices.get(i);
            Vector3 v2 = vertices.get((i % 5) + 6);
        }

        for (int i = 1; i <= 5; i++) {
            Vector3 v1 = vertices.get(i);
            Vector3 v2 = vertices.get(i + 5);
            Vector3 v3 = vertices.get(i % 5 + 6);

        }
        vertices.add(top);


        // Faces
        int[] facesIndexes = new int[] {
                0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 5, 0, 5 , 1,
                1, 2, 7, 2, 3, 8, 3, 4, 9, 4, 5, 10, 5, 1, 6,
                6, 7, 1, 7, 8, 2, 8, 9, 3, 9, 10, 4, 10, 6, 5,
                11, 6, 7, 11, 7, 8, 11, 8, 9, 11, 9, 10, 11, 10, 6
        };
        for (int i = 0; i < facesIndexes.length; i+=3) {
            List<Edge> fEdges = new ArrayList<>();
            List<Vector3> fVertices = new ArrayList<>();
            Vector3 f0 = vertices.get(facesIndexes[i]), f1 = vertices.get(facesIndexes[i+1]), f2 = vertices.get(facesIndexes[i+2]);
            Edge s = new Edge(f0, f1);
            Edge u = new Edge(f1, f2);
            Edge v = new Edge(f0, f2);
            fEdges.add(s);
            fEdges.add(u);
            fEdges.add(v);
            fVertices.add(f0);
            fVertices.add(f1);
            fVertices.add(f2);
            for (Edge fEdge : fEdges) {
                if (edges.contains(fEdge)) continue;
                edges.add(fEdge);
            }
            Face f = new Face(fVertices, fEdges);
            faces.add(f);
        }

        // Face Neighbors
        // Bottom Ring
        for (Face face : faces) {
            for (Face other : faces) {
                if(face == other) continue;
                if(!face.neighbors.contains(other) && face.isNeighbor(other)) {
                    face.neighbors.add(other);
                }
            }
        }
    }
}
