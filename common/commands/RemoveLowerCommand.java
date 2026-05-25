package common.commands;

import common.models.City;

public class RemoveLowerCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final City city;

    public RemoveLowerCommand(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    @Override
    public String getName() {
        return "remove_lower";
    }
}
