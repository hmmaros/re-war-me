package graphics;

import controller.Functions;
import finals.Finals;
import finals.Texts;
import logger.MyLogger;
import model.PathsModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileChooser extends JFrame {

    public final static JCheckBox checkBoxPort = new JCheckBox(Texts.ACCEPT_PORT);
    public final static JTextField portText = new JTextField();
    public final static JPanel fileChooserPanel = new JPanel();

    private static Logger logger = MyLogger.myLogger();

    private JFrame mainframe;
    private String targetToFind;
    private String directoryOrFileToSelect;
    private String directoryIdentifier;
    private JTextField textDirectory;

    private static JTextField txtPath = new JTextField();
    private static JFileChooser browseFiles = new JFileChooser();
    private static JPanel portPanel =  new JPanel();
    private static JLabel portLabel = new JLabel(Texts.TOMCAT_PORT);


    public FileChooser(String  targetToFind, JFrame mainframe, String directoryOrFileToSelect, String directoryIdentifier) {

        this.mainframe = mainframe;
        this.targetToFind = targetToFind;
        this.directoryOrFileToSelect = directoryOrFileToSelect;
        this.directoryIdentifier = directoryIdentifier;

        createTextField();
        createButtons();

    }

    private void createButtons() {
        JButton findFunctionButton = new JButton();
        findFunctionButton.setPreferredSize(new Dimension(Finals.FILE_CHOOSER_BUTTON_WIDTH, Finals.FILE_CHOOSER_BUTTON_HEIGHT));
        findFunctionButton.setText(Texts.BROWSE_BUTTON);

        fileChooserPanel.add( findFunctionButton );
        mainframe.getContentPane().add( fileChooserPanel );

        logger.log(Level.INFO, "Browse button created");

        findFunctionButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chooseFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                logger.log(Level.INFO, "Browse button action ended");
            }

        });
    }

    private void createTextField() {
        JTextField selectedFileDirectory = new JTextField(targetToFind);
        selectedFileDirectory.setColumns(Finals.FILE_CHOOSER_COLUMS_OF_TEXT_FIELD);
        fileChooserPanel.add(selectedFileDirectory);
        this.textDirectory = selectedFileDirectory;
        logger.log(Level.INFO, "Text field created");

    }

    private void chooseFile() throws IOException {

        if ( directoryOrFileToSelect.equals(Finals.DIRECTORIES_ONLY) )
        {
            // For Directory
            browseFiles.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        else if ( directoryOrFileToSelect.equals(Finals.FILES_ONLY) )
        {
            // For File
            browseFiles.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }


        browseFiles.setAcceptAllFileFilterUsed(false);

        int rVal = browseFiles.showOpenDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION)
        {
            String selectedFile =  browseFiles.getSelectedFile().toString();

            switch (directoryIdentifier) {
                case Finals.TOMCAT_IDENTIFIER:

                    int tomcatProofCounter = Functions.checkIfTomcatfolder(selectedFile);
                    if (tomcatProofCounter != 2) {
                        logger.log(Level.WARNING, "ERROR: This is not a tomcat directory ");
                        JOptionPane.showMessageDialog(mainframe, Texts.NOT_TOMCAT, Texts.WARNING_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        logger.log(Level.INFO, "TomcatFolder exists");
                        PathsModel.setTomcatPath(selectedFile);
                        portPanel(selectedFile);
                        setPathsToPanels(selectedFile);
                        portText.setBackground(Color.red);
                        checkBoxPort.setSelected(false);
                    }

                    break;
                case Finals.PROJECT_IDENTIFIER:
                    PathsModel.setProjectPath(selectedFile);
                    setPathsToPanels(selectedFile);
                    break;
                default:
                    logger.log(Level.WARNING, "No such directory identifier {0}", selectedFile);
                    break;
            }

        }

    }

    private void setPathsToPanels(String selectedFile) {
        txtPath.setText(selectedFile);
        textDirectory.setText(selectedFile);
        logger.log(Level.INFO,  "Selected files path: {0}", selectedFile);
    }

    private void portPanel(String path) throws IOException {
        Integer port = null;
        portPanel.add(portLabel);
        File serverXml = new File(path + Finals.SERVER_XML);
        if (serverXml != null){
            port = Functions.getTomcatPortFromConfigXml(serverXml);
        }

        String portString = (port != null) ? port.toString() : Finals.PORT_NOT_FOUND;

        setPortTextProperties(portString);

        portPanel.add(portText);
        portPanel.setPreferredSize(new Dimension(Finals.PORT_PANEL_WIDTH, Finals.PORT_PANEL_HEIGHT));

        portPanel.add(checkBoxPort);

        logger.log(Level.INFO, "checkbox for port created");

        fileChooserPanel.add(portPanel);
        SwingUtilities.updateComponentTreeUI(mainframe);
    }

    private void setPortTextProperties(String portString) {
        portText.setText(portString);
        portText.setPreferredSize(new Dimension(Finals.PORT_TEXT_FIELD_WIDTH, Finals.PORT_TEXT_FIELD_HEIGHT) );

        portText.setFont(new Font("default", Font.BOLD, 14));
        portText.setBackground(Color.red);
        portText.setForeground(Color.black);
        checkBoxPort.addActionListener(e -> {
            if (checkBoxPort.isSelected()){
                portText.setBackground(Color.blue);
                portText.setForeground(Color.gray);
            }
            else{
                portText.setBackground(Color.red);
                portText.setForeground(Color.black);
            }

        });
    }

}
