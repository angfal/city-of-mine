package edu.course.city.db.model;

import org.primefaces.model.map.LatLng;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "places")
public class Place implements Serializable {

    private static final long serialVersionUID = 2052439186750579916L;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private Place source;

    @Column(nullable = false)
    private String name;

    @Lob
    private String description;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coordinate_id", nullable = false)
    private Coordinate coordinate;

    @Column(nullable = false)
    private Integer zoom;

    @Column(name = "start_moderation_date")
    private Date startModerationDate;

    @Column(name = "end_moderation_date")
    private Date endModerationDate;

    private Boolean moderated;

    @Lob
    @Column(name = "moderator_comments")
    private String moderatorComments;

    @Column(name = "global_access")
    private boolean globalAccess = false;

    @Column(name = "global_copy")
    private boolean globalCopy = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Place getSource() {
        return source;
    }

    public void setSource(Place source) {
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setCoordinate(LatLng latLng) {
        this.coordinate = new Coordinate(latLng.getLat(), latLng.getLng());
    }

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public boolean isGlobalAccess() {
        return globalAccess;
    }

    public void setGlobalAccess(boolean globalAccess) {
        this.globalAccess = globalAccess;
    }

    public Boolean getModerated() {
        return moderated;
    }

    public void setModerated(Boolean moderated) {
        this.moderated = moderated;
    }

    public boolean isGlobalCopy() {
        return globalCopy;
    }

    public void setGlobalCopy(boolean copy) {
        this.globalCopy = copy;
    }

    public String getModeratorComments() {
        return moderatorComments;
    }

    public void setModeratorComments(String moderatorComments) {
        this.moderatorComments = moderatorComments;
    }

    public Date getStartModerationDate() {
        return startModerationDate;
    }

    public void setStartModerationDate(Date startModerationDate) {
        this.startModerationDate = startModerationDate;
    }

    public Date getEndModerationDate() {
        return endModerationDate;
    }

    public void setEndModerationDate(Date endModerationDate) {
        this.endModerationDate = endModerationDate;
    }
}
