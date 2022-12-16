package com.janfic.games.library.utils.cards;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public abstract class TurnedBasedCardGame extends CardGame {

    protected Deque<CardGameActor> turns;
    List<PlayerAction> invalidActions;

    public TurnedBasedCardGame(String name, List<CardGameRule> rules, List<CardGameEvent> eventTriggers, CardGameState state, Deque<CardGameActor> turns) {
        super(name, rules, eventTriggers, state);
        this.turns = turns;
        invalidActions = new ArrayList<>();
    }

    public TurnedBasedCardGame(String name) {
        super(name);
        invalidActions = new ArrayList<>();
        this.turns = new LinkedList<>();
    }

    @Override
    public void update() {

        // Look at oldest move in action queue
        assert (turns.size() > 0);
        if(actionQueue.size() == 0) return;
        StateChange change = actionQueue.poll();

        if(change instanceof PlayerAction) {
            PlayerAction a = (PlayerAction) change;
            if (a.getActor().id == turns.peek().id) {
                for (CardGameRule rule : rules) {
                    if (rule.isValid(a)) {
                        System.out.println(turns);
                        turns.addLast(turns.poll());
                        break;
                    }
                    else {
                        invalidActions.add(a);
                        change = null;
                    }
                }
            }
            else {
                invalidActions.add(a);
                change = null;
            }

        }

        // Apply Actions to state
        if(change != null) {
            change.applyStateChange(this.state);
            for (CardGameEvent event : events) {
                if(event.isTriggered(state)){
                    actionQueue.add(event);
                }
            }
        }


        // Update Observers
        updateObservers();

        // Reset
        invalidActions.clear();

    }

    @Override
    public String toString() {
        String s = super.toString();
        s += "Turns: " + turns + "\n";
        return s;
    }
}
