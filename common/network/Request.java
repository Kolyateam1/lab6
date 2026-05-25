package common.network;

import common.commands.Command;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Command command;

    public Request(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}