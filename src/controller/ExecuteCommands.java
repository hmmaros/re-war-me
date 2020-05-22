package controller;

import finals.Finals;
import finals.Texts;
import graphics.MainFrame;
import logger.MyLogger;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteCommands {

    private static Logger logger = MyLogger.myLogger();

    protected static void shutdownTomcat(String tomcatPath) throws RuntimeException {
        String stopTomcatCommand;
        String operatingSystem = System.getProperty(Finals.OS_NAME);
        String windowsStop = "shutdown.bat";
        String linuxMacStop = "shutdown.sh";

        String[] parts = operatingSystem.split(" ");
        operatingSystem = parts[0];

        if (Finals.LINUX.equals(operatingSystem) || Finals.MAC.equals(operatingSystem)) {
            // TODO test
            stopTomcatCommand =  tomcatPath + "/" + "bin" + "/" + linuxMacStop;
            try {
                Runtime.getRuntime().exec(Finals.SH + stopTomcatCommand);
                logger.log(Level.INFO, "Tomcat stopped");
            } catch (IOException e) {
                logger.log(Level.WARNING, "Tomcat cannot be stopped {0}", e);
            }
        }
        else if (Finals.WINDOWS.equals(operatingSystem)) {

            stopTomcatCommand =  tomcatPath + "\\" + "bin" + "\\" + windowsStop;
            try {
                Runtime.getRuntime().exec(Finals.CMD + stopTomcatCommand);
                logger.log(Level.INFO, "Windows Tomcat stopped with this command {0}", stopTomcatCommand);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Tomcat cannot be stopped {0}", e);
            }


        }
        else {
            JOptionPane.showMessageDialog(MainFrame.rewarmeFrame, Texts.UNSUPPORTED_OS, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
            Functions.stopRunning();
            throw new RuntimeException(Texts.UNSUPPORTED_OS);
        }
    }

    protected static void startTomcat(String tomcatPath){
        String startTomcatCommand;
        String operatingSystem = System.getProperty(Finals.OS_NAME);
        String windowsStart = "startup.bat";
        String linuxMacStart = "startup.sh";

        String[] parts = operatingSystem.split(" ");
        operatingSystem = parts[0];

        if (Finals.LINUX.equals(operatingSystem) || Finals.MAC.equals(operatingSystem)) {
            // TODO test
            startTomcatCommand =  tomcatPath + "/" + "bin" + "/" + linuxMacStart;
            try {
                Runtime.getRuntime().exec(Finals.SH + startTomcatCommand);
                logger.log(Level.INFO, "Tomcat started");
            } catch (IOException e) {
                logger.log(Level.WARNING, "Tomcat cannot be stopped {0}", e);
            }
        }
        else if (Finals.WINDOWS.equals(operatingSystem)) {

            startTomcatCommand =  tomcatPath + "\\" + "bin" + "\\" + windowsStart;
            try {
                Runtime.getRuntime().exec(Finals.CMD + startTomcatCommand);
                logger.log(Level.INFO, "Windows Tomcat started with this command {0}", startTomcatCommand);

            } catch (IOException e) {
                logger.log(Level.WARNING, "Tom cannot be started {0}", e);
            }


        }
        else {
            JOptionPane.showMessageDialog(MainFrame.rewarmeFrame, Texts.UNSUPPORTED_OS, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
            Functions.stopRunning();
            throw new RuntimeException(Texts.UNSUPPORTED_OS);
        }
    }

    protected static void copyWarFileToTomcat(Map findedFileNames, String tomcatPath, String projectPath) throws RuntimeException {

        String copyFileCommand;
        String operatingSystem = System.getProperty(Finals.OS_NAME);

        String[] parts = operatingSystem.split(" ");
        operatingSystem = parts[0];

        if (Finals.LINUX.equals(operatingSystem) || Finals.MAC.equals(operatingSystem)) {
            // TODO
            for (int i=0;i<findedFileNames.size();i++)
            {
                copyFileCommand = "cp " + projectPath + "/" + findedFileNames.get(i) + " " + "/" + tomcatPath + "/webapps";
                try {
                    Runtime.getRuntime().exec(Finals.SH + copyFileCommand);
                    logger.log(Level.INFO, "War file to tomcat copied");
                } catch (IOException e) {
                    logger.log(Level.WARNING, "File not copied {0}", e);
                }
            }
        }
        else if (Finals.WINDOWS.equals(operatingSystem)) {

            for (int i=0;i<findedFileNames.size();i++)
            {
                copyFileCommand = "copy " + "\"" + projectPath + "\\" + findedFileNames.get(i) + "\"" + " " + "\"" + tomcatPath + "\\webapps" + "\\";
                try {
                    Runtime.getRuntime().exec(Finals.CMD + copyFileCommand);
                    logger.log(Level.INFO, "War file to tomcat copied");
                } catch (IOException e) {
                    logger.log(Level.WARNING, "File not copied {0}", e);
                }
            }

        }
        else {
            JOptionPane.showMessageDialog(MainFrame.rewarmeFrame, Texts.UNSUPPORTED_OS, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
            Functions.stopRunning();
            throw new RuntimeException(Texts.UNSUPPORTED_OS);
        }

    }

    protected static void deleteFile(File pathDir, String fileName) throws IOException, RuntimeException {

        String deleteFileCommand;
        String operatingSystem = System.getProperty(Finals.OS_NAME);

        String[] parts = operatingSystem.split(" ");
        operatingSystem = parts[0];

        if (Finals.LINUX.equals(operatingSystem) || Finals.MAC.equals(operatingSystem)) {

            if (fileName.endsWith(Finals.WARSUFFIX))
            {
                deleteFileCommand = "rm -f " + "/" + pathDir + "/" + fileName;
            }
            else
            {
                deleteFileCommand = "rm -rf " + "/" + pathDir + "/" + fileName;
            }
            Runtime.getRuntime().exec(Finals.SH + deleteFileCommand);
        }
        else if (Finals.WINDOWS.equals(operatingSystem)) {
            if (fileName.endsWith(Finals.WARSUFFIX))
            {
                deleteFileCommand = "del /F /Q " + "\"" + pathDir + "\\" + fileName + "\"";
            }
            else
            {
                deleteFileCommand = "rmdir /s /q " + "\"" + pathDir + "\\" + fileName + "\"";
            }

            Runtime.getRuntime().exec(Finals.CMD + deleteFileCommand);
        }
        else {
            JOptionPane.showMessageDialog(MainFrame.rewarmeFrame, Texts.UNSUPPORTED_OS, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
            Functions.stopRunning();
            throw new RuntimeException(Texts.UNSUPPORTED_OS);
        }

    }

    protected static void findPID(Integer port){
        String findPIDCommand;
        String operatingSystem = System.getProperty(Finals.OS_NAME);

        String[] parts = operatingSystem.split(" ");
        operatingSystem = parts[0];

        if (Finals.LINUX.equals(operatingSystem) || Finals.MAC.equals(operatingSystem)) {
            // TODO test
        }
        else if (Finals.WINDOWS.equals(operatingSystem)) {

            findPIDCommand =  "netstat -ano | findstr :" + port;
            try {
                Process process = Runtime.getRuntime().exec(Finals.CMD + findPIDCommand);
                logger.log(Level.INFO, "PID founded with this command {0}", findPIDCommand);

                InputStream is = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line = null;
                line = reader.readLine();
                if (line != null) {
                    String pid = line.substring(line.lastIndexOf(" ")+1);
                    killPID(pid);
                }

            } catch (IOException e) {
                logger.log(Level.WARNING, "PID cannot be found {0}", e);
            }


        }
        else {
            JOptionPane.showMessageDialog(MainFrame.rewarmeFrame, Texts.UNSUPPORTED_OS, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
            Functions.stopRunning();
            throw new RuntimeException(Texts.UNSUPPORTED_OS);
        }
    }

    protected static void killPID(String pid){
        String killPIDCommand;
        String operatingSystem = System.getProperty(Finals.OS_NAME);

        String[] parts = operatingSystem.split(" ");
        operatingSystem = parts[0];

        if (Finals.LINUX.equals(operatingSystem) || Finals.MAC.equals(operatingSystem)) {
            // TODO test
        }
        else if (Finals.WINDOWS.equals(operatingSystem)) {

            killPIDCommand =  "taskkill /PID " + pid + " /F";
            try {
                Process process = Runtime.getRuntime().exec(Finals.CMD + killPIDCommand);
                logger.log(Level.INFO, "PID killed with this command {0}", killPIDCommand);

            } catch (IOException e) {
                logger.log(Level.WARNING, "PID cannot be killed {0}", e);
            }


        }
        else {
            JOptionPane.showMessageDialog(MainFrame.rewarmeFrame, Texts.UNSUPPORTED_OS, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
            Functions.stopRunning();
            throw new RuntimeException(Texts.UNSUPPORTED_OS);
        }
    }
}
