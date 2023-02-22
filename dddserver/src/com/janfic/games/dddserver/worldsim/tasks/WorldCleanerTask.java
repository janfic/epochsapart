package com.janfic.games.dddserver.worldsim.tasks;

import com.janfic.games.dddserver.worldsim.HexWorld;
import com.janfic.games.dddserver.worldsim.Polyhedron;
import com.janfic.games.dddserver.worldsim.PolyhedronChunk;
import com.janfic.games.library.utils.multithreading.OngoingTask;

import java.util.*;

public class WorldCleanerTask extends OngoingTask {

    HexWorld world;
    Polyhedron polyhedron;

    int maxChunksProcessed, queueMaxLength;
    final List<PolyhedronChunk> chunkQueue;
    final Set<PolyhedronChunk> inQueue;
    final Polyhedron.ChunkSorter sorter;

    public WorldCleanerTask(HexWorld world, int maxChunksProcessed, Polyhedron.ChunkSorter sorter) {
        super("Chunk Updater", "Updates world chunks");
        this.world = world;
        chunkQueue = new ArrayList<>();
        inQueue = new HashSet<>();
        this.maxChunksProcessed = maxChunksProcessed;
        queueMaxLength = 20;
        this.sorter = sorter;
    }

    @Override
    public void repeatedLogic() {

        for (PolyhedronChunk chunk : world.getChunks()) {
            if(!inQueue.contains(chunk) && chunk.isDirty()) {
                chunkQueue.add(chunk);
                inQueue.add(chunk);
            }
        }

        chunkQueue.sort(sorter);

        for (int i = 0; i < maxChunksProcessed && !chunkQueue.isEmpty(); i++) {
            PolyhedronChunk chunk = chunkQueue.get(0);
            if(chunk == null) continue;
            chunk.clean();
            chunkQueue.remove(chunk);
            inQueue.remove(chunk);
        }

    }

    private void bfs() {

    }
}
