package com.rolegame.data;

import java.io.Serializable;
import com.rolegame.client.JansiHelper;

public record Statistics(String name, int level, int activeLive, int maxLive, int activeEndurance, int maxEndurance,
                int protection, int power, int stamina, int speed) implements Serializable {

        @Override
        public String toString() {
                String output = "";
                output += JansiHelper.alert("name: ") + name + "\n";
                output += JansiHelper.alert("level: ") + level + "\n";
                output += JansiHelper.alert("activeLive: ") + activeLive + "\n";
                output += JansiHelper.alert("maxLive: ") + maxLive + "\n";
                output += JansiHelper.alert("activeEndurance: ") + activeEndurance + "\n";
                output += JansiHelper.alert("maxEndurance: ") + maxEndurance + "\n";
                output += JansiHelper.alert("protection: ") + protection + "\n";
                output += JansiHelper.alert("power: ") + power + "\n";
                output += JansiHelper.alert("stamina: ") + stamina + "\n";
                output += JansiHelper.alert("speed: ") + speed + "\n";
                return output;
        }
}
