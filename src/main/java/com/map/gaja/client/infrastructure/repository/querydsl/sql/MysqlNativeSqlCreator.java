package com.map.gaja.client.infrastructure.repository.querydsl.sql;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.stereotype.Component;

@Component
public class MysqlNativeSqlCreator implements NativeSqlCreator {
    public NumberExpression<Double> createCalcDistanceSQL(Double latitudeCond, Double longitudeCond,
                                                          NumberPath<Double> dbLatitude, NumberPath<Double> dbLongitude) {
        return Expressions.numberTemplate(Double.class,"ST_Distance_Sphere({0}, {1})",
                Expressions.stringTemplate("POINT({0}, {1})",
                        latitudeCond,
                        longitudeCond
                ),
                Expressions.stringTemplate("POINT({0}, {1})",
                        dbLatitude,
                        dbLongitude
                        )
        );
    }
}
