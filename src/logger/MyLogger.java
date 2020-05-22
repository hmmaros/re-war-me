package logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    public MyLogger(){

    }

    public static Logger logger;

    public static Logger myLogger(){
        logger = Logger.getLogger("My Logger");
        FileHandler fileHandler;

        try {
            fileHandler = new FileHandler("%T/ReWarMe.log", true);
            logger.addHandler(fileHandler);
            fileHandler.setLevel(Level.ALL);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }



}
