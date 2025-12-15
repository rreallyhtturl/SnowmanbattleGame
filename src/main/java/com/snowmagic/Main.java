package com.snowmagic;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Устанавливаем принудительные настройки графики, что бы не мерцал экран
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.opengl", "false");
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.accthreshold", "0");

        // Пробуем разные рендереры
        System.setProperty("sun.java2d.renderer", "sun.java2d.pisces.PiscesRenderingEngine");

        SwingUtilities.invokeLater(() -> {
            new SnowBattleGameFinal();
        });
    }
}