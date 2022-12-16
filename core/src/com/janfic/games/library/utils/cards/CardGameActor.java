package com.janfic.games.library.utils.cards;

import com.janfic.games.library.utils.patterns.Observer;

import java.util.LinkedList;
import java.util.Queue;

public class CardGameActor extends Observer<Queue<StateChange>> {
    int id;
    String name;
    public static int idCount;

    Queue<PlayerAction> queuedActions;

    public CardGameActor(String name) {
        this.name = name;
        this.queuedActions = new LinkedList<>();
        this.id = idCount++;
    }

    @Override
    public String toString() {
        return "(" + name + " | " + id + ")";
    }

    public Queue<PlayerAction> getQueuedActions() {
        return queuedActions;
    }

    public void queueAction(PlayerAction playerAction) {
        this.queuedActions.add(playerAction);
    }

    @Override
    public void observe(Queue<StateChange> obj) {
        this.observedData.addAll(obj);
    }
}