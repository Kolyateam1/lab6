package common.commands;

public class HelpCommand implements Command {
    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        return "help";
    }
}