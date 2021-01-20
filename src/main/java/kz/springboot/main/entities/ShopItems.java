package kz.springboot.main.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "shopItems")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "stars")
    private int stars;

    @Column(name = "small_picture_url")
    private String smallPictureUrl;

    @Column(name = "large_picture_url")
    private String largePictureUrl;

    @Column(name = "added_date")
    private Date addedDate;

    @Column(name = "in_top_page")
    private boolean inTopPage;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brands brandId;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Categories> categories;
    
}
