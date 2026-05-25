package server;

import common.commands.*;
import common.network.Request;
import common.network.Response;
import common.models.City;
import java.util.List;

public class RequestHandler {
    private final CollectionManager collectionManager;

    public RequestHandler(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public Response handle(Request request) {
        Command command = request.getCommand();

        try {
            switch (command.getName()) {
                case "add":
                    AddCommand addCmd = (AddCommand) command;
                    collectionManager.add(addCmd.getCity());
                    return new Response(true, "Город успешно добавлен");

                case "update":
                    UpdateCommand updateCmd = (UpdateCommand) command;
                    if (collectionManager.containsId(updateCmd.getId())) {
                        collectionManager.update(updateCmd.getId(), updateCmd.getCity());
                        return new Response(true, "Город обновлен");
                    }
                    return new Response(false, "Город с id " + updateCmd.getId() + " не найден");

                case "remove_by_id":
                    RemoveByIdCommand removeCmd = (RemoveByIdCommand) command;
                    if (collectionManager.containsId(removeCmd.getId())) {
                        collectionManager.removeById(removeCmd.getId());
                        return new Response(true, "Город удален");
                    }
                    return new Response(false, "Город с id " + removeCmd.getId() + " не найден");

                case "clear":
                    collectionManager.clear();
                    return new Response(true, "Коллекция очищена");

                case "show":
                    List<City> sortedCities = collectionManager.getSortedByName();
                    return new Response(true, "", sortedCities);

                case "info":
                    return new Response(true, collectionManager.getInfo());

                case "remove_lower":
                    RemoveLowerCommand lowerCmd = (RemoveLowerCommand) command;
                    collectionManager.removeLower(lowerCmd.getCity());
                    return new Response(true, "Элементы удалены");

                case "reorder":
                    collectionManager.reorder();
                    return new Response(true, "Порядок изменен на обратный");

                case "sort":
                    collectionManager.sort();
                    return new Response(true, "Коллекция отсортирована");

                case "remove_any_by_standard_of_living":
                    RemoveAnyByStandardOfLivingCommand solCmd = (RemoveAnyByStandardOfLivingCommand) command;
                    collectionManager.removeAnyByStandardOfLiving(solCmd.getStandardOfLiving());
                    return new Response(true, "Удален элемент с указанным уровнем жизни");

                case "group_counting_by_governor":
                    return new Response(true, collectionManager.getGroupCounting());

                case "filter_starts_with_name":
                    FilterStartsWithNameCommand filterCmd = (FilterStartsWithNameCommand) command;
                    List<City> filtered = collectionManager.filterStartsWithName(filterCmd.getPrefix());
                    return new Response(true, "", filtered);

                case "help":
                    return new Response(true, getHelpMessage());

                default:
                    return new Response(false, "Неизвестная команда");
            }
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage());
        }
    }

    private String getHelpMessage() {
        return "Доступные команды:\n" +
                "add - добавить город\n" +
                "update id - обновить город\n" +
                "remove_by_id id - удалить по id\n" +
                "clear - очистить коллекцию\n" +
                "show - показать все города\n" +
                "info - информация о коллекции\n" +
                "remove_lower - удалить меньшие\n" +
                "reorder - обратный порядок\n" +
                "sort - сортировка\n" +
                "remove_any_by_standard_of_living sol - удалить по уровню жизни\n" +
                "group_counting_by_governor - группировка по губернатору\n" +
                "filter_starts_with_name name - фильтр по имени\n" +
                "help - справка\n" +
                "exit - выход";
    }
}