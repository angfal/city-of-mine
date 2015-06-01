package edu.course.city.db.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "groups")
public class Group implements Serializable, Cloneable {

    private static final long serialVersionUID = -603572672480542504L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Group parent;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private GroupType type;

    @Column(name = "global_copy")
    private boolean globalCopy = false;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "icon_id")
    private Picture icon;

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

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parentId) {
        this.parent = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public boolean isGlobalCopy() {
        return globalCopy;
    }

    public void setGlobalCopy(boolean globalCopy) {
        this.globalCopy = globalCopy;
    }

    public Picture getIcon() {
        return icon;
    }

    public void setIcon(Picture icon) {
        this.icon = icon;
    }
}
