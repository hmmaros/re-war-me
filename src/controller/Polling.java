package controller;

import finals.Finals;
import finals.Texts;
import logger.MyLogger;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static controller.Functions.tomcatPath;

public class Polling {



    private static Logger logger = MyLogger.myLogger();
    private static Integer tomcatPort;
    private static String projectPath;

    private static Map findedFileNames = new HashMap();
    private static int countStarts = 0;
    private static String currentTime = "";

    public static ScheduledExecutorService executorForWarPolling;
    public static ScheduledExecutorService executorToCheckIfShutedDown;
    public static ScheduledExecutorService executorToDeleteFile;
    public static ScheduledExecutorService executorToCopyFile;
    public static ScheduledExecutorService executorToStartTomcat;
    public static ScheduledExecutorService executorForDebuggingCheck;
    public static ScheduledExecutorService executorToCheckIfTomcatStarted;
    static public Runnable myRunnable;

    private static boolean tomcatIsRunning;
    protected static boolean notRestartChecked;
    private static JFrame rewarmeFrame = new JFrame();
    static BasicFileAttributes attr;

    static boolean pollingToJustDoItIsRunning = false;

    public static void handlePolling(Integer port, Map findedFiles, String pPath, boolean thenotRestartChecked, JFrame frame, int thecountStarts) {
        notRestartChecked = thenotRestartChecked;
        projectPath = pPath;
        findedFileNames = findedFiles;
        tomcatPort = port;
        rewarmeFrame = frame;
        countStarts = thecountStarts;

        pollAndcheckIfNewWarExists();
    }

    private static void pollAndcheckIfNewWarExists() {


        myRunnable = new Runnable() {
            public void run() {
                for (int i = 0; i < findedFileNames.size(); i++) {

                    Path file = Paths.get(projectPath + "\\" + findedFileNames.get(i));
                    logger.log(Level.INFO, "File founded {0}", file);
                    attr = null;
                    try {
                        attr = Files.readAttributes(file, BasicFileAttributes.class);

                    } catch (IOException e) {
                        logger.log(Level.WARNING, e.toString());
                    }

                    if (!pollingToJustDoItIsRunning) {
                        if (countStarts > 0) {
                            if (attr != null) {
                                if (currentTime.equals(attr.lastModifiedTime().toString()))
                                {
                                    logger.log(Level.INFO, "File not modified {0}", currentTime);
                                }
                                else
                                {
                                    logger.log(Level.INFO, "File modified {0}", currentTime);
                                    pollingToJustDoItIsRunning = true;
                                    justDoIt(Finals.STOP_TOMCAT_WITH_PID);

                                }
                            }
                        } else {
                            pollingToJustDoItIsRunning = true;
                            justDoIt(Finals.STOP_TOMCAT_WITH_PID);
                            countStarts++;
                        }
                    }

                    if (attr != null) {
                        currentTime = attr.lastModifiedTime().toString();
                    }
                }

            }
        };
        executorForWarPolling =Executors.newScheduledThreadPool(1);
        executorForWarPolling.scheduleAtFixedRate(myRunnable,0,5,TimeUnit.SECONDS);
        }


    private static void justDoIt(boolean stopTomcatWithPID){

        tomcatIsRunning = Functions.isServerUp(tomcatPort);

        if (notRestartChecked){
            pollToDeleteFile();
        }
        else{

            // stop tomcat if running
            if (tomcatIsRunning) {
                if (stopTomcatWithPID) {
                    ExecuteCommands.findPID(tomcatPort);
                } else {
                    ExecuteCommands.shutdownTomcat(tomcatPath);
                }
            }

            executorToCheckIfShutedDown = Executors.newSingleThreadScheduledExecutor();
            executorToCheckIfShutedDown.scheduleAtFixedRate(() -> {

                tomcatIsRunning = Functions.isServerUp(tomcatPort);
                if (!tomcatIsRunning) {
                    executorToCheckIfShutedDown.shutdownNow();
                    pollToDeleteFile();
                }
                logger.log(Level.WARNING, "Waiting to take down server");

            }, 0, 500, TimeUnit.MILLISECONDS);


        }


    }

    private static void pollToDeleteFile() {
        executorToDeleteFile = Executors.newSingleThreadScheduledExecutor();
        executorToDeleteFile.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                logger.log(Level.INFO, "Start polling for delete-copy-start");
                Functions.checkServerSideIfExistsAndDelete(findedFileNames, Finals.WARSUFFIX);
                boolean deletedFile = Functions.checkServerSideIfDeleted(findedFileNames, Finals.WARSUFFIX);
                if (deletedFile) {
                    executorToDeleteFile.shutdownNow();
                    pollToCopyFile();
                    if (notRestartChecked){
                        pollingToJustDoItIsRunning = false;
                    }
                }

            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

    }

    private static void pollToCopyFile() {

        executorToCopyFile = Executors.newSingleThreadScheduledExecutor();
        executorToCopyFile.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ExecuteCommands.copyWarFileToTomcat(findedFileNames, tomcatPath, projectPath);
                boolean deletedFileAfterCopy = Functions.checkServerSideIfDeleted(findedFileNames, Finals.WARSUFFIX);
                if (!deletedFileAfterCopy) {
                    executorToCopyFile.shutdownNow();
                    tomcatIsRunning = Functions.isServerUp(tomcatPort);
                    if (!notRestartChecked || (notRestartChecked &&  !tomcatIsRunning)) {
                        pollToStartTomcat();
                    }

                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private static void pollToStartTomcat() {

        executorToStartTomcat = Executors.newSingleThreadScheduledExecutor();
        executorToStartTomcat.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                executorToStartTomcat.shutdownNow();
                ExecuteCommands.startTomcat(tomcatPath);
                pollingToJustDoItIsRunning = false;
                logger.log(Level.INFO, "Finished polling to start tomcat");
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

    }

}
