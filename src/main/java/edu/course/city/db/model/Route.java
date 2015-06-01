package edu.course.city.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route implements Serializable {

    private static final long serialVersionUID = -3149741677755162824L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Group group;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "main_coordinates_id")
    @OrderBy(value = "order")
    private List<Coordinate> mainCoordinates;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "route_coordinates_id")
    @OrderBy(value = "order")
    private List<Coordinate> routeCoordinates;

    @Column(nullable = false)
    private String name;

    @Lob
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Coordinate> getMainCoordinates() {
        if (mainCoordinates == null) {
            mainCoordinates = new ArrayList<>();
        }
        return mainCoordinates;
    }

    public void setMainCoordinates(List<Coordinate> mainCoordinates) {
        this.mainCoordinates = mainCoordinates;
    }

    public List<Coordinate> getRouteCoordinates() {
        if (routeCoordinates == null) {
            routeCoordinates = new ArrayList<>();
        }
        return routeCoordinates;
    }

    public void setRouteCoordinates(List<Coordinate> routeCoordinates) {
        this.routeCoordinates = routeCoordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
