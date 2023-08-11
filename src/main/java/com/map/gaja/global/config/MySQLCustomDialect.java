package com.map.gaja.global.config;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.spatial.dialect.mysql.MySQL8SpatialDialect;
import org.hibernate.type.StandardBasicTypes;

public class MySQLCustomDialect extends MySQL8SpatialDialect {
    public MySQLCustomDialect() {
        super();
        this.registerFunction("ST_Distance_Sphere",new StandardSQLFunction("ST_Distance_Sphere", StandardBasicTypes.DOUBLE));
    }
}
