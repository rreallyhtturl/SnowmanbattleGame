package com.snowmagic;

public class Snowflake {
    public double x, y;
    public double speed;
    public double wind;

    public Snowflake(double x, double y, double speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.wind = Math.random() * 0.5 - 0.25;
    }

    public void update(int width, int height) {
        y += speed;
        x += wind + Math.sin(y * 0.01) * 0.3;

        if (y > height) {
            y = 0;
            x = Math.random() * width;
        }
        if (x > width) x = 0;
        if (x < 0) x = width;
    }
}