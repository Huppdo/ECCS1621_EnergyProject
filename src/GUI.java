import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUI {
    private JPanel mainPanel;
    private JPanel screen1;
    private JPanel screen2;
    private JButton nextButton;
    private JButton nextTo3Button;
    private JPanel screen3;
    private JButton startAt1Button;
    private JTextField roomName;
    private JButton addRoomButton;
    private JTextArea rooms;
    private JButton resetButtonScreen1;

    private static ArrayList<String> roomList;

    /*
    - Page 1: Collect titles of rooms
    - Page 2: Collect exterior dimensions, height of one floor, # of floors
    - Page 3: Window information
    - Page 4: Heating information (# of heaters)
    - Page 5: Providing Sensor Data
    - Page 6: Confirm Lighting
    - Page 7: Calculate Home Report & Potential Savings
     */

    /*
    Room, Power Usage (W) from Baseboard
    Living Room, 2000
    Dining Room, 1500
    Kitchen, 2000
    Bedroom large, 2000
    Bedroom medium2, 1700
    Bedroom small, 1600
    Bathroom, 1000
     */

    public GUI() {
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screen1.setVisible(false);
                screen2.setVisible(true);
                screen3.setVisible(false);
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
        addRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roomList.add(roomName.getText());
                StringBuilder roomStr = new StringBuilder();

                for (String str : roomList) {
                    roomStr.append(str);
                    roomStr.append('\n');
                }

                rooms.setText(roomStr.toString());

                System.out.println(roomList);

                roomName.setText("");
            }
        });
        resetButtonScreen1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roomName.setText("");
                rooms.setText("");
                roomList.clear();
            }
        });
    }

    public static void main(String[] args) {
        roomList = new ArrayList<String>();

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
