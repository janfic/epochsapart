package com.janfic.games.library.utils;

import com.badlogic.gdx.graphics.Color;

import java.util.*;

public class ColorRamp {
    public List<Float> intervals;
    public Map<Float, Color> colorRamp;

    public ColorRamp() {
        intervals = new ArrayList<>();
        colorRamp = new HashMap<>();
    }

    public void addColor(float position, Color color) {
        intervals.add(position);
        Collections.sort(intervals);
        colorRamp.put(position, color);
    }

    public List<Map.Entry<Float, Color>> getBorderColors(float value) {
        assert (colorRamp.size() >= 2);
        List<Float> copy = new ArrayList<>(intervals);
        copy.add(value);
        Collections.sort(copy);
        int index = copy.indexOf(value);
        int lastIndex = intervals.size() - 1;
        int minIndex = index - 1;
        int maxIndex = index + 1;
        maxIndex = intervals.indexOf(copy.get(maxIndex));
        minIndex = intervals.indexOf(copy.get(minIndex));
        List<Map.Entry<Float, Color>> colors = new ArrayList<>();
        Map.Entry<Float, Color> a = new AbstractMap.SimpleEntry<>(intervals.get(minIndex), colorRamp.get(intervals.get(minIndex)));
        Map.Entry<Float, Color> b = new AbstractMap.SimpleEntry<>(intervals.get(maxIndex), colorRamp.get(intervals.get(maxIndex)));
        colors.add(a);
        colors.add(b);
        return colors;
    }

    public Color getColor(float value) {
        assert (colorRamp.size() >= 2);
        if(colorRamp.containsKey(value)) return colorRamp.get(value);
        if(value < intervals.get(0)) return colorRamp.get(intervals.get(0));
        if(value > intervals.get(intervals.size() - 1)) return colorRamp.get(intervals.get(intervals.size() - 1));
        List<Map.Entry<Float, Color>> colors = getBorderColors(value);
        Map.Entry<Float, Color> a = colors.get(0);
        Map.Entry<Float, Color> b = colors.get(1);

        Color c = new Color();
        float disA = Math.abs(value - a.getKey());
        float disB = Math.abs(value - b.getKey());
        float total = disA + disB;
        c.add(a.getValue().cpy().mul(disB/total));
        c.add(b.getValue().cpy().mul(disA/total));
        c.a = 1f;

        return c;
    }
}
