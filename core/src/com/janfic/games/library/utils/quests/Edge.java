package com.janfic.games.library.utils.quests;

public class Edge<T> {
    T value;
    Node start, end;

    public Edge(T value, Node start, Node end) {
        this.value = value;
        this.start = start;
        this.end = end;
    }
}
