package com.map.gaja.client.infrastructure.repository.querydsl.sql;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.spatial.locationtech.jts.JTSPointPath;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class MysqlNativeSqlCreator implements NativeSqlCreator {
    private static final String calculationDistanceQueryTemplate = "ST_DistanceSphere(ST_GeomFromText('%s', 4326), {0})";
    private static final String currentLocationPointFormat = "Point(%f %f)";
    private static final String radiusSearchSQLTemplate = "ST_DWithIn({0}, ST_GeomFromText('%s', 4326), {1}, false)";

    @Override
    public NumberExpression<Double> createCalcDistanceSQL(Point currentLocation, JTSPointPath dbLocation) {
        String distanceCalculationQuery = createCompleteQuery(calculationDistanceQueryTemplate, currentLocation);
        return Expressions.numberTemplate(Double.class, distanceCalculationQuery, dbLocation);
    }

    private String createCompleteQuery(String template, Point currentLocation) {
        String currentLocationPoint = String.format(currentLocationPointFormat, currentLocation.getX(), currentLocation.getY());
        return String.format(template, currentLocationPoint);
    }

    @Override
    public BooleanExpression createRadiusSearchSQL(Point currentLocation, JTSPointPath dbLocation, int distance) {
        String radiusSearchQuery = createCompleteQuery(radiusSearchSQLTemplate, currentLocation);
        return Expressions.booleanTemplate(radiusSearchQuery, dbLocation, distance);
    }
}
