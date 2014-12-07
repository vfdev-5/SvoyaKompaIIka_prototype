package com.vfdev.android.core;


import android.util.Pair;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.HashMap;

// Database configuration class.
// Can be serialized and deserialized with 'Simple Xml' library from xml file
@Root
public class DBConf {


    @Attribute(name="db_name")
    private String mDbName=null;
    @Attribute(name="db_version")
    private int mDbVersion=0;
    @ElementList(inline=true, type=DBTable.class)
    private ArrayList<DBTable> mDbTables;

    private String[] mDbTablenames;


    /// Public methods
    public String getDbName() {
        return mDbName;
    }
    public int getDbVersion() {
        return mDbVersion;
    }
    public int getTableCount() {
        return mDbTables.size();
    }
    public String[] getTablenames() {
        if (mDbTablenames==null) {
            mDbTablenames = new String[mDbTables.size()];
            for (int i=0;i<mDbTablenames.length;i++) {
                mDbTablenames[i] = mDbTables.get(i).name;
            }
        }
        return mDbTablenames;
    }

    /// For Debug purposes :
    public String printSelf() {
        String out = "DBConf : " + mDbName +", v=" + mDbVersion + "\n";
        for (DBTable table : mDbTables) {
            out += "- " + table.name + "\n";
            for (TableKeyTypePair p : table.tableKeyTypePairs) {
                out += "-- " + p.key + " : " + p.type + "\n";
            }
        }
        return out;
    }

    /// Method composes the query to create a table
    public String createTableQuery(int index) {
        if (mDbTables == null ||
                index >= mDbTables.size() || index < 0) {
            return null;
        }
        String tableName = mDbTables.get(index).name;
        ArrayList<TableKeyTypePair> tableKeyTypePairs=mDbTables.get(index).tableKeyTypePairs;

        StringBuilder out = new StringBuilder("CREATE TABLE " + tableName + "(");
        for (TableKeyTypePair p : tableKeyTypePairs) {
            out.append(p.key);
            out.append(" ");
            out.append(p.type);
            out.append(", ");
        }
        out.setCharAt(out.length()-2,')');
        out.setCharAt(out.length()-1,';');
        return out.toString();
    }

    /// Method composes the query to drop a table
    public String dropTableQuery(int index) {
        if (mDbTables == null || index >= mDbTables.size() || index < 0) {
            return null;
        }
        return "DROP TABLE IF EXISTS " + mDbTables.get(index).name + ";";
    }

}

@Root(name="db_table")
class DBTable {
    @Attribute
    public String name;
    @ElementList(inline=true, type=TableKeyTypePair.class)
    public ArrayList<TableKeyTypePair> tableKeyTypePairs;
}

/// For serialization purposes we define a class Pair
@Root(name="pair")
class TableKeyTypePair {
    @Attribute
    public String key;
    @Attribute
    public String type;
}