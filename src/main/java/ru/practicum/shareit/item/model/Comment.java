package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "comments", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String text;
    @OneToOne()
    @JoinColumn(name = "item_id")
    private Item item;
    @OneToOne()
    @JoinColumn(name = "author_id")
    private User author;
    @CreationTimestamp
    private LocalDateTime created;
}
