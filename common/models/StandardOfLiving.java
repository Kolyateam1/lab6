package common.models;

import java.io.Serializable;

/**
 * Перечисление уровней жизни.
 */
public enum StandardOfLiving implements Serializable {

    ULTRA_HIGH,
    VERY_HIGH,
    VERY_LOW,
    NIGHTMARE;

    private static final long serialVersionUID = 1L;

    public static String getNames() {
        StringBuilder sb = new StringBuilder();
        for (StandardOfLiving s : values()) {
            sb.append(s.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
