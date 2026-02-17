package com.snowmagic;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Bonus {
    public int x, y;
    public int type;
    public int timeLeft = 300;
    public double floatOffset = 0;
    private double rotation = 0;

    public Bonus(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = (int) (Math.random() * 4);
    }

    public void update() {
        timeLeft--;
        floatOffset = Math.sin(System.currentTimeMillis() * 0.003) * 5;
        rotation += 0.05;
    }

    public void draw(Graphics2D g2d) {
        Color[] colors = {Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA};
        String[] symbols = {"❤️", "⚡", "🛡️", "⚔️"};

        // Сохраняем текущую трансформацию
        AffineTransform originalTransform = g2d.getTransform();

        // Применяем вращение вокруг центра бонуса
        g2d.rotate(rotation, x + 17.5, y + floatOffset + 17.5);

        g2d.setColor(colors[type]);
        g2d.fillOval(x, (int) (y + floatOffset), 35, 35);

        // Блестящий эффект
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.fillOval(x + 5, (int) (y + floatOffset) + 5, 10, 10);

        // Восстанавливаем трансформацию для текста
        g2d.setTransform(originalTransform);

        // Используем системный шрифт, который поддерживает emoji
        Font emojiFont = getEmojiFont();
        g2d.setFont(emojiFont.deriveFont(20f));
        g2d.setColor(Color.WHITE);

        // Центрируем смайлик в круге
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(symbols[type]);
        int textHeight = fm.getAscent();

        int centerX = x + 35 / 2 - textWidth / 2;
        int centerY = (int) (y + floatOffset) + 35 / 2 + textHeight / 2 - 4;

        g2d.drawString(symbols[type], centerX, centerY);
    }

    private Font getEmojiFont() {
        // Попробуем найти шрифт, который поддерживает emoji
        String[] emojiFontNames = {
                "Segoe UI Emoji",
                "Apple Color Emoji",
                "Noto Color Emoji",
                "DejaVu Sans",
                "Dialog Unicode MS",
                "Symbola"
        };

        for (String fontName : emojiFontNames) {
            Font font = new Font(fontName, Font.PLAIN, 12);
            if (!font.getFamily().equalsIgnoreCase("dialog")) {
                return font;
            }
        }

        // Если не нашли специальный шрифт, используем стандартный
        return new Font("SansSerif", Font.PLAIN, 12);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 35, 35);
    }
}