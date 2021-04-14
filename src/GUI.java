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

    // create hashmaps to store heating, lighting and upgrade costs data
    private static HashMap<String, Integer> heating;
    private static HashMap<Integer, Integer> lightingTypes;
    private static HashMap<Integer, Integer> lightingHours;
    private static HashMap<String, Double> upgradeCosts;

    private static int lightingSlidePosition;

    // create variables that store information/dimensions user enters about their house
    private static int extLength;
    private static int extWidth;
    private static int floorHeight;
    private static int floorCount;
    private static double atticArea;

    // create variables that store count and size of windows user provides
    private static int windowSqin;
    private static int windowSqft;
    private static int windowCount;

    // given information about calculations and Uncle Roger's house
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

    // arrayList to hold strings of upgrades entered
    private static ArrayList<String> upgrades;

    /**
     * calculates the cost of lighting in Uncle Roger's house
     * @return total cost of lighting without optimization
     */
    public static double baseLightingCost() {
        int totalWattHours = 0;
        int currentKey;
        // for all lighting types in house,
        // sum multiplication of currentKey, current lightingType, and current lightingHours used
        for(int i = 0; i < lightingTypes.size(); i++){
            currentKey = (int) lightingTypes.keySet().toArray()[i];
            totalWattHours += (currentKey * lightingTypes.get(currentKey) * lightingHours.get(currentKey));
        }

        // convert Watts to KW
        double totalKWHours = (totalWattHours / 1000.0);
        // return total yearly cost, the multiplication of KWH, cost per KWH and 365 days
        return totalKWHours * COST_PER_KILOWATT_HOUR * DAYS_IN_USE;
    }

    /**
     * calculates optimized lighting cost in Uncle Roger's house given data
     * @return optimized lighting cost
     */
    public static double optimizedLightingCost() {
        // initialize variables
        double costs = 0.0;
        boolean read = false;
        int totalWattHours = 0;

        // grab file and create Scanner object
        File upgradesData = new File("src/upgrades_file.txt");
        Scanner upgradesScanner;

        try {
            // initialize Scanner obj
            upgradesScanner = new Scanner(upgradesData);
            while(upgradesScanner.hasNext()){
                String inputStr = upgradesScanner.nextLine();

                // if line is blank, set read to false and move on
                if(inputStr.isBlank()) { read = false; }

                // if line isn't blank,
                if(read) {
                    // split data on comma, sum total Watt hours,
                    // sum cost and add type of upgrade to upgrades arrayList
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
            // show error if file not found and exit program
            JOptionPane.showMessageDialog(null, "Error loading upgrades_file.txt, exiting program.");
            System.exit(-1);
        }

        // add lighting upgrades cost to hashmap
        upgradeCosts.put("Lighting Upgrade Costs", costs);

        // convert WH to KWH
        double totalKWHours = (totalWattHours / 1000.0);
        // return total yearly cost, the multiplication of KWH, cost per KWH and 365 days
        return totalKWHours * COST_PER_KILOWATT_HOUR * DAYS_IN_USE;
    }

    /**
     * calculate cost of heating for Uncle Roger
     * @return return unoptimized cost of heating
     * Uncle Roger's house
     */
    public static double baseHeatingCost() {
        // variable for area of attic
        atticArea = Math.sqrt(Math.pow((0.5 * extWidth), 2.0) + (ATTIC_HEIGHT * ATTIC_HEIGHT)) * extLength * 2.0;

        // initialize variables
        double totalWattHours = 0;
        int totalPower = 0;
        double BTUPerHr = 0.0;
        double heatTransferRateAprSep = 0.0;
        double heatTransferRateOctMar = 0.0;
        double heatLossPerYear = 0.0;
        double hoursHeatersRun = 0.0;
        double extCrossSectionalArea = (floorCount * floorHeight * extLength) + (floorCount * floorHeight * extWidth) - windowSqft;
        String currentKey;

        // for all heaters, sum total power
        for(int i = 0; i < heating.size(); i++){
            currentKey = heating.keySet().toArray()[i].toString();
            totalPower += heating.get(currentKey);
        }

        // calculations for heat transfer rate during April-September
        heatTransferRateAprSep += extCrossSectionalArea * (T_OUT_T_IN_APR_SEP / WALLS_R_VALUE);
        heatTransferRateAprSep += windowSqft * (T_OUT_T_IN_APR_SEP / WINDOW_R_VALUE);
        heatTransferRateAprSep += atticArea * (T_OUT_T_IN_APR_SEP / ATTIC_R_VALUE);
        heatTransferRateAprSep *= (DAYS_IN_USE / 2) * 24;

        // calculations for heat transfer rate during October-March
        heatTransferRateOctMar += extCrossSectionalArea * (T_OUT_T_IN_OCT_MAR / WALLS_R_VALUE);
        heatTransferRateOctMar += windowSqft * (T_OUT_T_IN_OCT_MAR / WINDOW_R_VALUE);
        heatTransferRateOctMar += atticArea * (T_OUT_T_IN_OCT_MAR / ATTIC_R_VALUE);
        heatTransferRateOctMar *= (DAYS_IN_USE / 2) * 24;

        // add two bi-yearly amounts together
        heatLossPerYear = heatTransferRateAprSep + heatTransferRateOctMar;

        // calculate BTU per hour
        BTUPerHr = totalPower * WATT_TO_BTU_PER_HR;

        // calculate how long heaters run in hours
        hoursHeatersRun = (heatLossPerYear / BTUPerHr);

        // calculate total Watt Hours
        totalWattHours = hoursHeatersRun * totalPower;

        // return cost of running heaters
        return (totalWattHours / 1000) * COST_PER_KILOWATT_HOUR;
    }

    /**
     * calculate optimized cost of heating for Uncle Roger
     * @return return optimized cost of heating
     * Uncle Roger's house
     */
    public static double optimizedHeatingCost() {
        // initialize variables
        double costs = 0.0;
        boolean read = false;
        double optimizedWindowRValue = WINDOW_R_VALUE;
        double optimizedWallRValue = WALLS_R_VALUE;
        double optimizedAtticRValue = ATTIC_R_VALUE;

        // get file and create Scanner object
        File upgradesData = new File("src/upgrades_file.txt");
        Scanner upgradesScanner;

        try {
            // initalize Scanner object
            upgradesScanner = new Scanner(upgradesData);
            while(upgradesScanner.hasNext()){
                // store user input while !EOF
                String inputStr = upgradesScanner.nextLine();

                // if line is blank, set read to false and move on
                if(inputStr.isBlank()) { read = false; }

                // if line isn't blank,
                if(read) {
                    // split user input on comma,
                    String[] dataSplit = inputStr.split(", ");
                    // sum cost and the optimized R value given respective input
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
                    // add first split to upgrades arrayList
                    upgrades.add(dataSplit[0]);
                }

                if(inputStr.equals("Potential Insulation Upgrades:")) {
                    read = true;
                    upgradesScanner.nextLine();
                }
            }
            // show error if file not found and exit program
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading upgrades_file.txt, exiting program.");
            System.exit(-1);
        }

        // add costs to hashmap with type of upgrade
        upgradeCosts.put("Heating Upgrade Costs", costs);

        // iniitalize variables needed for calculation
        double totalWattHours = 0;
        int totalPower = 0;
        double BTUPerHr = 0.0;
        double heatTransferRateAprSep = 0.0;
        double heatTransferRateOctMar = 0.0;
        double heatLossPerYear = 0.0;
        double hoursHeatersRun = 0.0;
        double extCrossSectionalArea = (floorCount * floorHeight * extLength) + (floorCount * floorHeight * extWidth) - windowSqft;
        String currentKey;

        // for all heaters, sum total power
        for(int i = 0; i < heating.size(); i++){
            currentKey = heating.keySet().toArray()[i].toString();
            totalPower += heating.get(currentKey);
        }

        // calculations for heat transfer rate during April-September
        heatTransferRateAprSep += extCrossSectionalArea * (T_OUT_T_IN_APR_SEP / optimizedWallRValue);
        heatTransferRateAprSep += windowSqft * (T_OUT_T_IN_APR_SEP / optimizedWindowRValue);
        heatTransferRateAprSep += atticArea * (T_OUT_T_IN_APR_SEP / optimizedAtticRValue);
        heatTransferRateAprSep *= (DAYS_IN_USE / 2) * 24;

        // calculations for heat transfer rate during October-March
        heatTransferRateOctMar += extCrossSectionalArea * (T_OUT_T_IN_OCT_MAR / optimizedWallRValue);
        heatTransferRateOctMar += windowSqft * (T_OUT_T_IN_OCT_MAR / optimizedWindowRValue);
        heatTransferRateOctMar += atticArea * (T_OUT_T_IN_OCT_MAR / optimizedAtticRValue);
        heatTransferRateOctMar *= (DAYS_IN_USE / 2) * 24;

        // add two bi-yearly amounts together
        heatLossPerYear = heatTransferRateAprSep + heatTransferRateOctMar;

        // calculate BTU per hour
        BTUPerHr = totalPower * WATT_TO_BTU_PER_HR;

        // calculate how long heaters run in hours
        hoursHeatersRun = (heatLossPerYear / BTUPerHr);

        // calculate total Watt Hours
        totalWattHours = hoursHeatersRun * totalPower;

        // return cost of running heaters
        return (totalWattHours / 1000) * COST_PER_KILOWATT_HOUR;
    }

    /**
     * calculates costs of miscellaneous upgrades
     * @return miscellaneous upgrade costs
     */
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

                // if line isn't blank,
                if(read) {
                    // split input on comma, sum costs, add type of upgrade to
                    // upgrades arrayList
                    String[] dataSplit = inputStr.split(", ");
                    costs += Double.parseDouble(dataSplit[1]);
                    upgrades.add(dataSplit[0]);
                }

                if(inputStr.equals("Miscellaneous Updates:")) {
                    read = true;
                    upgradesScanner.nextLine();
                }
            }
            // show error if file not found and exit program
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading upgrades_file.txt, exiting program.");
            System.exit(-1);
        }

        // add type of upgrade and respective costs to hashmap
        upgradeCosts.put("Miscellaneous Upgrade Costs", costs);

        // return cost of miscellaneous costs
        return costs;
    }

    // START OF GUI
    public GUI() {
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // hide first screen, show screen2
                screen1.setVisible(false);
                screen2.setVisible(true);

                // disable next button to move to screen3
                nextTo3Button.setEnabled(false);

                // prompt user about how often they use a given light bulb
                int currentKey = (int) lightingTypes.keySet().toArray()[lightingSlidePosition];
                bulbInfo.setText("Out of 24 hours, how often do you use the " + currentKey + "W bulbs (on average)? You have "
                        + lightingTypes.get(currentKey) + " in your house.");
            }
        });
        nextTo3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // hide current screen and show screen3
                screen2.setVisible(false);
                screen3.setVisible(true);
            }
        });
        nextTo4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get floor information from user
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
                    // show error if data is not in numeric form
                    screen3MessageBox.setText("Please make sure all values are in numeric form (ex. 18)");
                    return;
                }

                // hide current screen and show screen4
                screen3.setVisible(false);
                screen4.setVisible(true);
            }
        });
        startHomeEnergyCalculatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // show list of rooms
                StringBuilder roomstr = new StringBuilder();

                for (String str : heating.keySet()) {
                    roomstr.append(str);
                    roomstr.append('\n');
                }

                // display rooms to user
                rooms.setText(roomstr.toString());
                screen0.setVisible(false);
                screen1.setVisible(true);
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // for the size of lightingTypes
                if (lightingSlidePosition < lightingTypes.keySet().size()) {

                    String hourValueStr = lightingHourTextField.getText();
                    int hourValue = -1;

                    try {
                        float temp = Float.parseFloat(hourValueStr);
                        hourValue = Math.round(temp);

                        // check if user input is 0-24
                        if (hourValue < 0 || hourValue > 24) {
                            String currentInfo = bulbInfo.getText();
                            String[] currentInfoSplit = currentInfo.split("\n");
                            String newText = currentInfoSplit[0];
                            newText += "\n\n Please ensure the data is between 0 and 24 hours (ex. 18)";
                            bulbInfo.setText(newText);
                            return;
                        }
                    } catch (Exception ex1) {
                        // ask user for input
                        String currentInfo = bulbInfo.getText();
                        String[] currentInfoSplit = currentInfo.split("\n");
                        String newText = currentInfoSplit[0];
                        newText += "\n\n Please enter a number for the hours (ex. 18)";
                        bulbInfo.setText(newText);
                        return;
                    }

                    // add to hashmap, reset text and restart
                    lightingHours.put((int) lightingTypes.keySet().toArray()[lightingSlidePosition], hourValue);
                    lightingHourTextField.setText("");
                    lightingSlidePosition += 1;
                    // if length of types hasn't been reached,
                    if (lightingSlidePosition < lightingTypes.keySet().size()) {
                        // ask user again
                        int currentKey = (int) lightingTypes.keySet().toArray()[lightingSlidePosition];
                        bulbInfo.setText("Out of 24 hours, how often do you use the " + currentKey + "W bulbs (on average)? You have "
                                + lightingTypes.get(currentKey) + " in your house.");
                    } else {
                        // otherwise, move on and prompt user to move on to the nest page
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
                // set sq. ft of window and move to screen4
                windowSqft = (windowSqin / 144);
                screen4.setVisible(false);
                screen5.setVisible(true);
            }
        });
        calculateCosts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // calculate costs and show Uncle Roger

                // show original costs
                double totalEnergyBill = baseLightingCost() + baseHeatingCost() + miscellaneousCosts();
                lightingCostResult.setText(String.format("$%.2f per year", baseLightingCost()));
                heatingCostResult.setText(String.format("$%.2f per year", baseHeatingCost()));
                miscellaneousCostResult.setText(String.format("$%.2f per year", miscellaneousCosts()));
                totalCostResult.setText(String.format("$%.2f per year", totalEnergyBill));

                // show optimized costs
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
        addAnotherWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // initialize/set variables
                    float countTemp = Float.parseFloat(windowCountField.getText());
                    int count = Math.round(countTemp);
                    windowCount += count;

                    float heightTemp = Float.parseFloat(windowHeightField.getText());
                    int heightInInches = Math.round(heightTemp);
                    float widthTemp = Float.parseFloat(windowWidthField.getText());
                    int widthInInches = Math.round(widthTemp);

                    windowSqin += (count * heightInInches * widthInInches);

                    screen4MessageBox.setText("Current Window Count:" + windowCount + "\nCurrent Square Inches: " + windowSqin);
                } catch (Exception ex1) {
                    // check that all fields are valid
                    screen4MessageBox.setText("Please make sure all values are in numeric form (ex. 18)");
                    return;
                }

                windowWidthField.setText("");
                windowHeightField.setText("");
                windowCountField.setText("");
            }
        });
    }

    public static void main(String[] args) {
        // initialze hashmaps
        heating = new HashMap<String, Integer>();
        lightingTypes = new HashMap<Integer, Integer>();
        lightingHours = new HashMap<Integer, Integer>();
        upgradeCosts = new HashMap<String, Double>();
        upgrades = new ArrayList<String>();

        // initialize variables
        lightingSlidePosition = 0;

        extLength = -1;
        extWidth = -1;
        floorHeight = -1;
        floorCount = -1;

        windowCount = 0;
        windowSqin = 0;

        // get sensor data file and create Scanner object
        File sensorData = new File("src/dataFromSensors.txt");
        Scanner dataScanner;

        try {
            // initialize Scanner object
            dataScanner = new Scanner(sensorData);
        } catch (FileNotFoundException e) {
            // show error if file not found and exit program
            JOptionPane.showMessageDialog(null, "Error loading dataFromSensors.txt, exiting program.");
            return;
        }

        boolean header = true;
        boolean readingLighting = false;
        while (dataScanner.hasNextLine()) {
            String data = dataScanner.nextLine();
            // check for first line that describes the format
            // the text
            if (header) {
                header = false;
            } else {
                // if line is blank,
                if (data.isBlank()) {
                    // set reading to false and header to true
                    readingLighting = !readingLighting;
                    header = true;
                } else {
                    // split data on comma
                    String[] dataSplit = data.split(", ");
                    if (!readingLighting) {
                        // if the data isn't lighting data, add to heating hashmap
                        heating.put(dataSplit[0], Integer.parseInt(dataSplit[1]));
                    } else {
                        // otherwise add to lighting hashmap
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
