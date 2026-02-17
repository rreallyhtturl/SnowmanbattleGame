package com.snowmagic;

import java.awt.Rectangle;

public class Player {
    public int x, y;
    public int health = 100;
    public int maxHealth = 100;
    public int damage = 25;
    public boolean isSpeedBoosted = false;
    public boolean isInvincible = false;
    public boolean isDamageBoosted = false;
    private int effectTimer = 0;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - 25, y - 35, 50, 70);
    }

    public void takeDamage(int damage) {
        if (!isInvincible) {
            health -= damage;
            health = Math.max(0, health);
        }
    }

    public void activateSpeedBoost() {
        isSpeedBoosted = true;
        effectTimer = 300;
    }

    public void activateInvincibility() {
        isInvincible = true;
        effectTimer = 300;
    }

    public void activateDamageBoost() {
        isDamageBoosted = true;
        damage = 45;
        effectTimer = 300;
    }

    public void updateEffects() {
        if (effectTimer > 0) {
            effectTimer--;
            if (effectTimer == 0) {
                isSpeedBoosted = false;
                isInvincible = false;
                if (isDamageBoosted) {
                    isDamageBoosted = false;
                    damage = 25;
                }
            }
        }
    }
}