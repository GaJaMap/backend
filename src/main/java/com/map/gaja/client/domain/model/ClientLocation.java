package com.map.gaja.client.domain.model;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ClientLocation {
    @Column(columnDefinition = "GEOMETRY SRID 4326") // MySQL
//    @Column(columnDefinition = "geometry(Point, 4326)") // PostgreSQL
    private Point location;

    public ClientLocation(double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
