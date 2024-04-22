import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Flappybirds extends JPanel implements ActionListener, KeyListener {
    int boardWitdth = 360;
    int boardHeight = 640;

    // Image
    Image background;
    Image birdimg;
    Image bottompipeImg;
    Image topPipeImg;

    // Bird
    int birdX = boardWitdth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // pipes
    int pipeX = boardWitdth ;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe
    {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;

        Image img;
        boolean passed = false;
        Pipe (Image img)
        {
            this.img=img;
        }
    }

    // game logic
    Bird bird;
    int velocityX = -4; // move pipes to the left speed (simulates bird moving right)
    int velocityY = 0; // move bird up/down speed.
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placepipetimer;
    boolean gameOver = false;
    double Score = 0;

    Flappybirds() {
        setPreferredSize(new Dimension(boardWitdth, boardHeight));

        setFocusable(true);
        addKeyListener(this);
        // load image
        background = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdimg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        bottompipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();

        //Bird
        bird = new Bird(birdimg);


        pipes = new ArrayList<Pipe>();
        // Place pipe
        placepipetimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placepipe();
            }
        });
        placepipetimer.start();

        // game loop
        gameLoop = new Timer(1000 / 60, this); // how long it takes to start timer, milliseconds gone between frames
        gameLoop.start();

    }

    public void placepipe ()
    {
        int randomPipeY = (int) (pipeY - pipeHeight/4 -Math.random()*(pipeHeight/2));

        int openingSpace = boardHeight/4;

        Pipe toppipe = new Pipe(topPipeImg);
        toppipe.y = randomPipeY;
        pipes.add(toppipe);
        Pipe bottPipe = new Pipe(bottompipeImg);
        bottPipe.y = toppipe.y + pipeHeight + openingSpace;
        pipes.add(bottPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, boardWitdth, boardHeight, null);

        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i<pipes.size(); i++) 
        {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y,pipe.width,pipe.height,null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) Score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) Score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i =0 ;  i < pipes.size() ; i++)
        {
            
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width)
            {
                pipe.passed = true;
                Score += 0.5;
                
            }

            if (collision(bird, pipe))
            {
                gameOver= true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }


    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placepipetimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) 
            {
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                Score = 0;
                gameLoop.start();
                placepipetimer.start();
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

}