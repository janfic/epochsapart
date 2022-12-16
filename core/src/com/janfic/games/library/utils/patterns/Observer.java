package com.janfic.games.library.utils.patterns;

public abstract class Observer<T> {
    public T observedData;
    public abstract void observe(T obj);
}
