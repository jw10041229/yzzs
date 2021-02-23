package com.huimv.yzzs.db.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.huimv.yzzs.db.entity.Da_scj;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DA_SCJ.
*/
public class Da_scjDao extends AbstractDao<Da_scj, Long> {

    public static final String TABLENAME = "DA_SCJ";

    /**
     * Properties of entity Da_scj.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Jqid = new Property(1, String.class, "jqid", false, "JQID");
        public final static Property Rfid = new Property(2, String.class, "rfid", false, "RFID");
        public final static Property Tw = new Property(3, String.class, "tw", false, "TW");
        public final static Property Cjsj = new Property(4, String.class, "cjsj", false, "CJSJ");
    }


    public Da_scjDao(DaoConfig config) {
        super(config);
    }
    
    public Da_scjDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DA_SCJ' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'JQID' TEXT," + // 1: jqid
                "'RFID' TEXT," + // 2: rfid
                "'TW' TEXT," + // 3: tw
                "'CJSJ' TEXT);"); // 4: cjsj
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DA_SCJ'";
        db.execSQL(sql);
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Da_scj entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String jqid = entity.getJqid();
        if (jqid != null) {
            stmt.bindString(2, jqid);
        }
 
        String rfid = entity.getRfid();
        if (rfid != null) {
            stmt.bindString(3, rfid);
        }
 
        String tw = entity.getTw();
        if (tw != null) {
            stmt.bindString(4, tw);
        }
 
        String cjsj = entity.getCjsj();
        if (cjsj != null) {
            stmt.bindString(5, cjsj);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset) ? null : cursor.getLong(offset);
    }    

    @Override
    public Da_scj readEntity(Cursor cursor, int offset) {
        return new Da_scj( //
            cursor.isNull(offset) ? null : cursor.getLong(offset), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // jqid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // rfid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // tw
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // cjsj
        );
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Da_scj entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setJqid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRfid(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTw(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCjsj(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected Long updateKeyAfterInsert(Da_scj entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Da_scj entity) {
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
