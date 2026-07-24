package ru.jgers.regenesis.capability;


public interface IMarkOfCainData {
    int getYearCount();
    void setYearCount(int yearCount);
    void incrementYearCount();

    int getTickCount();
    void setTickCount(int tickCount);
    void incrementTickCount();
}