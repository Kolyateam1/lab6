package server;

import common.models.City;
import common.models.StandardOfLiving;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private LinkedList<City> collection;
    private LocalDateTime initDate;
    private FileManager fileManager;

    public CollectionManager() {
        this.fileManager = new FileManager("collection.csv");
        this.collection = new LinkedList<>();
        this.initDate = LocalDateTime.now();
        loadCollection();
    }

    private void loadCollection() {
        LinkedList<City> loaded = fileManager.readCollection();
        if (loaded != null) {
            this.collection = loaded;
        }
    }

    public void add(City city) {
        collection.add(city);
    }

    public void update(long id, City newCity) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                newCity.setId(id);
                collection.set(i, newCity);
                break;
            }
        }
    }

    public void removeById(long id) {
        collection.removeIf(c -> c.getId() == id);
    }

    public boolean containsId(long id) {
        return collection.stream().anyMatch(c -> c.getId() == id);
    }

    public void clear() {
        collection.clear();
    }

    public void removeLower(City city) {
        collection.removeIf(c -> c.compareTo(city) < 0);
    }

    public void reorder() {
        Collections.reverse(collection);
    }

    public void sort() {
        Collections.sort(collection);
    }

    public void removeAnyByStandardOfLiving(StandardOfLiving sol) {
        collection.stream()
                .filter(c -> (sol == null && c.getStandardOfLiving() == null) ||
                        (sol != null && sol.equals(c.getStandardOfLiving())))
                .findFirst()
                .ifPresent(collection::remove);
    }

    public String getGroupCounting() {
        return collection.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getGovernor().getAge(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> "Возраст " + e.getKey() + ": " + e.getValue() + " городов")
                .collect(Collectors.joining("\n"));
    }

    public List<City> filterStartsWithName(String prefix) {
        return collection.stream()
                .filter(c -> c.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<City> getSortedByName() {
        return collection.stream()
                .sorted(Comparator.comparing(City::getName))
                .collect(Collectors.toList());
    }

    public String getInfo() {
        return "Тип коллекции: " + collection.getClass().getName() + "\n" +
                "Дата инициализации: " + initDate + "\n" +
                "Количество элементов: " + collection.size();
    }

    public void shutdown() {
        try {
            fileManager.writeCollection(collection);
            ServerLogger.info("Коллекция сохранена при завершении");
        } catch (IOException e) {
            ServerLogger.severe("Не удалось сохранить коллекцию: " + e.getMessage());
        }
    }
}