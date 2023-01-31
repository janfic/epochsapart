package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

public class Face {
    List<Vertex> vertices;
    List<Edge> edges;
    Set<Edge> edgesSet;
    Vector3 center;
    List<Face> neighbors;
    float height;
    Color color;

    public Face(List<Vertex> vertices, List<Edge> edges) {
        this(vertices, edges, 0);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Face(List<Vertex> vertices, List<Edge> edges, float height) {
        this.edges = edges;
        this.vertices = vertices;
        this.edgesSet = new HashSet<>();
        edgesSet.addAll(edges);
        Vector3 center = new Vector3();
        for (Vertex vertex : vertices) {
            center.add(vertex);
            vertex.addFace(this);
        }
        this.center = center.scl(1f / vertices.size());
        neighbors = new ArrayList<>();
        for (Edge edge : this.edges) {
            edge.addFace(this);
        }
        for (Vertex v : this.vertices) {
            v.addFace(this);
        }
        this.height = height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    /**
     * Will attempt to make a face with shortest n edges.
     *
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
            if (edgeMap.containsKey(possibleEdge)) {
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

    private static Vector3 projectToPlane(Vector3 center, Vector3 normal, Vector3 x) {
        Vector3 cx = x.cpy().sub(center);
        return cx.cpy().sub(normal.cpy().scl(cx.cpy().dot(normal)));
    }

    private static void insert(List<Edge> edges, Edge e) {
        int i = 0;
        while (i < edges.size() && e.dist() > edges.get(i).dist()) {
            i++;
        }
        edges.add(i, e);
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
        if (!(obj instanceof Face)) return super.equals(obj);
        Face other = (Face) obj;
        Set<Edge> intersection = new HashSet<>(edgesSet);
        intersection.retainAll(other.edgesSet);
        return intersection.size() == edgesSet.size();
    }

    public Mesh makeMesh(int renderType, Polyhedron polyhedron) {
        Vector3 center = polyhedron.center.cpy();
        Mesh mesh = new Mesh(true, true, vertices.size(), vertices.size() * 10,
                new VertexAttributes(
                        new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                        new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
                )
        );
        float[] verts = new float[vertices.size() * mesh.getVertexSize() / 4];
        int posOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset / 4;
        int norOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset / 4;
        int colOffset = mesh.getVertexAttribute(VertexAttributes.Usage.ColorUnpacked).offset / 4;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex v = vertices.get(i);
            Vector3 norm = this.center.cpy().sub(center).nor();
            int j = i * mesh.getVertexSize() / 4;
            verts[j + posOffset] = v.x;
            verts[j + posOffset + 1] = v.y;
            verts[j + posOffset + 2] = v.z;
            verts[j + norOffset] = norm.x;
            verts[j + norOffset + 1] = norm.y;
            verts[j + norOffset + 2] = norm.z;
            verts[j + colOffset] = 1;
            verts[j + colOffset + 1] = 1;
            verts[j + colOffset + 2] = 1;
            verts[j + colOffset + 3] = 1;
        }
        mesh.setVertices(verts, 0, verts.length);
        if (renderType == GL20.GL_LINES) {
            short[] indices = new short[edges.size() * 2];
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                int a = vertices.indexOf(edge.a);
                int b = vertices.indexOf(edge.b);
                int j = i * 2;
                indices[j] = (short) a;
                indices[j + 1] = (short) b;
            }
            mesh.setIndices(indices, 0, indices.length);
        }
        if (renderType == GL20.GL_TRIANGLES) {
            int amount = (vertices.size() - 2);
            short[] indices = new short[amount * 3];
            int index = 0;
            Vertex a = vertices.get(0);
            for (int j = 1; j < vertices.size() - 1; j++) {
                Vertex b = vertices.get(j);
                Vertex c = vertices.get(j + 1);
                Plane plane = new Plane(a, b, c);
                if (!plane.isFrontFacing(this.center.cpy().sub(polyhedron.center))) {
                    indices[index++] = (short) vertices.indexOf(a);
                    indices[index++] = (short) vertices.indexOf(b);
                    indices[index++] = (short) vertices.indexOf(c);
                } else {
                    indices[index++] = (short) vertices.indexOf(a);
                    indices[index++] = (short) vertices.indexOf(c);
                    indices[index++] = (short) vertices.indexOf(b);
                }
            }
            mesh.setIndices(indices);
        }
        return mesh;
    }

    public int[] addToMesh(Mesh mesh, float[] vertices, short[] indices, int vertexOffset, int indexOffset, int renderType, Polyhedron polyhedron) {
        Vector3 center = polyhedron.center.cpy();
        int[] offsets = new int[2];
        // Top Face
        for (int i = 0; i < this.vertices.size(); i++) {
            Vertex v = this.vertices.get(i).cpy();
            Vector3 norm = this.center.cpy().sub(center).nor();
            Vector3 vNorm = v.cpy().sub(center).nor();
            v.add(vNorm.cpy().scl(height));
            int j = i * mesh.getVertexSize() / 4;
            int offset = j + vertexOffset;
            addVertex(mesh, vertices, offset, v, norm, color == null ? Color.WHITE : color);
            offsets[0] += mesh.getVertexSize() / 4;
        }
        if (renderType == GL20.GL_LINES) {
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                int j = i * 2;
                indices[indexOffset + j] = (short) (this.vertices.indexOf(edge.a) + vertexOffset / (mesh.getVertexSize() / 4));
                indices[indexOffset + j + 1] = (short) (this.vertices.indexOf(edge.b) + vertexOffset / (mesh.getVertexSize() / 4));
                offsets[1] += 2;
            }
        } else if (renderType == GL20.GL_TRIANGLES) {
            Vertex v = this.vertices.get(0);
            int index = 0;
            for (int j = 1; j < this.vertices.size() - 1; j++) {
                Vertex b = this.vertices.get(j);
                Vertex c = this.vertices.get(j + 1);
                Plane plane = new Plane(v, b, c);
                if (!plane.isFrontFacing(this.center.cpy().sub(polyhedron.center))) {
                    indices[indexOffset + index] = (short) (vertexOffset / (mesh.getVertexSize() / 4) + this.vertices.indexOf(v));
                    indices[indexOffset + index + 1] = (short) (vertexOffset / (mesh.getVertexSize() / 4) + this.vertices.indexOf(b));
                    indices[indexOffset + index + 2] = (short) (vertexOffset / (mesh.getVertexSize() / 4) + this.vertices.indexOf(c));
                } else {
                    indices[indexOffset + index] = (short) (vertexOffset / (mesh.getVertexSize() / 4) + this.vertices.indexOf(v));
                    indices[indexOffset + index + 1] = (short) (vertexOffset / (mesh.getVertexSize() / 4) + this.vertices.indexOf(c));
                    indices[indexOffset + index + 2] = (short) (vertexOffset / (mesh.getVertexSize() / 4) + this.vertices.indexOf(b));
                }
                index += 3;
            }
            offsets[1] += index;
        }
        if (height == 0) return offsets;
        // Side Faces
        // For each side face: ( edge )
        //      move vertexOffset to new offset
        //      move indexOffset to new offset
        //      lines:
        //          add top and bottom verts [ top, top, top, ...,  bottom, bottom, ... ]
        //          get index of top
        //          get index of bottom
        //          add to indices
        //          update offset[]
        //      triangles:
        //          for each edge
        //              calculate normal for face
        //              add 4 verts ( two top, two bottom ) ( with new normal )
        //              get triangle index
        //              update offsets[]
        if (GL20.GL_LINES == renderType) {
            // Add top and bottom verts
            int faceVertOffset = offsets[0];
            int faceIndexOffset = offsets[1];
            for (int i = 0; i < this.vertices.size(); i++) {
                Vertex v = this.vertices.get(i).cpy();
                Vector3 norm = this.center.cpy().sub(center).nor();
                Vector3 vNorm = v.cpy().sub(center).nor();
                Vector3 bottom = v.cpy();
                Vector3 top = v.cpy().add(vNorm.cpy().scl(height));
                int j = i * (mesh.getVertexSize() / 4) * 2; // Adding 2 vertices
                int offset = j + vertexOffset + faceVertOffset;
                addVertex(mesh, vertices, offset, top, norm,  color == null ? Color.WHITE : color);
                offsets[0] += (mesh.getVertexSize() / 4);
                short topIndex = (short) (offset / (mesh.getVertexSize() / 4));
                offset += (mesh.getVertexSize() / 4);
                addVertex(mesh, vertices, offset, bottom, norm,  color == null ? Color.WHITE : color);
                offsets[0] += (mesh.getVertexSize() / 4);
                short bottomIndex = (short) (offset / (mesh.getVertexSize() / 4));

                int iOffset = i * 2 + indexOffset;
                indices[iOffset + faceIndexOffset] = topIndex;
                indices[iOffset + faceIndexOffset + 1] = bottomIndex;
                offsets[1] += 2;
            }
        } else if (renderType == GL20.GL_TRIANGLES) {
            int faceVertOffset = offsets[0];
            int faceIndexOffset = offsets[1];
            Vector3 topCenter = new Vector3();
            for (int i = 0; i < this.vertices.size(); i++) {
                Vertex v = this.vertices.get(i).cpy();
                Vector3 vNorm = v.cpy().sub(center).nor();
                v.add(vNorm.scl(height));
                topCenter.add(v);
            }
            topCenter.scl(1f / this.vertices.size());
            Vector3 middleCenter = new Vector3();
            middleCenter.add(topCenter);
            middleCenter.add(this.center);
            middleCenter.scl(1 / 2f);
            int index = 0;
            for (int i = 0; i < this.edges.size(); i++) {
                Edge edge = edges.get(i);
                Vertex a = edge.a.cpy();
                Vertex b = edge.b.cpy();
                Vector3 aNorm = a.cpy().sub(center).nor();
                Vector3 bNorm = b.cpy().sub(center).nor();
                Vertex c = new Vertex(a.cpy().add(aNorm.scl(height)));
                Vertex d = new Vertex(b.cpy().add(bNorm.scl(height)));
                List<Vertex> sideVectors = new ArrayList<>();
                sideVectors.add(a);
                sideVectors.add(b);
                sideVectors.add(c);
                sideVectors.add(d);
                Vector3 sideCenter = new Vector3();
                sideCenter.add(a);
                sideCenter.add(b);
                sideCenter.add(c);
                sideCenter.add(d);
                sideCenter.scl(1 / 4f);
                Vector3 norm = sideCenter.sub(middleCenter).nor();;
                sortVerticesClockwise(sideVectors, norm);
                int j = (index) * mesh.getVertexSize() / 4;
                int offset = j + vertexOffset + faceVertOffset;
                addVertex(mesh, vertices, offset, sideVectors.get(0), norm,  color == null ? Color.WHITE : color);
                short aIndex = (short) (offset / (mesh.getVertexSize() / 4));
                offsets[0] += (mesh.getVertexSize() / 4);
                offset += (mesh.getVertexSize() / 4);
                addVertex(mesh, vertices, offset, sideVectors.get(1), norm,  color == null ? Color.WHITE : color);
                short bIndex = (short) (aIndex + 1);
                offsets[0] += (mesh.getVertexSize() / 4);
                offset += (mesh.getVertexSize() / 4);
                addVertex(mesh, vertices, offset, sideVectors.get(2), norm,  color == null ? Color.WHITE : color);
                short cIndex = (short) (bIndex + 1);
                offsets[0] += (mesh.getVertexSize() / 4);
                offset += (mesh.getVertexSize() / 4);
                addVertex(mesh, vertices, offset, sideVectors.get(3), norm, color == null ? Color.WHITE : color);
                short dIndex = (short) (cIndex + 1);
                offsets[0] += (mesh.getVertexSize() / 4);
                index += 4;

                int iOffset = i * 6 + indexOffset + faceIndexOffset;
                indices[iOffset + 0] = aIndex;
                indices[iOffset + 1] = bIndex;
                indices[iOffset + 2] = cIndex;
                indices[iOffset + 3] = aIndex;
                indices[iOffset + 4] = cIndex;
                indices[iOffset + 5] = dIndex;
                offsets[1] += 6;
            }
        }
        return offsets;
    }

    public void addVertex(Mesh mesh, float[] vertices, int offset, Vector3 vertex, Vector3 normal, Color color) {
        int posOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset / 4;
        int norOffset = mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset / 4;
        int colOffset = mesh.getVertexAttribute(VertexAttributes.Usage.ColorUnpacked).offset / 4;
        vertices[offset + posOffset] = vertex.x;
        vertices[offset + posOffset + 1] = vertex.y;
        vertices[offset + posOffset + 2] = vertex.z;
        vertices[offset + norOffset] = normal.x;
        vertices[offset + norOffset + 1] = normal.y;
        vertices[offset + norOffset + 2] = normal.z;
        vertices[offset + colOffset] = color.r;
        vertices[offset + colOffset + 1] = color.g;
        vertices[offset + colOffset + 2] = color.b;
        vertices[offset + colOffset + 3] = color.a;
    }

    public int getMeshVertexCount() {
        if (height == 0) {
            return vertices.size();
        } else {
            return vertices.size() + edges.size() * 4;
        }
    }

    public int getMeshIndexCount(int renderType) {
        if (height == 0) {
            return (renderType == GL20.GL_LINES ? edges.size() * 2 : (vertices.size() - 2) * 3);
        } else {
            return (renderType == GL20.GL_LINES ? edges.size() * 2 + vertices.size() * 2 : (vertices.size() - 2) * 3) + (edges.size() * 2) * 3;
        }
    }

    public List<Face> collectNeighbors(int radius) {
        List<Face> faces = new ArrayList<>();
        Set<Face> marked = new HashSet<>();
        Queue<Face> queue = new LinkedList<>();
        Map<Face, Integer> distance = new HashMap<>();
        queue.add(this);
        distance.put(this, 0);

        while (!queue.isEmpty()) {
            Face f = queue.poll();
            if (marked.contains(f)) continue;
            marked.add(f);
            faces.add(f);
            if (distance.get(f) < radius) {
                for (Face neighbor : f.neighbors) {
                    if (!distance.containsKey(neighbor))
                        distance.put(neighbor, distance.get(f) + 1);
                    queue.add(neighbor);
                }
            }
        }

        return faces;
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
            if (n.dot(cross) > 0) {
                angle = -angle;
            }
            if (angle < 0) angle += (Math.PI * 2);
            if (Float.isNaN(angle) && v != x) {
                angle = (float) Math.PI;
            }
            return angle;
        }

        @Override
        public int compare(Vector3 a, Vector3 b) {
            if (a.equals(b)) return 0;
            return (int) Math.signum(getAngle(a) - getAngle(b));
        }


    }
}