package common.commands;

public class RemoveByIdCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final long id;

    public RemoveByIdCommand(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }
}