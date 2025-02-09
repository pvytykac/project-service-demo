package net.pvytykac.db;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * @author Paly
 * @since 2025-02-08
 */
public enum Status {

    OK, WARN, ERROR,

    @JsonEnumDefaultValue
    UNKNOWN

}
