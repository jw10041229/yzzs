package com.huimv.yzzs.db.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.huimv.yzzs.db.entity.Da_ph;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DA_PH.
*/
public class Da_phDao extends AbstractDao<Da_ph, Long> {

    public static final String TABLENAME = "DA_PH";

    /**
     * Properties of entity Da_ph.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Hkphsx = new Property(1, String.class, "hkphsx", false, "HKPHSX");
        public final static Property Hkph = new Property(2, String.class, "hkph", false, "HKPH");
        public final static Property Hksj = new Property(3, String.class, "hksj", false, "HKSJ");
        public final static Property Hkazwz = new Property(4, String.class, "hkazwz", false, "HKAZWZ");
    }


    public Da_phDao(DaoConfig config) {
        super(config);
    }
    
    public Da_phDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DA_PH' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'HKPHSX' TEXT," + // 1: hkphsx
                "'HKPH' TEXT," + // 2: hkph
                "'HKSJ' TEXT," + // 3: hksj
                "'HKAZWZ' TEXT);"); // 4: hkazwz
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DA_PH'";
        db.execSQL(sql);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Da_ph entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String hkphsx = entity.getHkphsx();
        if (hkphsx != null) {
            stmt.bindString(2, hkphsx);
        }
 
        String hkph = entity.getHkph();
        if (hkph != null) {
            stmt.bindString(3, hkph);
        }
 
        String hksj = entity.getHksj();
        if (hksj != null) {
            stmt.bindString(4, hksj);
        }
 
        String hkazwz = entity.getHkazwz();
        if (hkazwz != null) {
            stmt.bindString(5, hkazwz);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }    

    @Override
    public Da_ph readEntity(Cursor cursor, int offset) {
        return new Da_ph( //
            cursor.isNull(offset) ? null : cursor.getLong(offset), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // hkphsx
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // hkph
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // hksj
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // hkazwz
        );
    }
     
    @Override
    public void readEntity(Cursor cursor, Da_ph entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setHkphsx(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setHkph(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setHksj(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setHkazwz(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected Long updateKeyAfterInsert(Da_ph entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Da_ph entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
