import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;
import java.util.ArrayList;

public class IdealGasSim extends JPanel implements ActionListener, KeyListener {
    private int WIDTH = 1200; // 600/11.5
    private int HEIGHT = 1000;
    private int BALL_SIZE = 10;
    private double R = 8.314; // Gas constant
    private double molarMass = 0; // Molar mass of Tungsten hexafluoride //O2 (Oxygen) = 0.032 kg/mol
    private double temperature = 0.001; // Kelvin
    private int numParticles = 1; //Moles
    private String name = "";
    private int volume = 2; //Liters
    private String warning = "Everything is fine";
    private double pressure = ((numParticles * R * temperature) / volume);
    private double temperatureIncrement = 1.0e-3;
    private ArrayList<Integer> ballX = new ArrayList<>();
    private ArrayList<Integer> ballY = new ArrayList<>();
    private ArrayList<Double> ballSpeedX = new ArrayList<>();
    private ArrayList<Double> ballSpeedY = new ArrayList<>();
    private ArrayList<Double> ballSpeeds = new ArrayList<>();

    private Timer timer;

    public IdealGasSim() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this); // Add key listener to the panel
        setFocusable(true); // Make the panel focusable to receive key events
        addBall(); 
        timer = new Timer(20, this);
        timer.start();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Light or Heavy?");
        String mode = scanner.nextLine();
        if (mode.toLowerCase().equals("light")) {
            molarMass = 0.032;
            name = "Oxygen (O2)";
        } else if (mode.toLowerCase().equals("heavy")) {
            molarMass = 0.29783;
            name = "Tungsten hexafluoride  (WF6)";
        }
        scanner.close();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        double speed = Math.sqrt((3 * R * temperature) / molarMass);
        for (int i = 0; i < ballX.size(); i++) {
            g.fillOval(ballX.get(i) , ballY.get(i) , BALL_SIZE, BALL_SIZE);
            pressure = ((numParticles * R * temperature) / volume);
            ballSpeeds.add(speed);
        }
        g.drawString("Speed is calculated as: " + ("√((3 * R constant * Temperature) / MolarMass"),WIDTH/50, 60 + (HEIGHT / 2));
        g.drawString("Press Up Arrow to Increase Temperature ",WIDTH/50,40 + (HEIGHT/2));
        g.drawString("PRESS SPACE to increase moles" ,WIDTH/50,(20 + (HEIGHT/2)));

        g.drawString("Average Speed: " + String.format("%.2f", speed) + " m/s", (WIDTH - WIDTH/4), HEIGHT/50);
        g.drawString("Pressure : " + String.format("%.2f",pressure) + " atm",(WIDTH - WIDTH/4), HEIGHT/30);
        g.drawString("Num of Gas Particles: " + numParticles + "", (WIDTH - WIDTH/4), HEIGHT/20);

        g.drawString("Temperature: " + String.format("%.3f",temperature) + " Kelvin ", WIDTH/120, HEIGHT/60);
        g.drawString("Gas: " + name, WIDTH/120, (HEIGHT/60) + 20);

        g.drawString("At " + String.format("%.3f",pressure) + " atm " + warning,WIDTH/2,HEIGHT/2);

        if (pressure >= 3) {
            warning = "Nitrogen in the air becomes narcotic";
        } else if (pressure >= 15) {
            warning = "helium can affect the nervous system, causing \n High Pressure Nervous Syndrome, also known as Helium Tremors";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ballSpeeds.clear(); // Clear the speed list for the next frame
        double speed = Math.sqrt((3 * R * temperature) / molarMass);
        for (int i = 0; i < ballX.size(); i++) {
            ballX.set(i, ballX.get(i) + (int) (ballSpeedX.get(i) * speed));
            ballY.set(i, ballY.get(i) + (int) (ballSpeedY.get(i) * speed));

            // Check for collisions with walls
            if (ballX.get(i) - BALL_SIZE / 2 < 0) {
                ballX.set(i, BALL_SIZE / 2);
                ballSpeedX.set(i, -ballSpeedX.get(i)); // Reverse horizontal speed
            } else if (ballX.get(i) + BALL_SIZE / 2 >= WIDTH) {
                ballX.set(i, WIDTH - BALL_SIZE / 2);
                ballSpeedX.set(i, -ballSpeedX.get(i)); // Reverse horizontal speed
            }

            if (ballY.get(i) - BALL_SIZE / 2 < 0) {
                ballY.set(i, BALL_SIZE / 2);
                ballSpeedY.set(i, -ballSpeedY.get(i)); // Reverse vertical speed
            } else if (ballY.get(i) + BALL_SIZE / 2 >= HEIGHT) {
                ballY.set(i, HEIGHT - BALL_SIZE / 2);
                ballSpeedY.set(i, -ballSpeedY.get(i)); // Reverse vertical speed
            }

            // Check for collisions between balls
            for (int j = i + 1; j < ballX.size(); j++) {
                double dx = ballX.get(i) - ballX.get(j);
                double dy = ballY.get(i) - ballY.get(j);
                double distance = Math.sqrt(dx * dx + dy * dy);
                double ballDistance = BALL_SIZE;
                if (distance < ballDistance) {
                    ballSpeedX.set(i, -ballSpeedX.get(i)); 
                    ballSpeedY.set(i, -ballSpeedY.get(i)); 
                    ballSpeedX.set(j, -ballSpeedX.get(j)); 
                    ballSpeedY.set(j, -ballSpeedY.get(j)); 
                }
            }
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {
            temperature += temperatureIncrement;
        } else if (keyCode == KeyEvent.VK_DOWN) {
            temperature -= temperatureIncrement;
            temperature = Math.max(0,temperature);
        } 
        else if (keyCode == KeyEvent.VK_SPACE) {
            addBall();
            numParticles++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void addBall() {
        ballX.add(WIDTH / 2);
        ballY.add(HEIGHT / 2);
        ballSpeedX.add(Math.random() * 20 - 10); 
        ballSpeedY.add(Math.random() * 20 - 10); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ideal Gas Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new IdealGasSim());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}