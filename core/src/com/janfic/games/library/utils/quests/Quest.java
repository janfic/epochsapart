package com.janfic.games.library.utils.quests;

import java.util.List;

public class Quest {
     public List<List<QuestPoint>> layers;
     List<QuestPoint> questPoints;

     @Override
     public String toString() {
          String r = "Quest:\n";
          r += "\tLayers:\n";
          for (int i = 0; i < layers.size(); i++) {
               r += "\t\t" + (i + 1) + ".\n";
               for (int j = 0; j < layers.get(i).size(); j++) {
                    r += "\t\t\t" + (j + 1) + ". " + layers.get(i).get(j).details.get(0).name + "\n";
               }
          }
          return r;
     }
}
