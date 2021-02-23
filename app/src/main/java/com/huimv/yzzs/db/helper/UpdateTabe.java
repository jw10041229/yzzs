package com.huimv.yzzs.db.helper;

import android.database.sqlite.SQLiteDatabase;



public class UpdateTabe extends AbstractMigratorHelper{

	@Override
	public void onUpgrade(SQLiteDatabase db) {
	    /* Create a temporal table where you will copy all the data from the previous table that you need to modify with a non supported sqlite operation */
	    db.execSQL("CREATE TABLE " + "'SESSION_ENTITY_TEMP' (" + //
	            "'_id' INTEGER PRIMARY KEY ," + // 0: id
	            "'SESSIONID' TEXT ," + // 1: postId
	            "'FROMIP' TEXT NOT NULL," + // 2: userId
	            "'TOIP' TEXT NOT NULL," + // 3: version
	            "'GOSSIP' TEXT NOT NULL);"); // 9: postContent
	    /* Copy the data from one table to the new one */
	    db.execSQL("INSERT INTO SESSION_ENTITY_TEMP (_id, SESSIONID, FROMIP, TOIP, GOSSIP)" +
	            "SELECT _id, SESSIONID, FROMIP, TOIP, GOSSIP FROM SESSION_ENTITY;");

	    /* Delete the previous table */
	    db.execSQL("DROP TABLE SESSION_ENTITY");
	    /* Rename the just created table to the one that I have just deleted */
	    db.execSQL("ALTER TABLE SESSION_ENTITY_TEMP RENAME TO SESSION_ENTITY");

/*	     Add Index/es if you want them 
	    db.execSQL("CREATE INDEX " + "IDX_post_USER_ID ON post" +
	            " (USER_ID);");
	    //Example sql statement
	    db.execSQL("ALTER TABLE user ADD COLUMN USERNAME TEXT");*/
		
	}

}
