package controller;


import finals.Finals;
import finals.Texts;
import graphics.FileChooser;
import logger.MyLogger;
import model.PathsModel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {

    private Functions() {

    }

    private static Logger logger = MyLogger.myLogger();

    private static JFrame rewarmeFrame = new JFrame();
    private static JPanel secondPanel = new JPanel();
    private static Map findedFileNames = new HashMap();
    private static JLabel running = new JLabel();

    static String tomcatPath;
    private static String projectPath;
    private static int counterWarFilesExists = 0;
    private static int tomcatProofCounter = 0;
    private static boolean notRestartChecked;
    private static int countStarts = 0;
    private static int countFilesFounded = 0;
    public static boolean noWarFile = true;

    public static void handleDirectories(JFrame mainframe, JPanel thesecondPanel, boolean thenotRestartChecked){

        rewarmeFrame = mainframe;
        secondPanel = thesecondPanel;
        notRestartChecked = thenotRestartChecked;
        logger.log(Level.INFO, "start handle directories");

        tomcatPath = PathsModel.getTomcatPath();
        projectPath = PathsModel.getProjectPath();

        findedFileNames = checkWarFileExists(projectPath, Finals.WARSUFFIX);

        rewarmeFrame.setTitle(Finals.APPNAME + " - " + findedFileNames.get(0).toString());

        if (findedFileNames.size() > 0)
        {
            countStarts = 0;

            Integer port = Integer.parseInt(FileChooser.portText.getText());
            Polling.handlePolling(port, findedFileNames, projectPath, notRestartChecked, rewarmeFrame, countStarts);

            running.setText(Texts.RUNNING);
            running.setForeground(Color.blue);
            FileChooser.fileChooserPanel.add(running);
            SwingUtilities.updateComponentTreeUI(rewarmeFrame);
        }
    }

    public static void stopRunning() {

        countStarts = 0;
        running.setText(Texts.STOPPED);
        running.setForeground(Color.red);
        FileChooser.fileChooserPanel.add(running);
        SwingUtilities.updateComponentTreeUI(rewarmeFrame);

        if(!Polling.executorForWarPolling.isShutdown())
        {
            Polling.executorForWarPolling.shutdownNow();
        }
        if(!Polling.executorToDeleteFile.isShutdown())
        {
            Polling.executorToDeleteFile.shutdownNow();
        }
        if(!Polling.executorToCopyFile.isShutdown())
        {
            Polling.executorToCopyFile.shutdownNow();
        }

        if (!Polling.notRestartChecked){
            if(!Polling.executorToStartTomcat.isShutdown())
            {
                Polling.executorToStartTomcat.shutdownNow();
            }
            if(!Polling.executorToCheckIfShutedDown.isShutdown())
            {
                Polling.executorToCheckIfShutedDown.shutdownNow();
            }
        }


        logger.log(Level.INFO, "app stopped");
    }

    public static Integer getTomcatPortFromConfigXml(File serverXml) throws IOException {

        Integer port = 8080; // default port - if function failed to find the port

        String wholeText = readServerXMLFile(serverXml);

        Matcher m = extractConnectorsFromXML(wholeText);

        while (m.find()) {
            String block = m.group(1);
            if (block.contains("port") && block.contains("protocol") && !block.toLowerCase().contains("AJP")){
                String block1 = block.split("protocol")[1];
                String block2 = block1.split("\"")[1];
                if (block2.toLowerCase().contains("http")){
                    String portBlock = block.split("port")[1];
                    String portBlock2 = portBlock.split("\"")[1];
                    port = Integer.valueOf(portBlock2);
                    break;
                }
            }

        }
        return port;

    }

    private static Matcher extractConnectorsFromXML(String wholeText) {
        String textWithoutComments = wholeText.replaceAll( "<!--[\\s\\S]*?-->", "");
        String textAfterService = textWithoutComments.split("<Service name=\"Catalina\">")[1];
        Pattern p = Pattern.compile(Pattern.quote("<Connector ") + "(.*?)" + Pattern.quote("/>"), Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
        return p.matcher(textAfterService);
    }

    private static String readServerXMLFile(File serverXml) throws IOException {
        String wholeText = "";
        BufferedReader br = new BufferedReader(new FileReader(serverXml));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            wholeText = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            br.close();
        }
        return wholeText;
    }

    protected static boolean isServerUp(int port) {
        boolean isUp = false;
        try {
            Socket socket = new Socket(Finals.LOCALHOST_IP, port);
            // Server is up
            isUp = true;
            socket.close();
            logger.log(Level.INFO, "Server is up");
        }
        catch (IOException e)
        {
            //logger.log(Level.WARNING, "Server is down {0}", e);
        }
        return isUp;
    }

    public static int checkIfTomcatfolder(String selectedFile){
        File dir = new File(selectedFile);
        tomcatProofCounter = 0;
        dir.listFiles((dir1, name) -> {

            if ( name.equals("bin") )
            {
                tomcatProofCounter++;
            }
            if ( name.equals("webapps") )
            {
                tomcatProofCounter++;
            }
            return dir1.exists();
        });
        return tomcatProofCounter;
    }

    private static Map checkWarFileExists(String folderPath, String suffix){

        Map foundedFileNames = new HashMap();
        counterWarFilesExists = 0;

        File dir = new File(folderPath);
        dir.listFiles((dir1, name) -> {

            if ( name.endsWith(suffix) )
            {
                foundedFileNames.put(counterWarFilesExists, name);
                counterWarFilesExists++;
                logger.log(Level.INFO, "Name with given suffix founded");
                noWarFile = false;
            }
            return name.endsWith(suffix);
        });

        if (counterWarFilesExists == 0)
        {
            logger.log(Level.INFO, "There is no WAR file in this directory");
            JOptionPane.showMessageDialog(rewarmeFrame, Texts.NO_WAR_FILE, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
            noWarFile = true;
        }
        return foundedFileNames;
    }

    private static Map isolateNames(Map foundedFileNames, String suffix) {

        logger.log(Level.INFO, "Isolate names started");
        Map isolatedNames = new HashMap();

        for (int i=0; i<foundedFileNames.size(); i++)
        {
            String clearName = foundedFileNames.get(i).toString().split(suffix)[0];
            isolatedNames.put(i, clearName);
            logger.log(Level.INFO, "Name isolated from suffix");
        }
        logger.log(Level.INFO, "Map with isolated named returned");
        return isolatedNames;
    }

    protected static void checkServerSideIfExistsAndDelete(Map fileNames,  String suffix) {

        Map isolatedNames = isolateNames(fileNames, suffix);

        File dir = new File(tomcatPath + "\\webapps");
        dir.listFiles((dir1, name) -> {

            for ( int i=0; i<fileNames.size(); i++ )
            {
                if ( name.equals(fileNames.get(i)) || name.equals(isolatedNames.get(i)) )
                {
                    logger.log(Level.INFO, "File founded in tomcat folder {0}", name);
                    try {
                        ExecuteCommands.deleteFile(dir1, name);
                        logger.log(Level.INFO, "File deleted {0}", name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    logger.log(Level.INFO, "Files not exists in tomcat folder {0}", name);
                }

            }
            return dir1.exists();
        });


    }

    protected static boolean checkServerSideIfDeleted(Map fileNames, String suffix) {

        Map isolatedNames = isolateNames(fileNames, suffix);
        countFilesFounded = 0;
        File dir = new File(tomcatPath + "\\webapps");
        dir.listFiles((dir1, name) -> {

            for ( int i=0; i<fileNames.size(); i++ )
            {
                if ( name.equals(fileNames.get(i)) || name.equals(isolatedNames.get(i)) )
                {
                    countFilesFounded++;
                }
            }
            return dir1.exists();
        });


        if (countFilesFounded == 0){
            logger.log(Level.INFO, "Tomcat folder empty - files deleted");
            return true;
        }
        else {
            logger.log(Level.INFO, "Files still not deleted from tomcat folder");
            return false;
        }



    }

}
