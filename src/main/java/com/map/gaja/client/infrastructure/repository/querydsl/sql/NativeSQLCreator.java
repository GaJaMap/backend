package com.map.gaja.client.infrastructure.repository.querydsl.sql;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;

public interface NativeSQLCreator {
    NumberExpression<Double> createCalcDistanceSQL(Double longitudeCond, Double latitudeCond, NumberPath<Double> dbLongitude, NumberPath<Double> dbLatitude);
}
