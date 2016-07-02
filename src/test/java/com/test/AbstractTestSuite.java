package com.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.jsql.model.accessible.DataAccess;
import com.jsql.model.accessible.bean.Column;
import com.jsql.model.accessible.bean.Database;
import com.jsql.model.accessible.bean.Table;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.exception.StoppableException;

public abstract class AbstractTestSuite {
    /**
     * Using default log4j.properties from root /
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractTestSuite.class);

    static {
        PropertyConfigurator.configure("src/test/java/log4j.tests.properties");
    }

    private List<String> databaseToFind = new ArrayList<>();
    private List<String> tableToFind = new ArrayList<>();
    private List<String> columnToFind = new ArrayList<>();
    private List<String> valueToFind = new ArrayList<>();
    
    protected String jdbcURL;
    protected String jdbcUser;
    protected String jdbcPassword;
    
    protected String jdbcQueryForDatabaseNames;
    protected String jdbcQueryForTableNames;
    protected String jdbcQueryForColumnNames;
    protected String jdbcQueryForValues;
    
    protected String jdbcColumnForDatabaseName;
    protected String jdbcColumnForTableName;
    protected String jdbcColumnForColumnName;
    
    protected String jsqlDatabaseName;
    protected String jsqlTableName;
    protected String jsqlColumnName;
    
    @Rule
    public Retry retry = new Retry(3);

    @BeforeClass
    public static void initialize() throws PreparationException, SQLException {
        System.err.println("AbstractTestSuite and ConcreteTestSuite are for initialization purpose.");
        System.err.println("Please run a test suite or a unit test instead.");
        throw new PreparationException();
    }

    public void initializer() throws PreparationException, SQLException {
        Connection conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);

        Statement stmt = null;
        try {
            ResultSet res = null;
            
            stmt = conn.createStatement();
            res = stmt.executeQuery(jdbcQueryForDatabaseNames);
                    
            while (res.next()) {
                String dbName = res.getString(jdbcColumnForDatabaseName);
                databaseToFind.add(dbName);
            }
            res.close();
            stmt.close();
            
            stmt = conn.createStatement();
            res = stmt.executeQuery(jdbcQueryForTableNames);

            while (res.next()) {
                String tableName = res.getString(jdbcColumnForTableName);
                tableToFind.add(tableName);
            }
            res.close();
            stmt.close();
            
            stmt = conn.createStatement();
            res = stmt.executeQuery(jdbcQueryForColumnNames);

            while (res.next()) {
                String colName = res.getString(jdbcColumnForColumnName);
                columnToFind.add(colName);
            }
            res.close();
            stmt.close();

            stmt = conn.createStatement();
            res = stmt.executeQuery(jdbcQueryForValues);

            while (res.next()) {
                String value = res.getString(jsqlColumnName);
                valueToFind.add(value);
            }
            res.close();

            conn.close();
        } catch(Exception e) {
            LOGGER.warn(e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch(SQLException se2) {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch(SQLException e) {
                    LOGGER.warn(e);
                }
            }
        }
    }

    @Test
    public void listDatabases() throws PreparationException, StoppableException {
        Set<Object> set1 = new HashSet<>();
        Set<Object> set2 = new HashSet<>();
        
        try {
            List<Database> dbs = DataAccess.listDatabases();
            List<String> databasesFound = new ArrayList<>();
            for (Database d: dbs) {
                databasesFound.add(d.toString());
            }

            set1.addAll(databasesFound);
            set2.addAll(databaseToFind);

            LOGGER.info("ListDatabases: found "+ set1 +"\nto find "+ set2 +"\n");

            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
            
        } catch(AssertionError e) {
            Set<Object> tmp = new TreeSet<>();
            for (Object x: set1) {
                if (!set2.contains(x)) {
                    tmp.add(x);
                }
            }
            for (Object x: set2) {
                if (!set1.contains(x)) {
                    tmp.add(x);
                }
            }
            throw new AssertionError("Error listDatabases: "+ tmp +"\n"+ e);
        }
    }

    @Test
    public void listTables() throws PreparationException, StoppableException {
        Set<Object> set1 = new HashSet<>();
        Set<Object> set2 = new HashSet<>();

        try {
            List<Table> ts = DataAccess.listTables(new Database(jsqlDatabaseName, "0"));
            List<String> tablesFound = new ArrayList<>();
            for (Table t: ts) {
                tablesFound.add(t.toString());
            }

            set1.addAll(tablesFound);
            set2.addAll(tableToFind);

            LOGGER.info("listTables: found "+ set1 +"\nto find "+ set2 +"\n");
            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
            
        } catch(AssertionError e) {
            Set<Object> tmp = new TreeSet<>();
            for (Object x: set1) {
                if (!set2.contains(x)) {
                    tmp.add(x);
                }
            }
            for (Object x: set2) {
                if (!set1.contains(x)) {
                    tmp.add(x);
                }
            }
            throw new AssertionError("Error listTables: "+ tmp +"\n"+ e);
        }
    }

    @Test
    public void listColumns() throws PreparationException, StoppableException {
        Set<Object> set1 = new HashSet<>();
        Set<Object> set2 = new HashSet<>();

        try {
            List<Column> cs = DataAccess.listColumns(
                new Table(jsqlTableName, "0", 
                    new Database(jsqlDatabaseName, "0")
                )
            );
            List<String> columnsFound = new ArrayList<>();
            for (Column c: cs) {
                columnsFound.add(c.toString());
            }

            set1.addAll(columnsFound);
            set2.addAll(columnToFind);

            LOGGER.info("listColumns: found "+ set1 +"\nto find "+ set2 +"\n");
            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
            
        } catch(AssertionError e) {
            Set<Object> tmp = new TreeSet<>();
            for (Object x: set1) {
                if (!set2.contains(x)) {
                    tmp.add(x);
                }
            }
            for (Object x: set2) {
                if (!set1.contains(x)) {
                    tmp.add(x);
                }
            }
            throw new AssertionError("Error listColumns: "+ tmp +"\n"+ e);
        }
    }

    @Test
    public void listValues() throws PreparationException, StoppableException {
        Set<Object> set1 = new TreeSet<>();
        Set<Object> set2 = new TreeSet<>();

        try {
            String[][] vs = DataAccess.listValues(Arrays.asList(
                new Column(jsqlColumnName, 
                    new Table(jsqlTableName, "0", 
                        new Database(jsqlDatabaseName, "0")
                    )
                )
            ));
            List<String> valuesFound = new ArrayList<>();
            for (String[] v: vs) {
                valuesFound.add(v[1].replaceAll("\r\n", "\n"));
            }

            set1.addAll(valuesFound);
            set2.addAll(valueToFind);

            LOGGER.info(
                "<<listValues: found "+ 
                set1.toString()
                    .replaceAll("\n", "[n]")
                    .replaceAll("\r", "[r]") +
                "\nto find "+ 
                set2.toString()
                    .replaceAll("\n", "[n]")
                    .replaceAll("\r", "[r]") + 
                ">>\n"
            );
            Assert.assertTrue(!set1.isEmpty() && !set2.isEmpty() && set1.equals(set2));
            
        } catch(AssertionError e) {
            Set<Object> tmp = new TreeSet<>();
            for (Object x: set1) {
                if (!set2.contains(x)) {
                    tmp.add(x);
                }
            }
            for (Object x: set2) {
                if (!set1.contains(x)) {
                    tmp.add(x);
                }
            }
            throw new AssertionError("Error listValues: "+ tmp +"\n"+ e);
        }
    }
}
