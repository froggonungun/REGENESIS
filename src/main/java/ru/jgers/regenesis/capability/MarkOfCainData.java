package ru.jgers.regenesis.capability;

import java.util.Random;

public class MarkOfCainData implements IMarkOfCainData {
    private int yearCount = 0;
    private int tickCount = 0;

    @Override
    public int getYearCount() {
        return this.yearCount;
    }

    @Override
    public void setYearCount(int yearCount) {
        this.yearCount = yearCount;
    }

    @Override
    public void incrementYearCount() {
        Random random = new Random();
        this.yearCount = this.yearCount + random.nextInt(2, 15);
    }

    @Override
    public int getTickCount() {
        return this.tickCount;
    }

    @Override
    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    @Override
    public void incrementTickCount() {
        this.tickCount++;
    }
}