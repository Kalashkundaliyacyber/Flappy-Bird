import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartingWindow extends JFrame {
    private JTextField playerNameField;
    private JButton startButton;
    private JLabel messageLabel;
    private JPanel contentPane;
    private Timer countdownTimer;
    private int countdownValue = 3; // Initial countdown value

    public StartingWindow() {
        setTitle("Flappy Bird - Get Ready");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 640); // Assuming the same size as the Flappy Bird game window
        setLocationRelativeTo(null); // Center the window
        setResizable(false);

        // Create a content pane with background image
        contentPane = new BackgroundPanel("bg.jpg");
        contentPane.setLayout(null); // Set layout to null for manual positioning

        // Add message label
        messageLabel = new JLabel("Get Ready", SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        messageLabel.setBounds(0, 50, 360, 50); // Position the label at the top center
        contentPane.add(messageLabel);

        // Add player name input field
        playerNameField = new JTextField(20);
        playerNameField.setFont(new Font("Arial", Font.PLAIN, 16));
        playerNameField.setBounds(60, 150, 240, 30); // Position the field below the label
        contentPane.add(playerNameField);

        // Add "Enter your name" label
        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setForeground(Color.WHITE); // Set the text color to white
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setBounds(60, 120, 240, 30); // Position the label above the text field
        contentPane.add(nameLabel);

        // Add start button
        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText().trim();
                if (!playerName.isEmpty()) {
                    startCountdown(); // Start countdown when the button is clicked
                } else {
                    JOptionPane.showMessageDialog(StartingWindow.this, "Please enter your name.");
                }
            }
        });
        startButton.setBounds(100, 200, 160, 30); // Position the button below the text field
        contentPane.add(startButton);

        setContentPane(contentPane);
        setVisible(true);
    }

    // Method to start the countdown
    private void startCountdown() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdownValue > 0) {
                    messageLabel.setText(String.valueOf(countdownValue)); // Update message label with countdown value
                    countdownValue--;
                } else {
                    dispose(); // Close the starting window
                    countdownTimer.stop();
                    startGame(playerNameField.getText().trim()); // Start the game
                }
            }
        });
        countdownTimer.start();
    }

    private void startGame(String playerName) {
        // Start the Flappy Bird game with the provided player name
        Flappybirds flappyBirdGame = new Flappybirds(playerName);
        flappyBirdGame.requestFocus(); // Ensure the game panel has focus
        flappyBirdGame.setFocusable(true); // Make the game panel focusable
        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(360, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(flappyBirdGame);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StartingWindow();
            }
        });
    }

    // Inner class for JPanel with background image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            setPreferredSize(new Dimension(backgroundImage.getWidth(null), backgroundImage.getHeight(null)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
