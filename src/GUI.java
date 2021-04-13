import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GUI {
    private JPanel mainPanel;
    private JPanel screen1;
    private JPanel screen2;
    private JButton nextButton;
    private JButton nextTo3Button;
    private JPanel screen3;
    private JButton startAt1Button;
    private JButton addRoomButton;
    private JTextArea rooms;
    private JPanel screen0;
    private JButton startHomeEnergyCalculatorButton;
    private JButton confirmButton;
    private JTextArea bulbInfo;
    private JButton resetButtonScreen1;
    private JTextField lightingHourTextField;

    private static HashMap<String, Integer> heating;
    private static HashMap<Integer, Integer> lightingTypes;
    private static HashMap<Integer, Integer> lightingHours;

    private static int lightingSlidePosition;

    public GUI() {
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screen1.setVisible(false);
                screen2.setVisible(true);

                int currentKey = (int) lightingTypes.keySet().toArray()[lightingSlidePosition];
                bulbInfo.setText("Out of 24 hours, how often do you use the " + currentKey + "W bulbs (on average)? You have "
                        + lightingTypes.get(currentKey) + " in your house.");
            }
        });
        nextTo3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screen1.setVisible(false);
                screen2.setVisible(false);
                screen3.setVisible(true);
            }
        });
        startAt1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screen1.setVisible(true);
                screen2.setVisible(false);
                screen3.setVisible(false);
            }
        });
        startHomeEnergyCalculatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder roomstr = new StringBuilder();

                for (String str : heating.keySet()) {
                    roomstr.append(str);
                    roomstr.append('\n');
                }

                rooms.setText(roomstr.toString());
                screen0.setVisible(false);
                screen1.setVisible(true);
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lightingSlidePosition < lightingTypes.keySet().size()) {

                    String hourValueStr = lightingHourTextField.getText();
                    int hourValue = -1;

                    try {
                        float temp = Float.parseFloat(hourValueStr);
                        hourValue = Math.round(temp);
                    } catch (Exception ex1) {
                        String currentInfo = bulbInfo.getText();
                        currentInfo += "\n\n Please enter a number for the hours (ex. 18)";
                        bulbInfo.setText(currentInfo);
                        return;
                    }

                    lightingHours.put((int) lightingTypes.keySet().toArray()[lightingSlidePosition], hourValue);
                    lightingHourTextField.setText("");
                    lightingSlidePosition += 1;
                    if (lightingSlidePosition < lightingTypes.keySet().size()) {
                        int currentKey = (int) lightingTypes.keySet().toArray()[lightingSlidePosition];
                        bulbInfo.setText("Out of 24 hours, how often do you use the " + currentKey + "W bulbs (on average)? You have "
                                + lightingTypes.get(currentKey) + " in your house.");
                    } else {
                        bulbInfo.setText("Data has been inputted for all of the lightbulbs in your home. Please continue to the next page");
                        confirmButton.setEnabled(false);
                        lightingHourTextField.setVisible(false);
                        confirmButton.setVisible(false);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        heating = new HashMap<String, Integer>();
        lightingTypes = new HashMap<Integer, Integer>();
        lightingHours = new HashMap<Integer, Integer>();

        lightingSlidePosition = 0;

        File sensorData = new File("src/dataFromSensors.txt");
        Scanner dataScanner;

        try {
            dataScanner = new Scanner(sensorData);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading dataFromSensors.txt, exiting program.");
            return;
        }

        boolean header = true;
        boolean readingLighting = false;
        while (dataScanner.hasNextLine()) {
            String data = dataScanner.nextLine();
            if (header) {
                header = false;
            } else {
                if (data.isBlank()) {
                    readingLighting = !readingLighting;
                    header = true;
                } else {
                    String[] dataSplit = data.split(", ");
                    if (!readingLighting) {
                        heating.put(dataSplit[0], Integer.parseInt(dataSplit[1]));
                    } else {
                        lightingTypes.put(Integer.parseInt(dataSplit[0]), Integer.parseInt(dataSplit[1]));
                    }
                }
            }
        }

        JFrame myFrame = new JFrame("Mental Health Resources Simulation");
        myFrame.setContentPane(new GUI().mainPanel);

        // sets up what happens when the frame is closed
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set the dimensions of the frame
        myFrame.setPreferredSize(new Dimension(500, 450));

        // put everything in the frame and make it visible
        myFrame.pack();
        myFrame.setVisible(true);
    }
}
