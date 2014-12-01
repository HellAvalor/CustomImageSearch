package com.andreykaraman.idstest.db;

public abstract class AbsDBObject implements DBItem {

    public static final long EMPTY_ID = -1L;

    public static long safeId(Long id) {
        return id == null ? EMPTY_ID : id;
    }

    public static Long objectId(long id) {
        return id == EMPTY_ID ? null : Long.valueOf(id);
    }

    public static String field(String tableName, String fieldName) {
        return tableName + "." + fieldName;
    }

    @Override
    public Long getIdAsObject() {
        return objectId(getId());
    }

}
