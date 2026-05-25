package client;

import common.commands.*;
import common.models.City;
import common.models.StandardOfLiving;
import client.utils.InputValidator;
import client.utils.CityFactory;

public class CommandReader {
    private final InputValidator validator;
    private final CityFactory cityFactory;

    public CommandReader(InputValidator validator) {
        this.validator = validator;
        this.cityFactory = new CityFactory(validator);
    }

    public Command readCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String cmdName = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : "";

        switch (cmdName) {
            case "add":
                System.out.println("Создание нового города:");
                return new AddCommand(cityFactory.createCity());

            case "update":
                if (arg.isEmpty()) {
                    System.out.println("Укажите id");
                    return null;
                }
                try {
                    long id = Long.parseLong(arg);
                    System.out.println("Введите новые данные города:");
                    return new UpdateCommand(id, cityFactory.createCity());
                } catch (NumberFormatException e) {
                    System.out.println("id должен быть числом");
                    return null;
                }

            case "remove_by_id":
                if (arg.isEmpty()) {
                    System.out.println("Укажите id");
                    return null;
                }
                try {
                    return new RemoveByIdCommand(Long.parseLong(arg));
                } catch (NumberFormatException e) {
                    System.out.println("id должен быть числом");
                    return null;
                }

            case "clear":
                return new ClearCommand();

            case "show":
                return new ShowCommand();

            case "info":
                return new InfoCommand();

            case "remove_lower":
                System.out.println("Введите город для сравнения:");
                return new RemoveLowerCommand(cityFactory.createCity());

            case "reorder":
                return new ReorderCommand();

            case "sort":
                return new SortCommand();

            case "remove_any_by_standard_of_living":
                if (arg.isEmpty()) {
                    System.out.println("Укажите уровень жизни");
                    System.out.println("Доступные значения: " + StandardOfLiving.getNames() + ", null");
                    return null;
                }
                try {
                    if (arg.equalsIgnoreCase("null")) {
                        return new RemoveAnyByStandardOfLivingCommand(null);
                    }
                    return new RemoveAnyByStandardOfLivingCommand(StandardOfLiving.valueOf(arg.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный уровень жизни");
                    return null;
                }

            case "group_counting_by_governor":
                return new GroupCountingByGovernorCommand();

            case "filter_starts_with_name":
                if (arg.isEmpty()) {
                    System.out.println("Укажите начало имени");
                    return null;
                }
                return new FilterStartsWithNameCommand(arg);

            case "help":
                return new HelpCommand();

            case "exit":
                return new ExitCommand();

            default:
                System.out.println("Неизвестная команда. Введите 'help' для справки.");
                return null;
        }
    }
}
