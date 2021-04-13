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
    private JButton nextTo4Button;
    private JButton addRoomButton;
    private JTextArea rooms;
    private JPanel screen0;
    private JButton startHomeEnergyCalculatorButton;
    private JButton confirmButton;
    private JTextArea bulbInfo;
    private JButton resetButtonScreen1;
    private JTextField lightingHourTextField;
    private JTextField extWidthTextField;
    private JTextArea screen3MessageBox;
    private JTextField extLengthTextField;
    private JTextField floorHeightTextField;
    private JTextField floorCountTextField;
    private JPanel screen4;
    private JTextArea screen4MessageBox;
    private JTextField windowWidthField;
    private JTextField windowHeightField;
    private JTextField windowCountField;
    private JButton nextTo5Button;
    private JPanel screen5;
    private JLabel calculations;
    private JLabel lightingCost;
    private JLabel lightingCostResult;
    private JLabel heatingCost;
    private JLabel heatingCostResult;
    private JLabel miscellaneousCost;
    private JLabel miscellaneousCostResult;
    private JButton calculateCosts;
    private JLabel totalCost;
    private JLabel totalCostResult;
    private JButton addAnotherWindow;
    private JLabel optimizedLightingCost;
    private JLabel optimizedLightingCostResult;
    private JLabel optimizedHeatingCost;
    private JLabel optimizedHeatingCostResult;
    private JLabel optimizedMiscellaneousCost;
    private JLabel optimizedMiscellaneousCostResult;
    private JLabel totalOptimizedCost;
    private JLabel totalOptimizedCostResult;
    private JTextArea upgradeDescriptionsAndCosts;
    private JLabel completeUpgradeCost;
    private JLabel completeUpgradeCostResult;

    private static HashMap<String, Integer> heating;
    private static HashMap<Integer, Integer> lightingTypes;
    private static HashMap<Integer, Integer> lightingHours;
    private static HashMap<String, Double> upgradeCosts;

    private static int lightingSlidePosition;

    private static int extLength;
    private static int extWidth;
    private static int floorHeight;
    private static int floorCount;
    private static double atticArea;

    private static int windowSqin;
    private static int windowSqft;
    private static int windowCount;

    private static final double COST_PER_KILOWATT_HOUR = 0.1;
    private static final double WATT_TO_BTU_PER_HR = 3.41;
    private static final int DAYS_IN_USE = 365;
    private static final double AVG_TEMP_APR_SEP = 63.3;
    private static final double AVG_TEMP_OCT_MAR = 41.8;
    private static final double HOUSE_TEMP_APR_SEP = 75;
    private static final double HOUSE_TEMP_OCT_MAR = 68;
    private static final double T_OUT_T_IN_APR_SEP = Math.abs(AVG_TEMP_APR_SEP - HOUSE_TEMP_APR_SEP);
    private static final double T_OUT_T_IN_OCT_MAR = Math.abs(AVG_TEMP_OCT_MAR - HOUSE_TEMP_OCT_MAR);
    private static final double WINDOW_R_VALUE = 3.5;
    private static final double WALLS_R_VALUE = 13;
    private static final double ATTIC_R_VALUE = 30;
    private static final int ATTIC_HEIGHT = 7;

    private static ArrayList<String> upgrades;


    public static double baseLightingCost() {
        int totalWattHours = 0;
        int currentKey;
        for(int i = 0; i < lightingTypes.size(); i++){
            currentKey = (int) lightingTypes.keySet().toArray()[i];
            totalWattHours += (currentKey * lightingTypes.get(currentKey) * lightingHours.get(currentKey));
        }

        double totalKWHours = (totalWattHours / 1000.0);
        return totalKWHours * COST_PER_KILOWATT_HOUR * DAYS_IN_USE;
    }

    public static double optimizedLightingCost() {
        double costs = 0.0;
        boolean read = false;
        int totalWattHours = 0;

        File upgradesData = new File("src/upgrades_file.txt");
        Scanner upgradesScanner;

        try {
            upgradesScanner = new Scanner(upgradesData);
            while(upgradesScanner.hasNext()){
                String inputStr = upgradesScanner.nextLine();

                if(inputStr.isBlank()) { read = false; }

                if(read) {
                    String[] dataSplit = inputStr.split(", ");
                    totalWattHours += lightingHours.get(Integer.parseInt(dataSplit[0])) * lightingTypes.get(Integer.parseInt(dataSplit[0])) * Integer.parseInt(dataSplit[1]);
                    costs += Double.parseDouble(dataSplit[2]) * lightingTypes.get(Integer.parseInt(dataSplit[0]));
                    upgrades.add("Upgrade to all LED bulbs");
                }

                if(inputStr.equals("Potential Lightbulb Upgrades:")) {
                    read = true;
                    upgradesScanner.nextLine();
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading upgrades_file.txt, exiting program.");
            System.exit(-1);
        }

        upgradeCosts.put("Lighting Upgrade Costs", costs);


        double totalKWHours = (totalWattHours / 1000.0);
        return totalKWHours * COST_PER_KILOWATT_HOUR * DAYS_IN_USE;
    }

    public static double baseHeatingCost() {
        atticArea = Math.sqrt(Math.pow((0.5 * extWidth), 2.0) + (ATTIC_HEIGHT * ATTIC_HEIGHT)) * extLength * 2.0;

        double totalWattHours = 0;
        int totalPower = 0;
        double BTUPerHr = 0.0;
        double heatTransferRateAprSep = 0.0;
        double heatTransferRateOctMar = 0.0;
        double heatLossPerYear = 0.0;
        double hoursHeatersRun = 0.0;
        double extCrossSectionalArea = (floorCount * floorHeight * extLength) + (floorCount * floorHeight * extWidth) - windowSqft;
        String currentKey;

        for(int i = 0; i < heating.size(); i++){
            currentKey = heating.keySet().toArray()[i].toString();
            totalPower += heating.get(currentKey);
        }

        heatTransferRateAprSep += extCrossSectionalArea * (T_OUT_T_IN_APR_SEP / WALLS_R_VALUE);
        heatTransferRateAprSep += windowSqft * (T_OUT_T_IN_APR_SEP / WINDOW_R_VALUE);
        heatTransferRateAprSep += atticArea * (T_OUT_T_IN_APR_SEP / ATTIC_R_VALUE);
        heatTransferRateAprSep *= (DAYS_IN_USE / 2) * 24;

        heatTransferRateOctMar += extCrossSectionalArea * (T_OUT_T_IN_OCT_MAR / WALLS_R_VALUE);
        heatTransferRateOctMar += windowSqft * (T_OUT_T_IN_OCT_MAR / WINDOW_R_VALUE);
        heatTransferRateOctMar += atticArea * (T_OUT_T_IN_OCT_MAR / ATTIC_R_VALUE);
        heatTransferRateOctMar *= (DAYS_IN_USE / 2) * 24;

        heatLossPerYear = heatTransferRateAprSep + heatTransferRateOctMar;

        BTUPerHr = totalPower * WATT_TO_BTU_PER_HR;

        hoursHeatersRun = (heatLossPerYear / BTUPerHr);

        totalWattHours = hoursHeatersRun * totalPower;

        return (totalWattHours / 1000) * COST_PER_KILOWATT_HOUR;
    }

    public static double optimizedHeatingCost() {
        double costs = 0.0;
        boolean read = false;
        double optimizedWindowRValue = WINDOW_R_VALUE;
        double optimizedWallRValue = WALLS_R_VALUE;
        double optimizedAtticRValue = ATTIC_R_VALUE;

        File upgradesData = new File("src/upgrades_file.txt");
        Scanner upgradesScanner;

        try {
            upgradesScanner = new Scanner(upgradesData);
            while(upgradesScanner.hasNext()){
                String inputStr = upgradesScanner.nextLine();

                if(inputStr.isBlank()) { read = false; }

                if(read) {
                    String[] dataSplit = inputStr.split(", ");
                    if(inputStr.contains("per window")) {
                        costs += Double.parseDouble(dataSplit[2]) * windowCount;
                        optimizedWindowRValue += Double.parseDouble(dataSplit[1]) * windowCount;
                    } else if(inputStr.contains("window")) {
                        optimizedWindowRValue += Double.parseDouble(dataSplit[1]);
                        costs += Double.parseDouble(dataSplit[2]);
                    } else if(inputStr.contains("wall")) {
                        optimizedWallRValue += Double.parseDouble(dataSplit[1]);
                        costs += Double.parseDouble(dataSplit[2]);
                    } else if(inputStr.contains("attic")) {
                        optimizedAtticRValue += Double.parseDouble(dataSplit[1]);
                        costs += Double.parseDouble(dataSplit[2]);
                    }
                    upgrades.add(dataSplit[0]);
                }

                if(inputStr.equals("Potential Insulation Upgrades:")) {
                    read = true;
                    upgradesScanner.nextLine();
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading upgrades_file.txt, exiting program.");
            System.exit(-1);
        }

        upgradeCosts.put("Heating Upgrade Costs", costs);


        double totalWattHours = 0;
        int totalPower = 0;
        double BTUPerHr = 0.0;
        double heatTransferRateAprSep = 0.0;
        double heatTransferRateOctMar = 0.0;
        double heatLossPerYear = 0.0;
        double hoursHeatersRun = 0.0;
        double extCrossSectionalArea = (floorCount * floorHeight * extLength) + (floorCount * floorHeight * extWidth) - windowSqft;
        String currentKey;

        for(int i = 0; i < heating.size(); i++){
            currentKey = heating.keySet().toArray()[i].toString();
            totalPower += heating.get(currentKey);
        }

        heatTransferRateAprSep += extCrossSectionalArea * (T_OUT_T_IN_APR_SEP / optimizedWallRValue);
        heatTransferRateAprSep += windowSqft * (T_OUT_T_IN_APR_SEP / optimizedWindowRValue);
        heatTransferRateAprSep += atticArea * (T_OUT_T_IN_APR_SEP / optimizedAtticRValue);
        heatTransferRateAprSep *= (DAYS_IN_USE / 2) * 24;

        heatTransferRateOctMar += extCrossSectionalArea * (T_OUT_T_IN_OCT_MAR / optimizedWallRValue);
        heatTransferRateOctMar += windowSqft * (T_OUT_T_IN_OCT_MAR / optimizedWindowRValue);
        heatTransferRateOctMar += atticArea * (T_OUT_T_IN_OCT_MAR / optimizedAtticRValue);
        heatTransferRateOctMar *= (DAYS_IN_USE / 2) * 24;

        heatLossPerYear = heatTransferRateAprSep + heatTransferRateOctMar;

        BTUPerHr = totalPower * WATT_TO_BTU_PER_HR;

        hoursHeatersRun = (heatLossPerYear / BTUPerHr);

        totalWattHours = hoursHeatersRun * totalPower;

        return (totalWattHours / 1000) * COST_PER_KILOWATT_HOUR;
    }

    public static double miscellaneousCosts() {
        double costs = 0.0;
        boolean read = false;

        File upgradesData = new File("src/upgrades_file.txt");
        Scanner upgradesScanner;

        try {
            upgradesScanner = new Scanner(upgradesData);
            while(upgradesScanner.hasNext()){
                String inputStr = upgradesScanner.nextLine();

                if(inputStr.isBlank()) { read = false; }

                if(read) {
                    String[] dataSplit = inputStr.split(", ");
                    costs += Double.parseDouble(dataSplit[1]);
                    upgrades.add(dataSplit[0]);
                }

                if(inputStr.equals("Miscellaneous Updates:")) {
                    read = true;
                    upgradesScanner.nextLine();
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading upgrades_file.txt, exiting program.");
            System.exit(-1);
        }

        upgradeCosts.put("Miscellaneous Upgrade Costs", costs);
        return costs;
    }

    public GUI() {
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screen1.setVisible(false);
                screen2.setVisible(true);

                nextTo3Button.setEnabled(false);

                int currentKey = (int) lightingTypes.keySet().toArray()[lightingSlidePosition];
                bulbInfo.setText("Out of 24 hours, how often do you use the " + currentKey + "W bulbs (on average)? You have "
                        + lightingTypes.get(currentKey) + " in your house.");
            }
        });
        nextTo3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screen2.setVisible(false);
                screen3.setVisible(true);
            }
        });
        nextTo4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    float temp = Float.parseFloat(extLengthTextField.getText());
                    extLength = Math.round(temp);
                    temp = Float.parseFloat(extWidthTextField.getText());
                    extWidth = Math.round(temp);
                    temp = Float.parseFloat(floorCountTextField.getText());
                    floorCount = Math.round(temp);
                    temp = Float.parseFloat(floorHeightTextField.getText());
                    floorHeight = Math.round(temp);
                } catch (Exception ex1) {
                    screen3MessageBox.setText("Please make sure all values are in numeric form (ex. 18)");
                    return;
                }

                screen3.setVisible(false);
                screen4.setVisible(true);
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

                        if (hourValue < 0 || hourValue > 24) {
                            String currentInfo = bulbInfo.getText();
                            String[] currentInfoSplit = currentInfo.split("\n");
                            String newText = currentInfoSplit[0];
                            newText += "\n\n Please ensure the data is between 0 and 24 hours (ex. 18)";
                            bulbInfo.setText(newText);
                            return;
                        }
                    } catch (Exception ex1) {
                        String currentInfo = bulbInfo.getText();
                        String[] currentInfoSplit = currentInfo.split("\n");
                        String newText = currentInfoSplit[0];
                        newText += "\n\n Please enter a number for the hours (ex. 18)";
                        bulbInfo.setText(newText);
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
                        nextTo3Button.setEnabled(true);
                        lightingHourTextField.setVisible(false);
                        confirmButton.setVisible(false);
                    }
                }
            }
        });
        nextTo5Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    float countTemp = Float.parseFloat(windowCountField.getText());
                    int count = Math.round(countTemp);
                    windowCount += count;
                    float heightTemp = Float.parseFloat(windowHeightField.getText());
                    int heightInInches = Math.round(heightTemp);
                    float widthTemp = Float.parseFloat(windowWidthField.getText());
                    int widthInInches = Math.round(widthTemp);

                    windowSqin += (count * heightInInches * widthInInches);
                } catch (Exception ex1) {
                    screen4MessageBox.setText("Please make sure all values are in numeric form (ex. 18)");
                    return;
                }

                windowSqft = (windowSqin / 144);
                screen3.setVisible(false);
                screen4.setVisible(true);
            }
        });
        calculateCosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double totalEnergyBill = baseLightingCost() + baseHeatingCost() + miscellaneousCosts();
                lightingCostResult.setText(String.format("$%.2f per year", baseLightingCost()));
                heatingCostResult.setText(String.format("$%.2f per year", baseHeatingCost()));
                miscellaneousCostResult.setText(String.format("$%.2f per year", miscellaneousCosts()));
                totalCostResult.setText(String.format("$%.2f per year", totalEnergyBill));

                totalEnergyBill = optimizedLightingCost() + optimizedHeatingCost() + miscellaneousCosts();
                optimizedLightingCostResult.setText(String.format("$%.2f per year", optimizedLightingCost()));
                optimizedHeatingCostResult.setText(String.format("$%.2f per year", optimizedHeatingCost()));
                optimizedMiscellaneousCostResult.setText(String.format("$%.2f per year", miscellaneousCosts()));
                totalOptimizedCostResult.setText(String.format("$%.2f per year", totalEnergyBill));

                double totalUpgradeCost = upgradeCosts.get("Miscellaneous Upgrade Costs") +
                        upgradeCosts.get("Heating Upgrade Costs") + upgradeCosts.get("Lighting Upgrade Costs");
                completeUpgradeCostResult.setText(String.format("$%.2f", totalUpgradeCost));

                upgradeDescriptionsAndCosts.setText(upgradeCosts.toString());

                JOptionPane.showMessageDialog(null, "Recommended upgrades: " + upgrades);
            }
        });
    }

    public static void main(String[] args) {
        heating = new HashMap<String, Integer>();
        lightingTypes = new HashMap<Integer, Integer>();
        lightingHours = new HashMap<Integer, Integer>();
        upgradeCosts = new HashMap<String, Double>();
        upgrades = new ArrayList<String>();

        lightingSlidePosition = 0;

        extLength = -1;
        extWidth = -1;
        floorHeight = -1;
        floorCount = -1;

        windowCount = 0;
        windowSqin = 0;

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

        JFrame myFrame = new JFrame("Smart Home Energy Calculator");
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
