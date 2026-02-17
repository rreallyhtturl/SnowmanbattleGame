package com.snowmagic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener, MouseListener {
    private SnowBattleGameFinal parent;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Snowball> snowballs;
    private ArrayList<Snowflake> snowflakes;
    private ArrayList<Bonus> bonuses;
    private ArrayList<Tree> trees;
    private Random random;
    private Timer animationTimer;
    private long lastThrowTime = 0;
    private final long THROW_COOLDOWN = 400;
    private int level = 1;
    private boolean paused = false;

    private Image playerImage;
    private Image playerImageLeft;
    private Image enemyImage;
    private Image enemyImageLeft;
    private Image snowballImage;
    private Image treeImage;

    // Контролы клиента
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean faceRight = true;

    public GamePanel(SnowBattleGameFinal parent) {
        this.parent = parent;
        setBackground(new Color(135, 206, 235));
        setPreferredSize(new Dimension(1200, 700));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        setLayout(null);

        enemies = new ArrayList<>();
        snowballs = new ArrayList<>();
        snowflakes = new ArrayList<>();
        bonuses = new ArrayList<>();
        trees = new ArrayList<>();
        random = new Random();

        // Инициализация изображений
        playerImage = createSnowmanImage(Color.BLUE, true);
        playerImageLeft = createSnowmanImage(Color.BLUE, false);
        enemyImage = createSnowmanImage(Color.RED, true);
        enemyImageLeft = createSnowmanImage(Color.RED, false);
        snowballImage = createSnowballImage();
        treeImage = createTreeImage();

        // Таймер анимации (60 FPS игра)
        animationTimer = new Timer(16, e -> {
            if (!paused) {
                updateGame();
                repaint();
            }
        });
        animationTimer.start();
    }

    private Image createSnowmanImage(Color hatColor, boolean facingRight) {
        int width = 60;
        int height = 80;
        Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Тело снеговика
        // Нижний шар
        int ballSize = 40;
        int x = (width - ballSize) / 2;
        int y = height - ballSize;
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x, y, ballSize, ballSize);

        // Средний шар
        ballSize = 32;
        x = (width - ballSize) / 2;
        y = height - 60;
        g2d.fillOval(x, y, ballSize, ballSize);

        // Верхний шар
        ballSize = 28;
        x = (width - ballSize) / 2;
        y = height - 83;
        g2d.fillOval(x, y, ballSize, ballSize);

        // Шапка
        g2d.setColor(hatColor);
        g2d.fillRect(x - 5, y - 10, ballSize + 10, 12);
        g2d.fillOval(x - 2, y - 20, ballSize + 4, 15);

        // Глаза
        g2d.setColor(Color.BLACK);
        int eyeOffset = facingRight ? 4 : -4;
        g2d.fillOval(x + 6 + eyeOffset, y + 6, 4, 4);
        g2d.fillOval(x + 14 + eyeOffset, y + 6, 4, 4);

        // Нос Морква
        g2d.setColor(new Color(255, 140, 0));
        Polygon nose = new Polygon();
        if (facingRight) {
            nose.addPoint(x + 12, y + 10);
            nose.addPoint(x + 24, y + 12);
            nose.addPoint(x + 12, y + 15);
        } else {
            nose.addPoint(x + 12, y + 10);
            nose.addPoint(x + 0, y + 12);
            nose.addPoint(x + 12, y + 15);
        }
        g2d.fill(nose);

        // Рот
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawArc(x + 8, y + 16, 8, 4, 180, 180);

        // Пуговицы
        g2d.fillOval(x + 14, height - 45, 5, 5);
        g2d.fillOval(x + 14, height - 35, 5, 5);
        g2d.fillOval(x + 14, height - 25, 5, 5);

        // Палки-копалки
        g2d.setColor(new Color(139, 69, 19));
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (facingRight) {
            g2d.drawLine(x, y + 15, x - 15, y + 5);
            g2d.drawLine(x + ballSize, y + 15, x + ballSize + 15, y + 5);
        } else {
            g2d.drawLine(x, y + 15, x - 15, y + 25);
            g2d.drawLine(x + ballSize, y + 15, x + ballSize + 15, y + 25);
        }

        g2d.dispose();
        return image;
    }

    private Image createSnowballImage() {
        int size = 20;
        Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RadialGradientPaint gradient = new RadialGradientPaint(
                size / 2, size / 2, size / 2,
                new float[]{0.0f, 0.8f, 1.0f},
                new Color[]{Color.WHITE, new Color(230, 240, 255), new Color(200, 220, 240)}
        );
        g2d.setPaint(gradient);
        g2d.fillOval(0, 0, size, size);

        // Блеск
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillOval(4, 4, 6, 6);

        g2d.dispose();
        return image;
    }

    private Image createTreeImage() {
        int width = 90;
        int height = 130;
        Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Ствол
        g2d.setColor(new Color(101, 67, 33));
        g2d.fillRect(width / 2 - 10, height - 30, 20, 30);

        // Ярусы ёлки
        Color darkGreen = new Color(0, 80, 0);
        Color mediumGreen = new Color(0, 120, 0);
        Color lightGreen = new Color(0, 160, 0);

        // Нижний ярус
        drawRealisticTreeLayer(g2d, width / 2, height - 30, 75, 45, darkGreen);

        // Средний ярус
        drawRealisticTreeLayer(g2d, width / 2, height - 65, 60, 40, mediumGreen);

        // Верхний ярус
        drawRealisticTreeLayer(g2d, width / 2, height - 95, 45, 35, lightGreen);

        // Верхушка звездочка
        g2d.setColor(new Color(255, 215, 0));
        drawStar(g2d, width / 2, height - 121, 12);

        // Новогодние игрушки
        drawOrnaments(g2d, width / 2, height - 30, 45);

        g2d.dispose();
        return image;
    }

    private void drawRealisticTreeLayer(Graphics2D g2d, int centerX, int bottomY, int width, int height,
                                        Color color) {
        // Основной треугольник
        Polygon triangle = new Polygon();
        triangle.addPoint(centerX, bottomY - height);
        triangle.addPoint(centerX - width / 2, bottomY);
        triangle.addPoint(centerX + width / 2, bottomY);

        // Градиент для объёма
        GradientPaint gradient = new GradientPaint(
                centerX, bottomY - height, color.brighter(),
                centerX, bottomY, color.darker()
        );
        g2d.setPaint(gradient);
        g2d.fill(triangle);

        // Контур
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(triangle);

        // Чут-чуть иголок для реализма
        g2d.setColor(color.brighter());
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < 20; i++) {
            int x = centerX - width / 2 + random.nextInt(width);
            int y = bottomY - height / 2 + random.nextInt(height / 2);
            int len = 5 + random.nextInt(8);
            double angle = random.nextDouble() * Math.PI;
            int x2 = x + (int) (Math.cos(angle) * len);
            int y2 = y + (int) (Math.sin(angle) * len);
            g2d.drawLine(x, y, x2, y2);
        }
    }

    private void drawStar(Graphics2D g2d, int x, int y, int size) {
        Polygon star = new Polygon();
        for (int i = 0; i < 5; i++) {
            double angle = Math.PI / 2 + i * 2 * Math.PI / 5;
            int px = x + (int) (Math.cos(angle) * size);
            int py = y - (int) (Math.sin(angle) * size);
            star.addPoint(px, py);

            angle += Math.PI / 5;
            px = x + (int) (Math.cos(angle) * size / 2);
            py = y - (int) (Math.sin(angle) * size / 2);
            star.addPoint(px, py);
        }

        g2d.fill(star);
    }

    private void drawOrnaments(Graphics2D g2d, int centerX, int bottomY, int width) {
        Color[] colors = {Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.ORANGE};
        for (int i = 0; i < 12; i++) {
            double angle = i * Math.PI / 6;
            int radius = 15 + i % 3 * 8;
            int x = centerX + (int) (Math.cos(angle) * radius);
            int y = bottomY - 30 + (int) (Math.sin(angle) * radius);

            g2d.setColor(colors[i % colors.length]);
            g2d.fillOval(x - 4, y - 4, 8, 8);

            // Блеск на игрушках
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x - 2, y - 2, 3, 3);
        }
    }

    public void startNewGame(int newLevel) {
        level = newLevel;
        player = new Player(200, 350);
        enemies.clear();
        snowballs.clear();
        snowflakes.clear();
        bonuses.clear();
        trees.clear();

        // Настройки уровней
        int enemyCount;
        int enemyHealth;
        double enemyAttackSpeed;

        switch (level) {
            case 1:
                enemyCount = 3;
                enemyHealth = 80;
                enemyAttackSpeed = 0.005;
                break;
            case 2:
                enemyCount = 4;
                enemyHealth = 100;
                enemyAttackSpeed = 0.007;
                break;
            case 3:
                enemyCount = 5;
                enemyHealth = 120;
                enemyAttackSpeed = 0.009;
                break;
            case 4:
                enemyCount = 6;
                enemyHealth = 150;
                enemyAttackSpeed = 0.011;
                break;
            case 5:
                enemyCount = 8;
                enemyHealth = 200;
                enemyAttackSpeed = 0.015;
                break;
            default:
                enemyCount = 3;
                enemyHealth = 80;
                enemyAttackSpeed = 0.005;
        }

        // Создание энимов
        for (int i = 0; i < enemyCount; i++) {
            int x = 800 + random.nextInt(300);
            int y = 100 + random.nextInt(500);
            enemies.add(new Enemy(x, y, level, enemyHealth, enemyAttackSpeed));
        }

        // Создание снежинок
        for (int i = 0; i < 150; i++) {
            snowflakes.add(new Snowflake(
                    random.nextInt(getWidth()),
                    random.nextInt(getHeight()),
                    random.nextDouble() * 2 + 1
            ));
        }

        // Создание ёлок
        for (int i = 0; i < 7; i++) {
            int x = 50 + i * 160;
            int y = 480 + random.nextInt(40);
            trees.add(new Tree(x, y));
        }

        repaint();
        requestFocusInWindow();
    }

    public void clearGame() {
        player = null;
        enemies.clear();
        snowballs.clear();
        bonuses.clear();
        repaint();
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (paused && e.getKeyCode() == KeyEvent.VK_P) {
            parent.togglePause();
            return;
        }

        if (paused) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                upPressed = true;
                break;
            case KeyEvent.VK_S:
                downPressed = true;
                break;
            case KeyEvent.VK_A:
                leftPressed = true;
                faceRight = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = true;
                faceRight = true;
                break;
            case KeyEvent.VK_SPACE:
                throwSnowballForward();
                break;
            case KeyEvent.VK_P:
                parent.togglePause();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void throwSnowballForward() {
        if (player == null || System.currentTimeMillis() - lastThrowTime < THROW_COOLDOWN) {
            return;
        }

        int targetX = faceRight ? player.x + 200 : player.x - 200;
        int targetY = player.y;

        snowballs.add(new Snowball(player.x, player.y, targetX, targetY, true, player.damage));
        lastThrowTime = System.currentTimeMillis();
    }

    private void updateGame() {
        if (player == null) return;

        // Обновление клиента
        int moveSpeed = player.isSpeedBoosted ? 6 : 4;
        if (upPressed) player.y -= moveSpeed;
        if (downPressed) player.y += moveSpeed;
        if (leftPressed) player.x -= moveSpeed;
        if (rightPressed) player.x += moveSpeed;

        // Обновление направления
        if (leftPressed) faceRight = false;
        if (rightPressed) faceRight = true;

        // Ограничение движения
        player.x = Math.max(50, Math.min(getWidth() - 50, player.x));
        player.y = Math.max(50, Math.min(getHeight() - 50, player.y));

        // Обновление эффектов клиента
        player.updateEffects();

        // Обновление снежков
        for (int i = snowballs.size() - 1; i >= 0; i--) {
            Snowball snowball = snowballs.get(i);
            snowball.update();

            // Столкновения снежков клиента с энемиами
            if (snowball.isPlayerSnowball) {
                for (int j = enemies.size() - 1; j >= 0; j--) {
                    Enemy enemy = enemies.get(j);
                    if (snowball.getBounds().intersects(enemy.getBounds())) {
                        snowballs.remove(i);
                        boolean killed = enemy.takeDamage(snowball.damage);
                        if (killed) {
                            enemies.remove(j);
                            parent.addPlayerScore();

                            if (random.nextDouble() < 0.4) {
                                bonuses.add(new Bonus(enemy.x, enemy.y));
                            }
                        }
                        break;
                    }
                }
            }
            // Столкновения снежков энимов с клиентом
            else if (snowball.getBounds().intersects(player.getBounds())) {
                snowballs.remove(i);
                if (!player.isInvincible) {
                    player.takeDamage(snowball.damage);
                    parent.updateHP(player.health);
                    if (player.health <= 0) {
                        parent.endLevel(false, "Попробуй еще раз!");
                        return;
                    }
                }
            }

            // Удаление снежков за пределами
            if (snowball.x < -100 || snowball.x > getWidth() + 100 ||
                    snowball.y < -100 || snowball.y > getHeight() + 100) {
                snowballs.remove(i);
            }
        }

        // Обновление энеми
        for (Enemy enemy : enemies) {
            enemy.update(player, snowballs, random, getWidth(), getHeight());

            // Атака энеми
            if (player != null && random.nextDouble() < enemy.attackSpeed) {
                int predictX = player.x;
                int predictY = player.y;

                // Предсказание движения
                if (Math.abs(player.x - enemy.lastPlayerX) > 5) {
                    predictX = player.x + (player.x - enemy.lastPlayerX);
                }
                if (Math.abs(player.y - enemy.lastPlayerY) > 5) {
                    predictY = player.y + (player.y - enemy.lastPlayerY);
                }

                snowballs.add(new Snowball(
                        enemy.x, enemy.y,
                        predictX, predictY,
                        false,
                        enemy.damage
                ));
                enemy.lastPlayerX = player.x;
                enemy.lastPlayerY = player.y;
            }
        }

        // Обновление снежинок
        for (Snowflake flake : snowflakes) {
            flake.update(getWidth(), getHeight());
        }

        // Обновление бонусов
        for (int i = bonuses.size() - 1; i >= 0; i--) {
            Bonus bonus = bonuses.get(i);
            bonus.update();

            if (bonus.getBounds().intersects(player.getBounds())) {
                bonuses.remove(i);
                applyBonus(bonus.type);
            }

            if (bonus.timeLeft <= 0) {
                bonuses.remove(i);
            }
        }

        // Проверка завершения уровня
        if (enemies.isEmpty()) {
            parent.checkLevelComplete();
        }
    }

    private void applyBonus(int type) {
        switch (type) {
            case 0: // Лечение
                player.health = Math.min(100, player.health + 30);
                parent.updateHP(player.health);
                break;
            case 1: // Ускорение
                player.activateSpeedBoost();
                break;
            case 2: // Неуязвимость
                player.activateInvincibility();
                break;
            case 3: // Усиление атаки
                player.activateDamageBoost();
                break;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Фон с градиентом
        GradientPaint skyGradient = new GradientPaint(
                0, 0, new Color(135, 206, 250),
                0, getHeight(), new Color(176, 224, 230)
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Горы на заднем плане
        drawMountains(g2d);

        // Сугробы
        g2d.setColor(Color.WHITE);
        g2d.fillOval(-100, getHeight() - 120, getWidth() + 200, 150);
        g2d.fillOval(300, getHeight() - 100, 600, 120);

        // Рисование ёлок
        for (Tree tree : trees) {
            g2d.drawImage(treeImage, tree.x, tree.y - 10, 90, 130, this);
        }

        // Рисование снежинок
        g2d.setColor(new Color(255, 255, 255, 220));
        for (Snowflake flake : snowflakes) {
            int size = (int) (flake.speed * 1.5);
            g2d.fillOval((int) flake.x, (int) flake.y, size, size);
        }

        // Рисование бонусов
        for (Bonus bonus : bonuses) {
            bonus.draw(g2d);
        }

        // Рисование клиента
        if (player != null) {
            Image currentPlayerImage = faceRight ? playerImage : playerImageLeft;
            g2d.drawImage(currentPlayerImage, player.x - 30, player.y - 40, 60, 80,
                    this);

            // Полоска здоровья клиента
            drawHealthBar(g2d, player.x - 30, player.y - 50, 60, 5, player.health, 100,
                    Color.GREEN);

            // Эффекты клиента
            if (player.isSpeedBoosted) {
                g2d.setColor(new Color(0, 255, 255, 100));
                g2d.fillOval(player.x - 35, player.y - 45, 70, 85);
            }
            if (player.isInvincible) {
                g2d.setColor(new Color(255, 255, 0, 100));
                g2d.fillOval(player.x - 35, player.y - 45, 70, 85);
            }

            // Индикатор перезарядки
            if (System.currentTimeMillis() - lastThrowTime < THROW_COOLDOWN) {
                double progress = (double) (System.currentTimeMillis() - lastThrowTime) / THROW_COOLDOWN;
                int barWidth = 40;
                int barHeight = 4;
                int filledWidth = (int) (barWidth * progress);

                // Координаты полоски
                int barX = player.x - 20;
                int barY = player.y - 75;

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1)); // Толщина обводки
                g2d.drawRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);

                // Полоска перезарядки - параметры
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(player.x - 20, player.y - 75, barWidth, 4);
                g2d.setColor(Color.CYAN);
                g2d.fillRect(player.x - 20, player.y - 75, filledWidth, 4);
            }
        }

        // Рисование энимов
        for (Enemy enemy : enemies) {
            boolean enemyFaceRight = player == null || enemy.x < player.x;
            Image currentEnemyImage = enemyFaceRight ? enemyImage : enemyImageLeft;
            g2d.drawImage(currentEnemyImage, enemy.x - 30, enemy.y - 40, 60, 80, this);

            // Полоска здоровья энемиа
            drawHealthBar(g2d, enemy.x - 30, enemy.y - 50, 60, 5, enemy.health, enemy.maxHealth,
                    Color.RED);

            // Индикатор атаки
            if (enemy.isAttacking) {
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.fillOval(enemy.x - 35, enemy.y - 45, 70, 85);
            }
        }

        // Рисование снежков
        for (Snowball snowball : snowballs) {
            g2d.drawImage(snowballImage, snowball.x - 10, snowball.y - 10, 20, 20, this);

            // След от снежка
            g2d.setColor(new Color(255, 255, 255, 50));
            for (int i = 0; i < 3; i++) {
                int offset = i * 5;
                g2d.fillOval(snowball.x - 10 - offset, snowball.y - 10 - offset,
                        20 + offset * 2, 20 + offset * 2);
            }
        }
        // Рисование информации об игре
        drawGameInfo(g2d);

        // Эффект паузы
        if (paused) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Dialog", Font.BOLD, 48));
            String pauseText = "ПАУЗА";
            int textWidth = g2d.getFontMetrics().stringWidth(pauseText);
            g2d.drawString(pauseText, getWidth() / 2 - textWidth / 2, getHeight() / 2);

            g2d.setFont(new Font("Dialog", Font.PLAIN, 20));
            String continueText = "Нажмите P для продолжения";
            textWidth = g2d.getFontMetrics().stringWidth(continueText);
            g2d.drawString(continueText, getWidth() / 2 - textWidth / 2, getHeight() / 2 + 40);
        }

        // Новогодняя надпись
        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Dialog", Font.BOLD, 36));
        String title = "🎄С НОВЫМ ГОДОМ!🎄";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, getWidth() / 2 - titleWidth / 2, 40);

        // Если панель не в фокусе
        if (!hasFocus() && !paused && player != null) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(getWidth() / 2 - 250, getHeight() / 2 - 60, 500, 120);

            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Dialog", Font.BOLD, 24));
            String focusText = "КЛИКНИТЕ СЮДА ДЛЯ УПРАВЛЕНИЯ";
            int textWidth = g2d.getFontMetrics().stringWidth(focusText);
            g2d.drawString(focusText, getWidth() / 2 - textWidth / 2, getHeight() / 2 - 10);

            g2d.setFont(new Font("Dialog", Font.PLAIN, 16));
            String hintText = "Управление: WASD - движение, SPACE - бросок, P - пауза";
            textWidth = g2d.getFontMetrics().stringWidth(hintText);
            g2d.drawString(hintText, getWidth() / 2 - textWidth / 2, getHeight() / 2 + 30);
        }
    }

    private void drawMountains(Graphics2D g2d) {
        g2d.setColor(new Color(120, 120, 120));

        // Горы на заднем плане
        Polygon mountain1 = new Polygon();
        mountain1.addPoint(0, 600);
        mountain1.addPoint(400, 600);
        mountain1.addPoint(200, 400);
        g2d.fill(mountain1);

        Polygon mountain2 = new Polygon();
        mountain2.addPoint(300, 600);
        mountain2.addPoint(500, 350);
        mountain2.addPoint(1100, 900);
        g2d.fill(mountain2);

        Polygon mountain3 = new Polygon();
        mountain3.addPoint(600, 600);
        mountain3.addPoint(800, 420);
        mountain3.addPoint(1000, 600);
        g2d.fill(mountain3);

        // Снег на горах
        g2d.setColor(Color.WHITE);
        Polygon snow1 = new Polygon();
        snow1.addPoint(100, 500);
        snow1.addPoint(200, 400);
        snow1.addPoint(300, 500);
        g2d.fill(snow1);

        Polygon snow2 = new Polygon();
        snow2.addPoint(420, 450);
        snow2.addPoint(500, 350);
        snow2.addPoint(609, 450);
        g2d.fill(snow2);

        Polygon snow3 = new Polygon();
        snow3.addPoint(711, 500);
        snow3.addPoint(800, 420);
        snow3.addPoint(890, 500);
        g2d.fill(snow3);
    }

    private void drawGameInfo(Graphics2D g2d) {
        // Фон для информации
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(10, 80, 250, 120, 15, 15);

        // Белая рамка
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(10, 80, 250, 120, 15, 15);

        // Заголовок
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Dialog", Font.BOLD, 18));
        g2d.drawString("📊 ИНФОРМАЦИЯ", 20, 105);

        // Информация об игроке
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 16));
        if (player != null) {
            g2d.drawString(String.format("🎮 Игрок: %d/%d HP", player.health, player.maxHealth), 20, 130);
        } else {
            g2d.drawString("🎮 Игрок: -", 20, 130);
        }

        // Информация о энемиах
        g2d.setColor(Color.ORANGE);
        g2d.drawString(String.format("🎯 Врагов: %d", enemies.size()), 20, 155);

        // Информация об уровне
        g2d.setColor(Color.PINK);
        g2d.drawString(String.format("📈 Уровень: %d", level), 20, 180);

        // Индикатор перезарядки
        if (player != null && System.currentTimeMillis() - lastThrowTime < THROW_COOLDOWN) {
            double progress = (double) (System.currentTimeMillis() - lastThrowTime) / THROW_COOLDOWN;
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(20, 185, 200, 10);
            g2d.setColor(Color.CYAN);
            g2d.fillRect(20, 185, (int) (200 * progress), 10);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Dialog", Font.PLAIN, 10));
            g2d.drawString("Перезарядка", 100, 193);
        }
    }

    private void drawHealthBar(Graphics2D g2d, int x, int y, int width, int height,
                               int current, int max, Color color) {
        // Фон полоски
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x - 1, y - 1, width + 2, height + 2);

        // Текущее здоровье
        int fillWidth = (int) ((double) current / max * width);
        g2d.setColor(color);
        g2d.fillRect(x, y, fillWidth, height);

        // Контур
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, width, height);

        // Текст с процентами (если полоска широкая)
        if (width > 40) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Dialog", Font.BOLD, 13));
            String text = current + "/" + max;
            int textWidth = g2d.getFontMetrics().stringWidth(text);
            g2d.drawString(text, x + width / 2 - textWidth / 2, y + height - 8);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}