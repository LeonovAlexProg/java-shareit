package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private Integer id;
    private Integer ownerId;
    private String name;
    private String description;
    private Boolean available;
}
