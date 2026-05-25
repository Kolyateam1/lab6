package common.models;

import java.io.Serializable;

/**
 * Перечисление форм правления.
 */
public enum Government implements Serializable {

    KRITARCHY,
    MERITOCRACY,
    NOOCRACY,
    REPUBLIC;

    private static final long serialVersionUID = 1L;

    public static String getNames() {
        StringBuilder sb = new StringBuilder();
        for (Government gov : values()) {
            sb.append(gov.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
