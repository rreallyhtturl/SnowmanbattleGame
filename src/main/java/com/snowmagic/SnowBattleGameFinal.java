package com.snowmagic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnowBattleGameFinal extends JFrame {
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel levelLabel;
    private JLabel hpLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton menuButton;
    private Timer gameTimer;
    private int timeLeft = 180;
    private int playerScore = 0;
    private int enemyScore = 0;
    private int currentLevel = 1;
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private JLabel resultLabel;

    public SnowBattleGameFinal() {
        setTitle("❄️ Снежная Битва | Весёлого Нового Года! <3 ❄️");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        setupTimer();
        setVisible(true);
    }

    private void initUI() {
        // Создание верхней панели с информацией (меню)
        JPanel infoPanel = new JPanel(new BorderLayout(10, 5));
        infoPanel.setBackground(new Color(70, 130, 180));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верхняя строка: уровень, счёт, таймер, HP
        JPanel topRow = new JPanel(new GridLayout(1, 4, 10, 5));
        topRow.setBackground(new Color(70, 130, 180));

        levelLabel = new JLabel("🎮 Уровень: 1");
        levelLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        levelLabel.setForeground(Color.WHITE);

        scoreLabel = new JLabel("🏆 Счёт: 0 - 0");
        scoreLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);

        timerLabel = new JLabel("⏰ 3:00");
        timerLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);

        hpLabel = new JLabel("❤️ ХП: 100/100");
        hpLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        hpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hpLabel.setForeground(Color.WHITE);

        topRow.add(levelLabel);
        topRow.add(scoreLabel);
        topRow.add(timerLabel);
        topRow.add(hpLabel);

        // Нижняя строка: кнопки и результат
        JPanel bottomRow = new JPanel(new GridLayout(1, 4, 10, 5));
        bottomRow.setBackground(new Color(70, 130, 180));

        startButton = new JButton("🎮 Новая игра");
        startButton.setFont(new Font("Dialog", Font.BOLD, 14));
        startButton.setBackground(new Color(50, 205, 50));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);

        pauseButton = new JButton("⏸️ Пауза");
        pauseButton.setFont(new Font("Dialog", Font.BOLD, 14));
        pauseButton.setBackground(new Color(255, 140, 0));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.setEnabled(false);

        menuButton = new JButton("🏠 Меню");
        menuButton.setFont(new Font("Dialog", Font.BOLD, 14));
        menuButton.setBackground(new Color(138, 43, 226));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setEnabled(false);

        // Лейбл для отображения результата игры
        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setForeground(Color.YELLOW);

        bottomRow.add(startButton);
        bottomRow.add(pauseButton);
        bottomRow.add(menuButton);
        bottomRow.add(resultLabel);

        // Добавляем обе строки на панель
        infoPanel.add(topRow, BorderLayout.NORTH);
        infoPanel.add(bottomRow, BorderLayout.SOUTH);

        // Создание игровой панели
        gamePanel = new GamePanel(this);

        // Размещение компонентов
        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        // Обработчики кнопок
        startButton.addActionListener(e -> startGame());
        pauseButton.addActionListener(e -> togglePause());
        menuButton.addActionListener(e -> returnToMenu());
    }

    private void setupTimer() {
        gameTimer = new Timer(1000, e -> {
            if (gameRunning && !gamePaused) {
                timeLeft--;
                updateTimer();
                if (timeLeft <= 0) {
                    endLevel(false, "Время вышло!");
                }
            }
        });
    }

    private void startGame() {
        gameRunning = true;
        gamePaused = false;
        timeLeft = 180;
        playerScore = 0;
        enemyScore = 0;
        currentLevel = 1;
        resultLabel.setText("");
        resultLabel.setForeground(Color.YELLOW);
        gamePanel.startNewGame(currentLevel);
        startButton.setEnabled(false);
        startButton.setText("🎮 Игра идет...");
        pauseButton.setEnabled(true);
        pauseButton.setText("⏸️ Пауза");
        menuButton.setEnabled(true);
        updateScore();
        updateTimer();
        updateLevel();
        updateHP(100);
        gameTimer.start();
        gamePanel.requestFocusInWindow();
    }

    public void togglePause() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            pauseButton.setText("▶️ Продолжить");
            gamePanel.setPaused(true);
        } else {
            pauseButton.setText("⏸️ Пауза");
            gamePanel.setPaused(false);
            gamePanel.requestFocusInWindow();
        }
    }

    private void returnToMenu() {
        gameRunning = false;
        gamePaused = false;
        gameTimer.stop();
        gamePanel.setPaused(false);

        startButton.setEnabled(true);
        startButton.setText("🎮 Новая игра");
        pauseButton.setEnabled(false);
        pauseButton.setText("⏸️ Пауза");
        menuButton.setEnabled(false);
        resultLabel.setText("");
        resultLabel.setForeground(Color.YELLOW);

        gamePanel.clearGame();
        updateScore();
        updateTimer();
        updateLevel();
        updateHP(100);
    }

    private void updateTimer() {
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        timerLabel.setText(String.format("⏰ %d:%02d", minutes, seconds));
    }

    private void updateScore() {
        scoreLabel.setText(String.format("🏆 Счёт: %d - %d", playerScore, enemyScore));
    }

    private void updateLevel() {
        levelLabel.setText(String.format("🎮 Уровень: %d", currentLevel));
    }

    public void updateHP(int hp) {
        String color;
        if (hp > 70) color = "#4CAF50";
        else if (hp > 30) color = "#FFA500";
        else color = "#FF5252";

        hpLabel.setText(String.format("<html><font color='%s'>❤️ ХП: %d/100</font></html>", color, hp));
    }

    public void endLevel(boolean win, String message) {
        gameRunning = false;
        gameTimer.stop();

        if (win) {
            currentLevel++;
            if (currentLevel > 5) {
                gameWon();
            } else {
                nextLevel();
            }
        } else {
            gameOver(message);
        }
    }

    private void nextLevel() {
        timeLeft = 180;
        gamePanel.startNewGame(currentLevel);
        gameRunning = true;
        gameTimer.start();
        updateLevel();
        updateTimer();
        gamePanel.requestFocusInWindow();

        JOptionPane.showMessageDialog(this,
                String.format("🎉 Уровень %d пройден! 🎉\nПереходим на уровень %d!",
                        currentLevel - 1, currentLevel),
                "Новый уровень!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void gameWon() {
        returnToMenu();
        String resultMessage = "🎊 ПОБЕДА! Вы прошли все 5 уровней!";
        resultLabel.setText(resultMessage);
        JOptionPane.showMessageDialog(this,
                String.format("🎊 Поздравляем! Вы прошли все 5 уровней! 🎊\nФинальный счёт: %d - %d",
                        playerScore, enemyScore),
                "ПОБЕДА!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void gameOver(String message) {
        returnToMenu();
        String resultMessage = "😢 ПРОИГРЫШ! " + message;
        resultLabel.setText(resultMessage);
        JOptionPane.showMessageDialog(this,
                String.format("😢 %s!\nФинальный счёт: %d - %d",
                        message, playerScore, enemyScore),
                "ПРОИГРЫШ",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void addPlayerScore() {
        playerScore++;
        updateScore();
    }

    public void addEnemyScore() {
        enemyScore++;
        updateScore();
    }

    public void checkLevelComplete() {
        if (gamePanel.getEnemies().isEmpty() && gameRunning) {
            endLevel(true, "Все энемии побеждены!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnowBattleGameFinal());
    }
}
