package com.map.gaja.global.config;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.spatial.dialect.postgis.PostgisPG10Dialect;
import org.hibernate.type.StandardBasicTypes;

public class PostGisCustomDialect extends PostgisPG10Dialect {
    public PostGisCustomDialect() {
        super();
        this.registerFunction("ST_DistanceSphere",new StandardSQLFunction("ST_DistanceSphere", StandardBasicTypes.DOUBLE));
    }
}
