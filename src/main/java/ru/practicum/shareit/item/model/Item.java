package ru.practicum.shareit.item.model;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "last_start_booking")
    private LocalDate lastStartBooking;

    @Column(name = "last_end_booking")
    private LocalDate lastEndBooking;

    @Column(name = "next_start_booking")
    private LocalDate nextStartBooking;

    @Column(name = "next_end_booking")
    private LocalDate nextEndBooking;

    @OneToMany
    @JoinColumn(name = "item_id")
    private List<Comment> comments;
}
