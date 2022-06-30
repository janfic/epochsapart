package com.janfic.games.library.utils.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestGenerator {
    int layers;
    int minQuestPointsPerLayer;
    int maxQuestPointsPerLayer;
    float linearity;

    Random rand;

    public QuestGenerator(int layers, int minQuestPointsPerLayer, int maxQuestPointsPerLayer, float linearity) {
        this.layers = layers;
        this.minQuestPointsPerLayer = minQuestPointsPerLayer;
        this.maxQuestPointsPerLayer = maxQuestPointsPerLayer;
        this.linearity = linearity;
        this.rand = new Random();
    }

    public Quest generateQuest(List<QuestPoint> endings, List<Location> locations, List<Item> items, List<NPC> npcs, List<Guidance> guideTypes) {
        Quest quest = new Quest();

        List<List<QuestPoint>> layers = new ArrayList<>();

        List<QuestPoint> startLayer = new ArrayList<>();
        QuestPoint start = new QuestPoint();
        start.details = new ArrayList<>();
        start.details.add(new Location("START"));
        startLayer.add(start);
        layers.add(startLayer);

        for (int layer = 0; layer < this.layers - 1; layer++) {
            List<QuestPoint> list = new ArrayList<>();
            int amountInLayer = rand.nextInt(maxQuestPointsPerLayer - minQuestPointsPerLayer) + minQuestPointsPerLayer;
            for (int i = 0; i < amountInLayer; i++) {
                QuestPoint point = new QuestPoint();
                List<QuestDetail> details = new ArrayList<>();
                details.add(locations.get(rand.nextInt(locations.size())));
                point.details = details;
                list.add(point);
            }
            layers.add(list);
        }

        layers.add(endings);

        quest.layers = layers;
        return quest;
    }

}
