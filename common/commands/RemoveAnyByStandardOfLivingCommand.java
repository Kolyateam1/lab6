package common.commands;

import common.models.StandardOfLiving;

public class RemoveAnyByStandardOfLivingCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final StandardOfLiving standardOfLiving;

    public RemoveAnyByStandardOfLivingCommand(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }

    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }

    @Override
    public String getName() {
        return "remove_any_by_standard_of_living";
    }
}
