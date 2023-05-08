package ru.practicum.shareit.booking;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private Long id;

    @JoinColumn(name = "item_Id")
    @OneToOne
    @ToString.Exclude
    private Item item;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "start_Booking")
    private LocalDate startBooking;

    @Column(name = "end_Booking")
    private LocalDate endBooking;

    @Column(name ="state")
    @Enumerated(EnumType.STRING)
    private BookingState state;

    @Column(name = "createDate")
    private LocalDate createDate;
}
