package ru.checkdev.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notify {

    private String email;
    private Map<String, ?> keys;

    @EqualsAndHashCode.Include
    private String template;

    public enum Type {
        REG, FORGOT, ORDER
    }
}
