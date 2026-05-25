package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.models.*;

/**
 * Отвечает за чтение и запись коллекции в CSV-файл.
 * Использует {@link java.io.InputStreamReader} и {@link java.io.PrintWriter}.
 */
public class FileManager {
    private String filename;
    private static final String CSV_DELIMITER = ";";
    private static final int EXPECTED_FIELDS = 12;

    public FileManager(String filename) {
        this.filename = filename;
    }

    public LinkedList<City> readCollection() {
        LinkedList<City> collection = new LinkedList<>();
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("Файл не существует, будет создан новый при сохранении");
            return collection;
        }

        if (!file.canRead()) {
            System.err.println("Нет прав на чтение файла");
            return collection;
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
            BufferedReader br = new BufferedReader(reader);
            String line;
            int lineNum = 0;
            
            while ((line = br.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) {
                    continue; // пропускаем пустые строки
                }
                
                try {
                    City city = parseCity(line);
                    if (city != null) {
                        collection.add(city);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка в строке " + lineNum + ": " + e.getMessage());
                }
            }
            
            System.out.println("Загружено городов: " + collection.size());
            
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
        
        return collection;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ';' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result;
    }

    public void writeCollection(LinkedList<City> collection) throws IOException {
        File file = new File(filename);
        
        if (!file.exists()) {
            file.createNewFile();
        }
        
        if (!file.canWrite()) {
            throw new IOException("Нет прав на запись в файл");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (City city : collection) {
                writer.println(cityToCSV(city));
            }
            writer.flush();
        }
    }

    private City parseCity(String line) {
        List<String> parts = parseCsvLine(line);

        if (parts.size() < EXPECTED_FIELDS) {
            throw new IllegalArgumentException("Недостаточно полей.");
        }

        if (parts.size() > EXPECTED_FIELDS) {
            throw new IllegalArgumentException("Слишком много полей. ");
        }

        try {
            City city = new City();

            // name
            if (parts.get(1).isEmpty()) {
                throw new IllegalArgumentException("Пустое имя");
            }
            String name = unescapeCsv(parts.get(1));
            city.setName(name);

            // coordinates
            if (parts.get(2).isEmpty() || parts.get(3).isEmpty()) {
                throw new IllegalArgumentException("Пустые координаты");
            }
            Coordinates coords = new Coordinates(
                    Double.parseDouble(parts.get(2)),
                    Float.parseFloat(parts.get(3))
            );
            city.setCoordinates(coords);

            // area
            if (parts.get(5).isEmpty()) {
                throw new IllegalArgumentException("Пустая площадь");
            }
            city.setArea(Double.parseDouble(parts.get(5)));

            // population
            if (parts.get(6).isEmpty()) {
                throw new IllegalArgumentException("Пустое население");
            }
            city.setPopulation(Long.parseLong(parts.get(6)));

            // metersAboveSeaLevel
            if (!parts.get(7).isEmpty()) {
                city.setMetersAboveSeaLevel(Long.parseLong(parts.get(7)));
            }

            // climate
            if (parts.get(8).isEmpty()) {
                throw new IllegalArgumentException("Пустой климат");
            }
            city.setClimate(Climate.valueOf(parts.get(8)));

            // government
            if (parts.get(9).isEmpty()) {
                throw new IllegalArgumentException("Пустое правление");
            }
            city.setGovernment(Government.valueOf(parts.get(9)));

            // standardOfLiving
            if (!parts.get(10).isEmpty()) {
                city.setStandardOfLiving(StandardOfLiving.valueOf(parts.get(10)));
            }

            // governor
            if (parts.get(11).isEmpty()) {
                throw new IllegalArgumentException("Пустой возраст губернатора");
            }
            Human governor = new Human(Long.parseLong(parts.get(11)));
            city.setGovernor(governor);

            return city;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат числа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверное значение: " + e.getMessage());
        }
    }

    private String escapeCsv(String field) {
        if (field == null) return "";
        if (field.contains(";") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return field;
    }

    private String unescapeCsv(String field) {
        if (field == null || field.isEmpty()) return "";
        if (field.startsWith("\"") && field.endsWith("\"")) {
            String inner = field.substring(1, field.length() - 1);
            return inner.replace("\"\"", "\"");
        }
        return field;
    }

    private String cityToCSV(City city) {
        return String.join(CSV_DELIMITER,
            String.valueOf(city.getId()),
                escapeCsv(city.getName()),
            String.valueOf(city.getCoordinates().getX()),
            String.valueOf(city.getCoordinates().getY()),
            city.getCreationDate().toString(),
            String.valueOf(city.getArea()),
            String.valueOf(city.getPopulation()),
            city.getMetersAboveSeaLevel() != null ? String.valueOf(city.getMetersAboveSeaLevel()) : "",
            city.getClimate().name(),
            city.getGovernment().name(),
            city.getStandardOfLiving() != null ? city.getStandardOfLiving().name() : "",
            String.valueOf(city.getGovernor().getAge())
        );
    }
}
