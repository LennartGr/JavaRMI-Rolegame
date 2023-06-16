package com.rolegame.data;

import java.io.Serializable;
import com.rolegame.client.JansiHelper;

public class Statistics implements Serializable {
    private String name;
    private int level;
    private int activeLive;
    private int maxLive;
    private int activeEndurance;
    private int maxEndurance;
    private int protection;
    private int power;
    private int stamina;
    private int speed;

    public Statistics(String name, int level, int activeLive, int maxLive, int activeEndurance, int maxEndurance,
                      int protection, int power, int stamina, int speed) {
        this.name = name;
        this.level = level;
        this.activeLive = activeLive;
        this.maxLive = maxLive;
        this.activeEndurance = activeEndurance;
        this.maxEndurance = maxEndurance;
        this.protection = protection;
        this.power = power;
        this.stamina = stamina;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getActiveLive() {
        return activeLive;
    }

    public void setActiveLive(int activeLive) {
        this.activeLive = activeLive;
    }

    public int getMaxLive() {
        return maxLive;
    }

    public void setMaxLive(int maxLive) {
        this.maxLive = maxLive;
    }

    public int getActiveEndurance() {
        return activeEndurance;
    }

    public void setActiveEndurance(int activeEndurance) {
        this.activeEndurance = activeEndurance;
    }

    public int getMaxEndurance() {
        return maxEndurance;
    }

    public void setMaxEndurance(int maxEndurance) {
        this.maxEndurance = maxEndurance;
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

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
