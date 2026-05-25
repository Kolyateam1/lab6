package client.utils;

import common.models.*;
import client.utils.InputValidator;

/**
 * Фабрика для создания объектов {@link City} через последовательный ввод полей.
 */
public class CityFactory {
    private InputValidator validator;

    public CityFactory(InputValidator validator) {
        this.validator = validator;
    }
    
    public City createCity() {
        City city = new City();

        // name
        String name = validator.readNonEmptyLine("Введите название города:");
        city.setName(name);

        // coordinates
        System.out.println("Введите координаты:");
        Double x = validator.readDouble("X (> -591):", false, -591.0);
        Float y = validator.readFloat("Y (> -156): ", false, -156.0f);
        city.setCoordinates(new Coordinates(x, y));

        //area
        Double area = validator.readDouble("Площадь (> 0): ", false, 0.0);
        city.setArea(area);

        // population
        Long population = validator.readLong("Население (> 0): ", false, 1L);
        city.setPopulation(population);
        
        // metersAboveSeaLevel
        Long meters = validator.readLong("Высота над уровнем моря: ", false, -200L);
        city.setMetersAboveSeaLevel(meters);
        
        // climate
        Climate climate = validator.readEnum("Климат: ", Climate.class, false);
        city.setClimate(climate);
        
        // government
        Government government = validator.readEnum("Форма правления: ", Government.class, false);
        city.setGovernment(government);
        
        // standardOfLiving
        StandardOfLiving sol = validator.readEnum("Уровень жизни (enter для null): ", StandardOfLiving.class, true);
        city.setStandardOfLiving(sol);
        
        // governor
        System.out.println("\nВведите данные губернатора:");
        Long age = validator.readLong("Возраст губернатора (> 0): ", false, 1L);
        city.setGovernor(new Human(age));
        
        return city;
    }
}
