package com.map.gaja.client.infrastructure.repository.querydsl.sql;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.spatial.locationtech.jts.JTSPointPath;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class MysqlNativeSqlCreator implements NativeSqlCreator {
    static final String radiusSearchQueryTemplate = "ST_Distance_Sphere(ST_GeomFromText('%s', 4326), {0})";
    static final String currentLocationPointFormat = "Point(%f %f)";

    @Override
    public NumberExpression<Double> createCalcDistanceSQL(Point currentLocation, JTSPointPath dbLocation) {
        String radiusSearchQuery = createRadiusSearchQuery(currentLocation);
        return Expressions.numberTemplate(Double.class, radiusSearchQuery, dbLocation);
    }

    private String createRadiusSearchQuery(Point currentLocation) {
        String currentLocationPoint = String.format(currentLocationPointFormat, currentLocation.getY(), currentLocation.getX());
        return String.format(radiusSearchQueryTemplate, currentLocationPoint);
    }
}
