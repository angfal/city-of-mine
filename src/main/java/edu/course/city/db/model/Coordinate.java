package edu.course.city.db.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "coordinates")
public class Coordinate implements Serializable {

    private static final long serialVersionUID = -1729775725782052127L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(name = "_order")
	private int order = 0;

	public Coordinate() {
	}

    public Coordinate(Coordinate coordinate) {
        this(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getOrder());
    }

	public Coordinate(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

    public Coordinate(Double latitude, Double longitude, int order) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
