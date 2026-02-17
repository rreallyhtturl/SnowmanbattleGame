package com.snowmagic;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Enemy {
    public int x, y;
    public int health;
    public int maxHealth;
    public int damage;
    public double attackSpeed;
    public int level;
    public int lastPlayerX, lastPlayerY;
    public boolean isAttacking = false;
    private int attackCooldown = 0;
    private int state = 0;
    private int stateTimer = 0;

    public Enemy(int x, int y, int level, int health, double attackSpeed) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.maxHealth = health;
        this.health = health;
        this.damage = level * 5 + 10;
        this.attackSpeed = attackSpeed;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - 25, y - 35, 50, 70);
    }

    public boolean takeDamage(int damage) {
        health -= damage;
        return health <= 0;
    }

    public void update(Player player, ArrayList<Snowball> snowballs, Random random, int width, int height) {
        if (player == null) return;

        lastPlayerX = player.x;
        lastPlayerY = player.y;

        // Обновление таймера атаки
        if (attackCooldown > 0) {
            attackCooldown--;
            isAttacking = attackCooldown > 20;
        } else {
            isAttacking = false;
        }

        // Умный (почти) ИИ
        stateTimer--;
        if (stateTimer <= 0) {
            state = random.nextInt(4);
            stateTimer = 80 + random.nextInt(80);
        }

        int dx = 0;
        int dy = 0;

        switch (state) {
            case 0: // Преследование
                if (x < player.x - 50) dx += 3;
                else if (x > player.x + 50) dx -= 3;

                if (y < player.y - 30) dy += 3;
                else if (y > player.y + 30) dy -= 3;
                break;

            case 1: // Обход
                int angle = (int) (System.currentTimeMillis() / 100) % 360;
                dx = (int) (Math.cos(Math.toRadians(angle)) * 3);
                dy = (int) (Math.sin(Math.toRadians(angle)) * 3);
                break;

            case 2: // Уклонение
                for (Snowball snowball : snowballs) {
                    if (snowball.isPlayerSnowball) {
                        double distance = Math.sqrt(Math.pow(x - snowball.x, 2) + Math.pow(y - snowball.y, 2));
                        if (distance < 100) {
                            dx += (x - snowball.x) / 8;
                            dy += (y - snowball.y) / 8;
                        }
                    }
                }
                break;

            case 3: // Случайное движение
                dx = random.nextInt(7) - 3;
                dy = random.nextInt(7) - 3;
                break;
        }

        // Избегание краёв
        if (x < 100) dx += 2;
        if (x > width - 100) dx -= 2;
        if (y < 100) dy += 2;
        if (y > height - 100) dy -= 2;

        x += dx;
        y += dy;

        // Ограничение движения
        x = Math.max(50, Math.min(width - 50, x));
        y = Math.max(50, Math.min(height - 50, y));

        // Атака
        if (random.nextDouble() < attackSpeed && attackCooldown == 0) {
            attackCooldown = 40;
            isAttacking = true;
        }
    }
}