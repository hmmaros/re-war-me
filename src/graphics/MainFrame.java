package graphics;

import controller.Functions;
import finals.Finals;
import finals.Texts;
import logger.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFrame {

    private static Logger logger = MyLogger.myLogger();

    public static final JFrame rewarmeFrame = new JFrame(Finals.APPNAME);
    private JPanel stopStartButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private JCheckBox checkBox = new JCheckBox("Do not restart Tomcat for every .war update");
    private static JButton stopButton = new JButton("Stop");
    private static JButton startButton = new JButton("Start");
    private boolean notRestartChecked = false;
    private int countAppStarts = 0;


    public MainFrame() {
        JLabel copyright = new JLabel("\u00a9 " + Finals.CREATOR + " " + Finals.EDITION);
        rewarmeFrame.add(copyright, BorderLayout.SOUTH);
        rewarmeFrame.setPreferredSize(new Dimension(Finals.MAIN_FRAME_WIDTH, Finals.MAIN_FRAME_HEIGHT));
        rewarmeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rewarmeFrame.pack();
        rewarmeFrame.setLocationRelativeTo(null);

        createFindTomcatDirectoryFunction();
        createFindProjectTargetDirectoryFunction();
        createCheckBox();
        createStopButton();
        createStartButton();

        rewarmeFrame.setVisible(true);

        rewarmeFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/resources/LogoReWarMe.png"));

        logger.log(Level.INFO, "Main frame created");

        rewarmeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        rewarmeFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                showExitConfirmDialog("The app will close. Are you sure?", "Really Closing?", true);
            }
        });
    }

    private void createFindTomcatDirectoryFunction(){

        new FileChooser("Tomcat root directory", rewarmeFrame, Finals.DIRECTORIES_ONLY, Finals.TOMCAT_IDENTIFIER);
        logger.log(Level.INFO, Finals.TOMCAT_IDENTIFIER + " path selected");
    }

    private void createFindProjectTargetDirectoryFunction(){

        new FileChooser("Directory of the file (e.g. /project/target)", rewarmeFrame,Finals.DIRECTORIES_ONLY, Finals.PROJECT_IDENTIFIER);
        logger.log(Level.INFO, Finals.PROJECT_IDENTIFIER + " path selected");
    }

    private void createCheckBox(){

        secondPanel.add(checkBox);

        checkBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()){
                    notRestartChecked = true;
                }
                else{
                    notRestartChecked = false;
                }

            }
        });


        rewarmeFrame.getContentPane().add(BorderLayout.NORTH, secondPanel );
        logger.log(Level.INFO, "checkbox created and added to rewarmeFrame");
    }


    private void createStartButton() {

        stopStartButtonsPanel.add(Box.createHorizontalStrut(Finals.DISTANCE_BETWEEN_BOTTOM_BUTTONS_MAIN_FRAME));

        // Add button to JPanel
        stopStartButtonsPanel.add(BorderLayout.EAST, startButton);

        // And JPanel needs to be added to the JFrame itself!
        rewarmeFrame.getContentPane().add(BorderLayout.SOUTH, stopStartButtonsPanel);

        startButton.setEnabled(true);

        startButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    try {
                        countAppStarts++;
                        if (countAppStarts == 1) {
                            if (FileChooser.checkBoxPort.isSelected()) {
                                Functions.handleDirectories(rewarmeFrame, secondPanel, notRestartChecked);
                                if (!Functions.noWarFile) {
                                    startButton.setEnabled(false);
                                    stopButton.setEnabled(true);
                                }
                                else{
                                    countAppStarts = 0;
                                    startButton.setEnabled(true);
                                    stopButton.setEnabled(false);
                                    Functions.stopRunning();
                                }

                            } // TODO otan den exei epilextei kanena pedio erxete auto to minima proto lanthasmena...prepei to fill in all fields
                            else {
                                countAppStarts = 0;
                                logger.log(Level.INFO, "port checkbox not selected");
                                JOptionPane.showMessageDialog(rewarmeFrame, "Please select the box if you are sure that this is the right port!", "Accept the port", JOptionPane.INFORMATION_MESSAGE);
                            }

                        } else {
                            logger.log(Level.INFO, "The app is running. You clicked start button for more than one time");
                            JOptionPane.showMessageDialog(rewarmeFrame, "The app is running. If you want to rerun the app with different parameters please stop it first!", "App is running", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (NullPointerException err) {
                        countAppStarts = 0;
                        logger.log(Level.WARNING, "ERROR: One or more fields are not completed " + err);
                        JOptionPane.showMessageDialog(rewarmeFrame, "Please fill in all fields!", Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);

                    }

            }
        });
        logger.log(Level.INFO, "start button created");

    }

    private void createStopButton() {

        // Add button to JPanel

        stopStartButtonsPanel.add(stopButton);

        // And JPanel needs to be added to the JFrame itself!
        rewarmeFrame.getContentPane().add(BorderLayout.SOUTH, stopStartButtonsPanel);
        stopButton.setEnabled(false);

        stopButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showExitConfirmDialog(Texts.EXIT_CORFIRMATION, "Really stop?", false);
                Functions.stopRunning();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                countAppStarts = 0;
            }
        });
        logger.log(Level.INFO, "stop button created");
    }

    private void showExitConfirmDialog(String message, String title, boolean exit) {
        if (JOptionPane.showConfirmDialog(rewarmeFrame,
                message, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){

            if (exit){
                System.exit(0);
            }
        }
    }




}
