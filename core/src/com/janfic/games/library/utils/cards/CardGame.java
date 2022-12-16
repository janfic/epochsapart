package com.janfic.games.library.utils.cards;

import com.janfic.games.library.utils.patterns.Observable;

import java.util.*;

public abstract class CardGame extends Observable<Queue<StateChange>> {
    protected String name;
    protected List<CardGameRule> rules;
    protected List<CardGameEvent> events;
    protected CardGameState state;

    protected PriorityQueue<StateChange> actionQueue;

    public CardGame(String name, List<CardGameRule> rules, List<CardGameEvent> eventTriggers, CardGameState state) {
        super(null);
        this.name = name;
        this.rules = rules;
        this.events = eventTriggers;
        this.state = state;
        this.actionQueue = new PriorityQueue<>(new StateChange.StateChangeComparator());
        this.data = new LinkedList<>();
        this.observers = new ArrayList<>();
    }

    public CardGame(String name) {
        this(name, new ArrayList<>(), new ArrayList<>(), new CardGameState());
    }

    public abstract void setup();

    public abstract void update();

    public void queueAction(MoveCardByAmountAction action) {
        actionQueue.add(action);
    }

    @Override
    public String toString() {
        String s = "";
        s += "Name: " + name + "\n";
        s += "Rules:  " + rules + " \n";
        s += "Events:  " + events + " \n";
        s += "Action Queue:  " + actionQueue + " ]\n";
        s += "State: { " + state + " }\n";
        return s;
    }

    public CardGameState getState() {
        return state;
    }
}
