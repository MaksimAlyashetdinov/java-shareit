package ru.practicum.shareit.item;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    private Long id;

    @Column(name = "item_id")
    private long itemId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "text")
    private String text;

    @Column(name = "create_date")
    private LocalDate createDate;
}
