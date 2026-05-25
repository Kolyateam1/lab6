package common.commands;

import common.models.City;

public class UpdateCommand implements Command {
    private static final long serialVersionUID = 1L;
    private final long id;
    private final City city;

    public UpdateCommand(long id, City city) {
        this.id = id;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    @Override
    public String getName() {
        return "update";
    }
}