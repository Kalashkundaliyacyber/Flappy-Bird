import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;

public class Flappybirds extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    BufferedImage background;
    BufferedImage birdImg;
    BufferedImage bottomPipeImg;
    BufferedImage topPipeImg;
    BufferedImage blurOverlay; // New: to hold the blur effect overlay

    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        BufferedImage img;

        Bird(BufferedImage img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        BufferedImage img;
        boolean passed = false;

        Pipe(BufferedImage img) {
            this.img = img;
        }
    }

    // Game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    boolean blurActive = false; // New: flag to indicate whether the blur overlay is active

    private String playerName; // Variable to store player's name

    public Flappybirds(String playerName) {
        this.playerName = playerName; // Assigning player's name
        setPreferredSize(new Dimension(boardWidth, boardHeight)); // Setting panel size
        setFocusable(true); // Making panel focusable for key events
        addKeyListener(this); // Adding key listener to the panel

        // Load images
        background = loadImage("./flappybirdbg.jpg");
        birdImg = loadImage("./flappybird.png");
        bottomPipeImg = loadImage("./bottompipe.png");
        topPipeImg = loadImage("./toppipe.png");

        // Bird
        bird = new Bird(birdImg);

        pipes = new ArrayList<Pipe>();

        // Place pipe
        placePipeTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipe();
            }
        });
        placePipeTimer.start();

        // Game loop
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        // Load the blur overlay
        blurOverlay = createBlurOverlay();
        addKeyListener(this);

    }

    public BufferedImage loadImage(String path) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return image;
    }

    public BufferedImage createBlurOverlay() {
        BufferedImage overlay = new BufferedImage(boardWidth, boardHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = overlay.createGraphics();
        g2d.setColor(new Color(0, 0, 0, 200)); // Semi-transparent black color
        g2d.fillRect(0, 0, boardWidth, boardHeight);
        g2d.dispose();
        return overlay;
    }

    public void placePipe() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        if (blurActive) { // Draw the blur overlay if it's active
            g.drawImage(blurOverlay, 0, 0, null);
        }
    }

    public void draw(Graphics g) {
        // Draw background
        g.drawImage(background, 0, 0, boardWidth, boardHeight, null);

        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if (gameOver) {
            // Highlight "Game Over" message
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.white);
            String gameOverMsg = "Game Over";
            int gameOverX = (boardWidth - g.getFontMetrics().stringWidth(gameOverMsg)) / 2;
            int gameOverY = boardHeight / 2 - 30;
            g.drawString(gameOverMsg, gameOverX, gameOverY);

            // Highlight score at the top left corner
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            String scoreString = "Score: " + (int) score;
            int scoreX = 10; // Adjusted to position at the top left corner
            int scoreY = 35;
            g.drawString(scoreString, scoreX, scoreY);

        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

        // Add play again button if game is over
        if (gameOver) {
            JButton playAgainButton = new JButton("Play Again");
            playAgainButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resetGame();
                }
            });
            playAgainButton.setBounds(boardWidth / 2 - 75, boardHeight / 2 - 25, 150, 50);
            add(playAgainButton);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
                blurActive = true; // Activate blur effect when game over
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
            blurActive = true; // Activate blur effect when game over
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    public void resetGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        blurActive = false; // Deactivate blur effect when game reset
        score = 0;
        gameLoop.start();
        placePipeTimer.start();
        // Remove the play again button
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                remove(component);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocityY = -9; // Bird jumps only if the game is not over
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
