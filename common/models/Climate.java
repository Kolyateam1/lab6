package common.models;

import java.io.Serializable;

/**
 * Перечисление возможных климатических зон.
 */
public enum Climate implements Serializable{

    TROPICAL_SAVANNA,
    OCEANIC,
    MEDITERRANIAN;

    private static final long serialVersionUID = 1L;

    public static String getNames() {
        StringBuilder sb = new StringBuilder();
        for (Climate clim : values()) {
            sb.append(clim.name()).append(", ");
        }
        return sb.substring(0, sb.length()-2);
    }
}
