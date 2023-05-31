package com.rolegame.data;

import java.io.Serializable;

public record Statistics(String name, int level, int activeLive, int maxLive, int activeEndurance, int maxEndurance,
        int protection, int power, int stamina, int speed) implements Serializable {

}
