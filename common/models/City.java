package common.models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Представляет город со всеми характеристиками.
 * Реализует {@link Comparable} для сортировки по id.
 * @author [Бахуров Николай]
 * @version 1.0
 */

public class City implements Comparable<City>, Serializable{
    /**
     * Следующий доступный id для новых городов.
     */
    private static long nextId = 1;
    private static final long serialVersionUID = 1L;

    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private double area; //Значение поля должно быть больше 0
    private Long population; //Значение поля должно быть больше 0, Поле не может быть null
    private Long metersAboveSeaLevel;
    private Climate climate; //Поле не может быть null
    private Government government; //Поле не может быть null
    private StandardOfLiving standardOfLiving; //Поле может быть null
    private Human governor; //Поле не может быть null

    /**
     * Создаёт новый город с автоматической генерацией id и даты создания.
     */
    public City() {
        this.id = nextId++;
        this.creationDate = LocalDate.now();
    }

    public long getId() { return id;}
    public String getName() { return name;}
    public Coordinates getCoordinates() { return coordinates;}
    public LocalDate getLocalDate() { return creationDate;}
    public double getArea() { return area;}
    public long getPopulation() { return population;}
    public Long getMetersAboveSeaLevel() { return metersAboveSeaLevel;}
    public Climate getClimate() { return climate;}
    public Government getGovernment() { return government;}
    public StandardOfLiving getStandardOfLiving() { return standardOfLiving;}
    public Human getGovernor() { return governor;}
    public LocalDate getCreationDate() { return creationDate;}

    public void setName(String name) {
        if (name == null ) {
            throw new IllegalArgumentException("Название города не может быть null");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Название города не может быть пустым");
        }
        this.name = trimmed;
    }
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }
        this.coordinates = coordinates;
    }

    public void setArea(double area) {
        if (Double.isNaN(area) || Double.isInfinite(area)) {
            throw new IllegalArgumentException("Площадь должна быть числом");
        }
        if (area <= 0) {
            throw new IllegalArgumentException("Площадь должна быть больше 0");
        }
        this.area = area;
    }

    public void setPopulation(Long population) {
        if (population == null) {
            throw new IllegalArgumentException("Население не может быть null");
        }
        if (population <=0) {
            throw new IllegalArgumentException("население должно быть больше 0");
        }
        this.population = population;
    }

    public void setMetersAboveSeaLevel(Long metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public void setClimate(Climate climate) {
        if (climate == null) {
            throw new IllegalArgumentException("Климат не может быть null");
        }
        this.climate = climate;
    }
    
    public void setGovernment(Government government) {
        if (government == null) {
            throw new IllegalArgumentException("Правительство не может быть null");
        }
        this.government = government;
    }

    public void setStandardOfLiving(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }

    public void setGovernor(Human governor) {
        if (governor == null) {
            throw new IllegalArgumentException("Губернатор не может быть null");
        }
        this.governor = governor;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int compareTo(City obj) {
        return Long.compare(this.id, obj.id);
    }

    @Override
    public String toString() {
        return String.format("City[" +
                        "  id=%d" +
                        "  name='%s'" +
                        "  coordinates=(%s, %s)" +
                        "  creationDate=%s" +
                        "  area=%s" +
                        "  population=%d" +
                        "  metersAboveSeaLevel=%s" +
                        "  climate=%s" +
                        "  government=%s" +
                        "  standardOfLiving=%s" +
                        "  governorAge=%d" +
                        "]",
                id,
                name,
                coordinates.getX(),
                coordinates.getY(),
                creationDate,
                area,
                population,
                metersAboveSeaLevel != null ? metersAboveSeaLevel : "null",
                climate,
                government,
                standardOfLiving != null ? standardOfLiving : "null",
                governor.getAge()
        );
    }
}