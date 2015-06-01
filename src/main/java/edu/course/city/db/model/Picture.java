package edu.course.city.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pictures")
public class Picture implements Serializable {

    private static final long serialVersionUID = -9146795215451641887L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "picture_places",
            joinColumns = @JoinColumn(name = "picture_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "place_id", nullable = false))
    private List<Place> places;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Lob
    @Column(nullable = false, length = 10000004)
    private byte[] data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Place> getPlaces() {
        if (places == null) {
            places = new ArrayList<>();
        }
        return places;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
