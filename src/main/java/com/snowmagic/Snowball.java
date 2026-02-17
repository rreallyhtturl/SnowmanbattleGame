package com.snowmagic;

import java.awt.Rectangle;

public class Snowball {
    public int x, y;
    public double dx, dy;
    public boolean isPlayerSnowball;
    public int damage;

    public Snowball(int startX, int startY, int targetX, int targetY, boolean isPlayerSnowball, int damage) {
        this.x = startX;
        this.y = startY;
        this.isPlayerSnowball = isPlayerSnowball;
        this.damage = damage;

        double angle = Math.atan2(targetY - startY, targetX - startX);
        double speed = isPlayerSnowball ? 9 : 8;
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
    }

    public void update() {
        x += dx;
        y += dy;
        dy += 0.04;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - 8, y - 8, 16, 16);
    }
}