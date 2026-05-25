package common.commands;

import common.models.City;

public class AddCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final City city;

    public AddCommand(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    @Override
    public String getName() {
        return "add";
    }
}