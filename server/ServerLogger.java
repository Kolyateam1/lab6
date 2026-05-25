package server;

import java.util.logging.*;

public class ServerLogger {
    private static final Logger logger = Logger.getLogger("Server");

    static {
        try {
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Не удалось создать лог-файл: " + e.getMessage());
        }
    }

    public static void info(String message) {
        logger.info(message);
        System.out.println("[INFO] " + message);
    }

    public static void warning(String message) {
        logger.warning(message);
        System.out.println("[WARN] " + message);
    }

    public static void severe(String message) {
        logger.severe(message);
        System.err.println("[ERROR] " + message);
    }
}