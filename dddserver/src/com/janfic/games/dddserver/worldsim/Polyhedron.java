package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;

import java.util.*;
import java.util.function.Function;

public class Polyhedron<T extends Face> implements RenderableProvider {
    public int renderType;
    List<Vertex> vertices;
    List<Edge> edges;
    List<T> faces;
    Vertex center;
    Vector3 up;
    Matrix4 transform;
    Camera camera;

    private List<PolyhedronChunk> chunks;
    private Map<Vector3, PolyhedronChunk> chunkMap;
    private int chunkSize = 3;

    Function<Face, T> faceCreator;

    public Polyhedron(List<Vertex> v, List<Edge> e, List<T> f) {
        vertices = new ArrayList<>(v);
        edges = new ArrayList<>(e);
        faces = new ArrayList<>(f);
        transform = new Matrix4();
        calculateCenter();
        index();
        renderType = GL20.GL_TRIANGLES;
        chunks = new ArrayList<>();
    }

    public Polyhedron() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        faces = new ArrayList<>();
        transform = new Matrix4();
        chunks = new ArrayList<>();
    }

    public void setFaceCreator(Function<Face, T> faceCreator) {
        this.faceCreator = faceCreator;
    }

    public void transformFaces() {
        List<T> faceList = new ArrayList<>();
        for (T face : faces) {
            faceList.add(faceCreator.apply(face));
        }
        faces.clear();
        faces.addAll(faceList);
    }

    public void dual() {
        Polyhedron<T> copy = copy();

        List<Vertex> vs = new ArrayList<>();
        List<T> fs = new ArrayList<>();

        Map<Edge, Edge> edgeMap = new HashMap<>();
        Map<Vector3, Set<Vertex>> map = new HashMap<>();
        Map<Vertex, Integer> indexMap = new HashMap<>();

        for (T face : copy.faces) {
            Vertex v = new Vertex(face.center);
            indexMap.put(v, vs.size());
            vs.add(v);
        }

        for (Vertex vertex : copy.vertices) {
            Set<Vertex> vertexSet = new HashSet<>();
            map.put(vertex, vertexSet);
            for (Face face : vertex.faces) {
                Vertex v = new Vertex(face.center);
                vertexSet.add(vs.get(indexMap.get(v)));
            }
        }

        long timeSorting = 0;
        long timeCreating = 0;
        long total = 0;
        long tStart = System.currentTimeMillis();
        for (Map.Entry<Vector3, Set<Vertex>> entry : map.entrySet()) {
            Vector3 key = entry.getKey();
            Set<Vertex> vertexSet = entry.getValue();
            List<Vertex> vertexList = new ArrayList<>(vertexSet);
            Vector3 center = Vertex.getAverage(vertexList);
            Vector3 normal = center.cpy().sub(copy.center);
            Face.sortVerticesClockwise(vertexList, normal);
            Face face = Face.makeFaceFromVertices(vertexList, edgeMap, vertexSet.size());
            for (Edge edge : face.edges) {
                if (!edgeMap.containsKey(edge))
                    edgeMap.put(edge, edge);
            }
            fs.add((T)face);
        }
        long tEnd = System.currentTimeMillis();

        this.vertices.clear();
        this.edges.clear();
        this.faces.clear();
        this.vertices.addAll(vs);
        this.edges.addAll(new ArrayList<>(edgeMap.values()));
        this.faces.addAll(fs);
        calculateCenter();
        calculateNeighbors();
        setChunkSize(chunkSize);
        setUp(up.cpy());
        makeChunks();
    }

    public void uniformTruncate() {
        Polyhedron<T> copy = copy();

        List<Vertex> vs = new ArrayList<>();
        List<Edge> es = new ArrayList<>();
        Map<Edge, Edge> edgeMap = new HashMap<>();
        List<T> fs = new ArrayList<>();

        Map<Vector3, Set<Vertex>> map = new HashMap<>();

        // Create Edges
        for (Edge edge : copy.edges) {
            Vertex a = edge.a;
            Vertex b = edge.b;
            Face f = edge.faces.get(0);
            Face g = edge.faces.get(1);
            Vector3 c = f.center;
            Vector3 d = g.center;

            Vertex s = edge.getVertexOnEdge(1 / 3f);
            Vertex t = edge.getVertexOnEdge(2 / 3f);

            vs.add(s);
            vs.add(t);

            Edge e = new Edge(s, t);
            es.add(e);
            edgeMap.put(e, e);

            if (!map.containsKey(a))
                map.put(a, new HashSet<>());
            if (!map.containsKey(b))
                map.put(b, new HashSet<>());
            if (!map.containsKey(c))
                map.put(c, new HashSet<>());
            if (!map.containsKey(d))
                map.put(d, new HashSet<>());

            map.get(a).add(s);
            map.get(b).add(t);
            map.get(c).add(s);
            map.get(c).add(t);
            map.get(d).add(s);
            map.get(d).add(t);
        }

        long timeSorting = 0;
        long timeCreating = 0;
        long total = 0;
        long tStart = System.currentTimeMillis();
        for (Map.Entry<Vector3, Set<Vertex>> entry : map.entrySet()) {
            Vector3 key = entry.getKey();
            Set<Vertex> vertexSet = entry.getValue();
            List<Vertex> vertexList = new ArrayList<>(vertexSet);
            Vector3 center = Vertex.getAverage(vertexList);
            Vector3 normal = center.cpy().sub(copy.center);
            long start = System.currentTimeMillis();
            Face.sortVerticesClockwise(vertexList, normal);
            long end = System.currentTimeMillis();
            timeSorting += end - start;
            start = System.currentTimeMillis();
            Face face = Face.makeFaceFromVertices(vertexList, edgeMap, vertexSet.size());
            end = System.currentTimeMillis();
            timeCreating += end - start;
            for (Edge edge : face.edges) {
                if (!edgeMap.containsKey(edge))
                    edgeMap.put(edge, edge);
            }
            fs.add((T)face);
        }
        long tEnd = System.currentTimeMillis();

        this.vertices.clear();
        this.edges.clear();
        this.faces.clear();
        this.vertices.addAll(vs);
        this.edges.addAll(new ArrayList<>(edgeMap.values()));
        this.faces.addAll(fs);

        setUp(up.cpy());
        calculateCenter();
        setChunkSize(chunkSize + 3);
        calculateNeighbors();
        makeChunks();
    }

    public void  sphereProject(float radius) {

        Polyhedron<T> copy = copy();

        Map<Vertex, Vertex> map = new HashMap<>();

        List<Vertex> vertexList = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        List<T> faceList = new ArrayList<>();

        Vector3 center = copy.center;
        for (Vertex vertex : vertices) {
            Vector3 dir = vertex.cpy().sub(center).nor();
            Vertex newDelta = new Vertex(center.cpy().add(dir.scl(radius)));
            map.put(vertex, newDelta);
            vertexList.add(newDelta);
        }

        for (Edge edge : copy.edges) {
            Edge e = new Edge(map.get(edge.a), map.get(edge.b));
            edgeList.add(e);
        }

        for (Face face : copy.faces) {
            List<Vertex> verts = new ArrayList<>();
            for (Vertex vertex : face.vertices) {
                verts.add(map.get(vertex));
            }
            List<Edge> edges = new ArrayList<>();
            for (Edge edge : face.edges) {
                Edge e = new Edge(map.get(edge.a), map.get(edge.b));
                e = edgeList.get(edgeList.indexOf(e));
                edges.add(e);
            }

            faceList.add((T)new Face(verts, edges));
        }


        this.vertices.clear();
        this.edges.clear();
        this.faces.clear();
        this.vertices.addAll(vertexList);
        this.edges.addAll(edgeList);
        this.faces.addAll(faceList);
        calculateCenter();
        calculateNeighbors();
        setChunkSize(chunkSize);
        makeChunks();
        setUp(up.cpy());
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<T> getFaces() {
        return faces;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public int getRenderType() {
        return renderType;
    }

    public void setRenderType(int renderType) {
        this.renderType = renderType;
    }

    public Polyhedron<T> copy() {
        long start = System.currentTimeMillis();
        List<Vertex> vs = new ArrayList<>(vertices);
        List<Edge> es = new ArrayList<>(edges);
        List<T> fs = new ArrayList<>(faces);

        /**
         for (Vertex vertex : vertices) {
         vs.add(vertex.cpy());
         }

         for (Edge edge : edges) {
         Vertex a = vs.get(vs.indexOf(edge.a));
         Vertex b = vs.get(vs.indexOf(edge.b));

         Edge e = new Edge(a, b);
         e.addToVertices();
         es.add(e);
         }

         for (Face face : faces) {
         List<Vertex> cV = new ArrayList<>();
         for (Vector3 vertex : face.vertices) {
         cV.add(vs.get(vs.indexOf(vertex)));
         }
         List<Edge> cE = new ArrayList<>();
         for (Edge edge : face.edges) {
         cE.add(es.get(es.indexOf(edge)));
         }
         Face f = new Face(cV, cE);
         fs.add(f);
         }
         **/

        Polyhedron<T> polyhedron = new Polyhedron<T>(vs, es, fs);
        polyhedron.calculateCenter();
        polyhedron.setUp(up.cpy());
        polyhedron.calculateNeighbors();
        polyhedron.setRenderType(this.renderType);
        polyhedron.setChunkSize(this.chunkSize);
        long end = System.currentTimeMillis();
        //System.out.println(" " + (end - start) / 1000f);
        return polyhedron;
    }

    public Vector3 calculateCenter() {
        center = new Vertex();
        for (Vector3 vertex : vertices) {
            center.add(vertex);
        }
        center.scl(1f / vertices.size());
        return center;
    }

    public void calculateNeighbors() {
        for (Face face : faces) {
            for (Edge edge : face.edges) {
                face.neighbors.add(edge.getOtherFace(face));
            }
        }
    }

    public void sortReferences() {
        for (T face : faces) {
            Vector3 norm = face.center.cpy().sub(this.center).nor();
            Face.sortVerticesClockwise(face.vertices, norm);
            float[] vers = new float[face.vertices.size() * 3];
            List<Vertex> vertexList = face.vertices;
            for (int i = 0; i < vertexList.size(); i++) {
                Vertex vertex = vertexList.get(i);
                int j = i * 3;
                vers[j] = vertex.x;
                vers[j + 1] = vertex.y;
                vers[j + 2] = vertex.z;
            }
        }
    }

    public void index() {
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            vertex.setIndex((short) i);
        }
    }

    public Vector3 getCenter() {
        return center;
    }

    public Vector3 getUp() {
        return up;
    }

    public void setUp(Vector3 up) {
        this.up = up;
    }

    public void addTransform(Matrix4 delta) {
        this.transform.mul(delta);
    }

    public Matrix4 getTransform() {
        return transform;
    }

    public void setTransform(Matrix4 transform) {
        this.transform = transform;
    }

    public void setRenderSettings(Camera camera) {
        this.camera = camera;
    }

    public void makeChunks() {
        float radius = center.cpy().dst(vertices.get(0));
        System.out.println("making chunks");

        chunks.clear();
        chunkMap = new HashMap<>();

        if (chunkSize % 2 == 0) chunkSize += 1;
        float deltaDivisions = chunkSize;
        int deltaI = 0;
        for (float delta = 0; delta < Math.PI * 2; delta += 2 * Math.PI / deltaDivisions) {
            int d = (int) Math.min(Math.abs(deltaI - chunkSize / 4), Math.abs(deltaI - 3 * chunkSize / 4));
            float thetaDivisions = chunkSize - d * 3;
            for (float theta = 0; theta < Math.PI * 2; theta += 2 * Math.PI / thetaDivisions) {
                Vector3 vector3 = new Vector3(
                        (float) (radius * Math.cos(theta) * Math.sin(delta)),
                        (float) (radius * Math.sin(theta) * Math.sin(delta)),
                        (float) (radius * Math.cos(delta)));
                vector3.add(center);
                PolyhedronChunk chunk = new PolyhedronChunk(this, vector3);
                chunk.setDirty();
                chunks.add(chunk);
                chunkMap.put(vector3, chunk);
            }
            deltaI++;
        }

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);
            float dist = chunks.get(0).getChunkPoint().dst2(face.center);
            Vector3 closest = chunks.get(0).getChunkPoint();
            for (PolyhedronChunk chunk : chunks) {
                Vector3 vector3 = chunk.getChunkPoint();
                float d = vector3.dst2(face.center);
                if (dist > d) {
                    closest = vector3;
                    dist = d;
                }
            }
            chunkMap.get(closest).addFace(face);
        }
    }

    public List<PolyhedronChunk> getChunks() {
        return chunks;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        int renderedChunks = 0;
        for (int i = 0; i < chunks.size(); i++) {
            PolyhedronChunk chunk = chunks.get(i);
            Vector3 chunkVector = chunks.get(i).getChunkPoint();
            if (chunk.isDirty()) {
                chunk.setRenderType(renderType);
                continue;
            }
            if (camera.position.dst(chunkVector) > camera.far) continue;
            Renderable renderable = pool.obtain();
            renderable.meshPart.mesh = chunk.getMesh();
            renderable.material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
            renderable.meshPart.offset = 0;
            renderable.meshPart.primitiveType = renderType;
            renderable.meshPart.size = renderable.meshPart.mesh.getNumIndices();
            renderable.worldTransform.set(transform);
            renderables.add(renderable);
            renderedChunks++;
        }
    }


    public float getMinHeight() {
        float minHeight = Float.MAX_VALUE;
        for (Face face : faces) {
            if (minHeight > face.height) minHeight = face.height;
        }
        return minHeight;
    }

    public float getMaxHeight() {
        float maxHeight = Float.MIN_VALUE;
        for (Face face : faces) {
            if (maxHeight < face.height) maxHeight = face.height;
        }
        return maxHeight;
    }

    public static class ChunkSorter implements Comparator<PolyhedronChunk> {

        Vector3 cameraPosition;

        public ChunkSorter(Vector3 cameraPosition) {
            this.cameraPosition = cameraPosition;
        }

        @Override
        public int compare(PolyhedronChunk o1, PolyhedronChunk o2) {
            if (o1 != null && o2 != null)
                return (int) Math.signum(o1.getChunkPoint().dst2(cameraPosition) - o2.getChunkPoint().dst2(cameraPosition));
            if (o1 != null) return -1;
            if (o2 != null) return 1;
            return 0;
        }

        public void setCameraPosition(Vector3 cameraPosition) {
            this.cameraPosition = cameraPosition;
        }
    }
}
