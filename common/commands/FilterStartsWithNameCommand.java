package common.commands;

public class FilterStartsWithNameCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final String prefix;

    public FilterStartsWithNameCommand(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getName() {
        return "filter_starts_with_name";
    }
}