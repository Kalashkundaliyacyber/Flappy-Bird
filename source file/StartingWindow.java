
// StartingWindow.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StartingWindow extends JFrame {
    private JTextField playerNameField;
    private JButton startButton;
    private JLabel messageLabel;
    private JPanel contentPane;

    public StartingWindow() {
        setTitle("Flappy Bird - Get Ready");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 640); // Assuming the same size as the Flappy Bird game window
        setLocationRelativeTo(null); // Center the window
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        // Add background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("bg.jpg"));
        JLabel backgroundLabel = new JLabel(backgroundImage);
        contentPane.add(backgroundLabel, BorderLayout.CENTER);

        // Add message label
        messageLabel = new JLabel("Get Ready", SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPane.add(messageLabel, BorderLayout.NORTH);

        // Add player name input field
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        playerNameField = new JTextField(20);
        playerNameField.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setForeground(Color.WHITE);
        inputPanel.add(nameLabel);
        inputPanel.add(playerNameField);
        contentPane.add(inputPanel, BorderLayout.CENTER);

        // Add start button
        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText().trim();
                if (!playerName.isEmpty()) {
                    dispose(); // Close the starting window
                    startGame(playerName);
                } else {
                    JOptionPane.showMessageDialog(StartingWindow.this, "Please enter your name.");
                }
            }
        });
        contentPane.add(startButton, BorderLayout.SOUTH);

        setContentPane(contentPane);
        setVisible(true);
    }

    private void startGame(String playerName) {
        // Start the Flappy Bird game with the provided player name
        JFrame frame = new JFrame("Flappy Bird");
        Flappybirds flappyBirdGame = new Flappybirds(playerName);
        frame.add(flappyBirdGame);
        frame.setSize(360, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StartingWindow();
            }
        });
    }
}
