package com.map.gaja.client.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class ClientLocation {
    @Column(columnDefinition = "geometry(Point, 4326)") // PostgreSQL
    private Point location;

    public ClientLocation(double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
