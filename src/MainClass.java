import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import javax.swing.Timer;
import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MainClass extends GraphicsProgram implements ActionListener {
    private Clip clip;

    public void playMusic(String filepath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Play the music in a loop
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public GOval food;
    private ArrayList<GRect> snakeBody;
    private int snakeX, snakeY, snakeWidth, snakeHeight;
    private static final int INITIAL_DELAY = 1000 / 10;  // 10 frames per second
    public Timer timer = new Timer(INITIAL_DELAY, this);
    private boolean isPlaying, isGameOver, gameOverDisplayed;
    private int score, previousScore;
    private int highScore = 0; // Store the high score
    private int loopsSinceLastFood = 0; // Track the number of loops since the last food was eaten
    private GLabel scoreLabel;
    private GLabel highScoreLabel;
    private GLabel instructions1;
    private GLabel instructions2;
    private GLabel title;
    private GLabel overMessage;
    private GLabel scoreMessage;
    private GLabel highScoreMessage;
    private static final int WALL_THICKNESS = 10;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    public void run() {
        playMusic("/Users/Aaron/IdeaProjects/SnakeLite/gaming.wav"); // Update with the correct path to your music file
        addKeyListeners();
        drawWalls();
        setBackground(Color.ORANGE);
        food = new Ball(450, 250, 20, 20);
        food.setFillColor(Color.RED);
        food.setFilled(true);
        add(food);
        snakeBody = new ArrayList<>();
        drawSnake();
        setUpInfo();  // Ensure this is called after drawing the snake
        timer.start();
        isGameOver = false;
        gameOverDisplayed = false;
    }

    public void setUpInfo() {
        score = 0;
        scoreLabel = new GLabel("Hi! Your current score is: " + score, 30, 30);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Adjust font size

        highScoreLabel = new GLabel("High Score: " + highScore, 30, 50);
        highScoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Adjust font size

        title = new GLabel("SNAKE LITE");
        title.setFont(new Font("SansSerif", Font.BOLD, 30)); // Set a larger font size for the title
        double titleWidth = title.getWidth();
        title.setLocation((CANVAS_WIDTH - titleWidth) / 2, CANVAS_HEIGHT / 2 - 100); // Move the title higher

        instructions1 = new GLabel("Welcome to the SnakeLite game! Eat as many balls as you can.");
        instructions1.setFont(new Font("SansSerif", Font.PLAIN, 15)); // Set a larger font size
        double instructions1Width = instructions1.getWidth();
        instructions1.setLocation((CANVAS_WIDTH - instructions1Width) / 2, CANVAS_HEIGHT / 2 + 20); // Move instructions1 further down

        instructions2 = new GLabel("Each time you eat a ball, the snake's length grows. Do NOT collide with a wall or hit yourself!");
        instructions2.setFont(new Font("SansSerif", Font.PLAIN, 15)); // Set a larger font size
        double instructions2Width = instructions2.getWidth();
        instructions2.setLocation((CANVAS_WIDTH - instructions2Width) / 2, CANVAS_HEIGHT / 2 + 50); // Move instructions2 further down

        add(scoreLabel);
        add(highScoreLabel);
        add(title);
        add(instructions1);
        add(instructions2);
    }

    boolean blockKey = false;
    String currentDirection = "";

    public void keyReleased(KeyEvent keyReleased) {
        if (!isGameOver) {
            removeInstructions();  // Remove instructions and title when any key is released
        } else if (gameOverDisplayed) {
            restartGame();  // Restart the game if it's over and an arrow key is pressed
        }

        if (blockKey) {
            blockKey = false;
            switch (keyReleased.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (!Objects.equals(currentDirection, "down")) {
                        goUp = true;
                        goLeft = false;
                        goRight = false;
                        goDown = false;
                        currentDirection = "up";
                        System.out.println("Up pressed!");
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (!Objects.equals(currentDirection, "up")) {
                        goUp = false;
                        goLeft = false;
                        goRight = false;
                        goDown = true;
                        currentDirection = "down";
                        System.out.println("Down pressed!");
                    }
                    break;

                case KeyEvent.VK_LEFT:
                    if (!Objects.equals(currentDirection, "right")) {
                        goUp = false;
                        goLeft = true;
                        goRight = false;
                        goDown = false;
                        currentDirection = "left";
                        System.out.println("Left pressed!");
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (!Objects.equals(currentDirection, "left")) {
                        goUp = false;
                        goLeft = false;
                        goRight = true;
                        goDown = false;
                        currentDirection = "right";
                        System.out.println("Right pressed!");
                    }
                    break;
            }
        }
    }

    public void randomFood() {
        if (!isGameOver) {
            Random random = new Random();
            int gridSize = 20; // Size of each grid block
            int maxX = (getWidth() - gridSize - 2 * WALL_THICKNESS) / gridSize;
            int maxY = (getHeight() - gridSize - 2 * WALL_THICKNESS) / gridSize;
            int randX = random.nextInt(maxX + 1) * gridSize + WALL_THICKNESS;
            int randY = random.nextInt(maxY + 1) * gridSize + WALL_THICKNESS;
            food.setLocation(randX, randY);
        }
    }

    public void drawWalls() {
        GRect topWall = new GRect(0, 0, CANVAS_WIDTH, WALL_THICKNESS);
        GRect bottomWall = new GRect(0, CANVAS_HEIGHT - WALL_THICKNESS, CANVAS_WIDTH, WALL_THICKNESS);
        GRect leftWall = new GRect(0, 0, WALL_THICKNESS, CANVAS_HEIGHT);
        GRect rightWall = new GRect(CANVAS_WIDTH - WALL_THICKNESS, 0, WALL_THICKNESS, CANVAS_HEIGHT);

        topWall.setFilled(true);
        bottomWall.setFilled(true);
        leftWall.setFilled(true);
        rightWall.setFilled(true);

        add(topWall);
        add(bottomWall);
        add(leftWall);
        add(rightWall);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    @Override
    public void init() {
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    public void drawSnake() {
        int size = 20;  // Size of each snake part
        int initialX = 350;
        int initialY = 250;
        int numParts = 10;

        for (int i = 0; i < numParts; i++) {
            SnakePart part = new SnakePart(initialX - i * size, initialY, size, size);
            add(part);
            snakeBody.add(part);
            if (i == 0) {
                part.setFillColor(Color.CYAN); // Set the head to white
            } else {
                part.setFillColor(Color.BLUE); // Set the rest of the body to blue
            }
            part.setFilled(true);
        }
    }

    public void addScore() {
        score++;
        scoreLabel.setLabel("Hi! Your current score is: " + score);
        loopsSinceLastFood = 0; // Reset the loop counter

        // Increase game speed slightly
        int newDelay = Math.max(timer.getDelay() - 10, 50); // Ensure the delay doesn't go below 50 ms
        timer.setDelay(newDelay);
    }

    public void removeInstructions() {
        if (instructions1 != null && instructions1.getParent() != null) {
            remove(instructions1);
        }
        if (instructions2 != null && instructions2.getParent() != null) {
            remove(instructions2);
        }
        if (title != null && title.getParent() != null) {
            remove(title);
        }
    }

    boolean goUp = false;
    boolean goLeft = false;
    boolean goRight = false;
    boolean goDown = false;

    public void keyPressed(KeyEvent keyPressed) {
        blockKey = true;
    }

    private void redrawSnake() {
        for (int i = snakeBody.size() - 1; i > 0; i--) {
            snakeBody.get(i).setLocation(snakeBody.get(i - 1).getX(), snakeBody.get(i - 1).getY());
        }
    }

    private void growSnake() {
        SnakePart newPart = new SnakePart(snakeBody.get(snakeBody.size() - 1).getX(), snakeBody.get(snakeBody.size() - 1).getY(), 20, 20);
        newPart.setFillColor(Color.BLUE);
        newPart.setFilled(true);
        snakeBody.add(newPart);
        add(newPart);
    }

    private void moveUp() {
        snakeBody.get(0).move(0, -20);
        System.out.println("Moving Up!");
    }

    private void moveDown() {
        snakeBody.get(0).move(0, 20);
        System.out.println("Moving Down!");
    }

    private void moveLeft() {
        snakeBody.get(0).move(-20, 0);
        System.out.println("Moving Left!");
    }

    private void moveRight() {
        snakeBody.get(0).move(20, 0);
        System.out.println("Moving Right!");
    }

    public Boolean intersectsFood(GRect head, GOval ball) {
        return head.getX() == ball.getX() && head.getY() == ball.getY();
    }

    public boolean intersectsSnake() {
        GRect head = snakeBody.get(0);
        for (int i = 1; i < snakeBody.size(); i++) { // Start from 1 to avoid checking head against itself
            GRect part = snakeBody.get(i);
            if (head.getX() == part.getX() && head.getY() == part.getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean intersectsWall() {
        GRect head = snakeBody.get(0);
        return head.getX() < WALL_THICKNESS ||
                head.getX() >= CANVAS_WIDTH - WALL_THICKNESS ||
                head.getY() < WALL_THICKNESS ||
                head.getY() >= CANVAS_HEIGHT - WALL_THICKNESS;
    }

    public void gameOver() {
        timer.stop();
        isGameOver = true;
        gameOverDisplayed = true;

        stopMusic(); // Stop the music when the game is over

        overMessage = new GLabel("Game is over!", CANVAS_WIDTH / 2 - 50, CANVAS_HEIGHT / 2);
        overMessage.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(overMessage);

        scoreMessage = new GLabel("Your score: " + score, CANVAS_WIDTH / 2 - 50, CANVAS_HEIGHT / 2 + 30);
        scoreMessage.setFont(new Font("SansSerif", Font.PLAIN, 18));
        add(scoreMessage);

        if (score > highScore) {
            highScore = score;
            highScoreMessage = new GLabel("New High Score!", CANVAS_WIDTH / 2 - 50, CANVAS_HEIGHT / 2 + 60);
            highScoreMessage.setFont(new Font("SansSerif", Font.PLAIN, 18));
            add(highScoreMessage);
        }

        highScoreLabel.setLabel("High Score: " + highScore);
    }

    public void restartGame() {
        removeAll();
        snakeBody.clear();
        timer.setDelay(INITIAL_DELAY); // Reset the timer delay
        timer.restart();
        isGameOver = false;
        gameOverDisplayed = false;
        run();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (!isGameOver) {
            if (intersectsSnake() || intersectsWall()) {
                gameOver();
            }
            if (intersectsFood(snakeBody.get(0), food)) {
                randomFood();
                growSnake();
                addScore();
            }
            if (goUp) {
                redrawSnake();
                moveUp();
            } else if (goLeft) {
                redrawSnake();
                moveLeft();
            } else if (goRight) {
                redrawSnake();
                moveRight();
            } else if (goDown) {
                redrawSnake();
                moveDown();
            }
        }
    }

    public static void main(String[] args) {
        new MainClass().start();
    }
}
