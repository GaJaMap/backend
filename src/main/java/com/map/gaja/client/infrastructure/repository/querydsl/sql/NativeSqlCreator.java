package com.map.gaja.client.infrastructure.repository.querydsl.sql;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.spatial.locationtech.jts.JTSPointPath;
import org.locationtech.jts.geom.Point;

public interface NativeSqlCreator {
    NumberExpression<Double> createCalcDistanceSQL(Point constant, JTSPointPath location);
    BooleanExpression createRadiusSearchSQL(Point currentLocation, JTSPointPath dbLocation, int distance);
}
