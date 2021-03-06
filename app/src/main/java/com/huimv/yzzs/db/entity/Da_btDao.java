package com.huimv.yzzs.db.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.huimv.yzzs.db.entity.Da_bt;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DA_BT.
*/
public class Da_btDao extends AbstractDao<Da_bt, Long> {

    public static final String TABLENAME = "DA_BT";

    /**
     * Properties of entity Da_bt.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Jqid = new Property(1, String.class, "jqid", false, "JQID");
        public final static Property Lcid = new Property(2, String.class, "lcid", false, "LCID");
        public final static Property Sblx = new Property(3, String.class, "sblx", false, "SBLX");
        public final static Property Lydz = new Property(4, String.class, "lydz", false, "LYDZ");
        public final static Property Lybm = new Property(5, String.class, "lybm", false, "LYBM");
        public final static Property Sjbz = new Property(6, String.class, "sjbz", false, "SJBZ");
    }


    public Da_btDao(DaoConfig config) {
        super(config);
    }
    
    public Da_btDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DA_BT' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'JQID' TEXT," + // 1: jqid
                "'LCID' TEXT," + // 2: lcid
                "'SBLX' TEXT," + // 3: sblx
                "'LYDZ' TEXT," + // 4: lydz
                "'LYBM' TEXT," + // 5: lybm
                "'SJBZ' TEXT);"); // 6: sjbz
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DA_BT'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Da_bt entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String jqid = entity.getJqid();
        if (jqid != null) {
            stmt.bindString(2, jqid);
        }
 
        String lcid = entity.getLcid();
        if (lcid != null) {
            stmt.bindString(3, lcid);
        }
 
        String sblx = entity.getSblx();
        if (sblx != null) {
            stmt.bindString(4, sblx);
        }
 
        String lydz = entity.getLydz();
        if (lydz != null) {
            stmt.bindString(5, lydz);
        }
 
        String lybm = entity.getLybm();
        if (lybm != null) {
            stmt.bindString(6, lybm);
        }
 
        String sjbz = entity.getSjbz();
        if (sjbz != null) {
            stmt.bindString(7, sjbz);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Da_bt readEntity(Cursor cursor, int offset) {
        Da_bt entity = new Da_bt( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // jqid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // lcid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sblx
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // lydz
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // lybm
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // sjbz
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Da_bt entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setJqid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLcid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSblx(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLydz(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLybm(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSjbz(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Da_bt entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Da_bt entity) {
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
