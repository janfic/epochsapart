package com.janfic.games.library.utils.patterns;

import java.util.List;

public abstract class Observable<T> {
    protected List<Observer<T>> observers;
    protected T data;


    protected Observable(T data) {
        this.data = data;
    }

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    // TODO: Abstract updating observers.
    public abstract void updateObservers();
}
