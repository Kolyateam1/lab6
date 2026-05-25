package client.utils;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.NoSuchElementException;

/**
 * Обеспечивает безопасный ввод данных с консоли.
 * Повторяет запрос при ошибках, обрабатывает пустые строки и null.
 */
public class InputValidator {
    private Scanner scanner;
    private boolean fileMode;

    public InputValidator() {
        this.scanner = new Scanner(System.in);
        this.fileMode = false;
    }
    
    public void setFileMode(boolean fileMode) {
        this.fileMode = fileMode;
    }

    public boolean isFileMode() {
        return fileMode;
    }

    public String readLine(String prompt) {
        if (!fileMode && prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }

        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println("\n Программа завершена. Но вообще для этого существует команда exit...");
            System.exit(0);
            return "";
        } catch (IllegalStateException e) {
            System.out.println("Ошибка ввода/вывода");
            return "";
        }
    }

    public String readNonEmptyLine(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (line == null) {
                continue;
            }
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
            System.out.println("Строка не может быть пустой");
        }
    }

    public Long readLong(String prompt, boolean canBeNull, Long min) {
        while (true) {
            String line = readLine(prompt);
            if (canBeNull && line.trim().isEmpty()) {
                return null;
            }
            try {
                long value = Long.parseLong(line.trim());
                if (min != null && value < min) {
                    System.out.println("Значение должно быть не меньше " + min);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число");
            }
        }
    }

    public Double readDouble(String prompt, boolean canBeNull, Double min) {
        while (true) {
            String line = readLine(prompt);
            if (canBeNull && line.trim().isEmpty()) {
                return null;
            }
            try {
                String normalized = line.trim().replace(',', '.');
                String[] parts = normalized.split("\\.");
                if (parts.length == 2 && parts[1].length() > 15) {
                    normalized = parts[0] + "." + parts[1].substring(0, 12);
                }

                double value = Double.parseDouble(normalized);

                if (min != null && value <= min) {
                    System.out.println("Ошибка: значение должно быть больше " + min);
                    continue;
                }
                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число");
            }
        }
    }

    public Float readFloat(String prompt, boolean canBeNull, Float min) {

        while (true) {
            String line = readLine(prompt);

            if (canBeNull && line.trim().isEmpty()) {
                return null;
            }
            
            try {
                String normalized = line.trim().replace(',', '.');
                String[] parts = normalized.split("\\.");
                if (parts.length == 2 && parts[1].length() > 7) {
                    normalized = parts[0] + "." + parts[1].substring(0, 4);
                }

                float value = Float.parseFloat(normalized);

                if (min != null && value <= min) {
                    System.out.println("Ошибка: значение должно быть больше " + min);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число");
            }
        }
    }

    public <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, boolean canBeNull) {
        T[] constants = enumClass.getEnumConstants();
        
        System.out.println("Доступные значения: " + getEnumNames(constants));
        
        while (true) {
            String line = readLine(prompt);
            
            if (canBeNull && line.trim().isEmpty()) {
                return null;
            }
            
            String upperLine = line.trim().toUpperCase();
            
            for (T constant : constants) {
                if (constant.name().equals(upperLine)) {
                    return constant;
                }
            }
            
            System.out.println("Ошибка: введите одно из доступных значений");
        }
    }

    private <T extends Enum<T>> String getEnumNames(T[] constants) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < constants.length; i++) {
            sb.append(constants[i].name());
            if (i < constants.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
