package common.commands;

import java.io.Serializable;

/**
 * Базовый интерфейс для всех команд (Command Pattern).
 */
public interface Command extends Serializable {

    String getName();
    
}
