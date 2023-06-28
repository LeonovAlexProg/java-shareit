package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id")
    private User user;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_available")
    private Boolean isAvailable;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public static Item of(ItemDto itemUserDto) {
        return Item.builder()
                .id(itemUserDto.getId())
                .name(itemUserDto.getName())
                .description(itemUserDto.getDescription())
                .isAvailable(itemUserDto.getAvailable())
                .build();
    }
}
