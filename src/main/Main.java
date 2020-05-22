package main;

import controller.ExecuteCommands;
import finals.Texts;
import graphics.MainFrame;
import logger.MyLogger;
import java.util.logging.*;

public class Main {

    public static void main(String[] args) {
        Logger logger = MyLogger.myLogger();
        logger.log(Level.INFO, Texts.APP_STARTED);
        new MainFrame();
    }

}
