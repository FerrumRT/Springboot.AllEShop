package kz.springboot.main.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ShopItems itemId;

    @Column(name = "count")
    private int count;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "bought_date")
    private Timestamp boughtDate;
}
