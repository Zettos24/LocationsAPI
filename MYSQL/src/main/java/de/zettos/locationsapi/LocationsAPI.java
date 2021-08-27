package de.zettos.locationsapi;

import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Map;

public final class LocationsAPI extends JavaPlugin {

    private static MySQL mySQL;
    private static String tableName;

    public static void connect(String host, int port, String database, String username, String password, String tablename){
        tableName = tablename;
        mySQL = new MySQL();

        mySQL.setCredentials(host,port,database,username,password);

        mySQL.connect(() -> {
            mySQL.createTable(tablename,"ID Text, Location Text");
        });
    }

    public static void disconnect(){
        mySQL.disconnect();
    }

    @SneakyThrows
    public static Location getLocation(String ID){
        String locString = "";
        ResultSet rs = mySQL.query("SELECT Location FROM "+tableName+" WHERE ID = '"+ID+"';");
        if(rs.next()) locString = rs.getString("Location");
        return Location.deserialize(deserialize(Base64.getDecoder().decode(locString)));
    }

    @SneakyThrows
    public static void saveLocation(String ID, Location location){
        mySQL.update("DELETE FROM "+tableName+" WHERE ID = '"+ID+"'");
        String loc = Base64.getEncoder().encodeToString(serialize(location.serialize()));
        mySQL.update("INSERT INTO "+tableName+" (ID,Location) VALUES ('"+ID+"', '"+loc+"')");
    }

    @SneakyThrows
    public static void deleteLocation(String ID){
        mySQL.update("DELETE FROM "+tableName+" WHERE ID = '"+ID+"'");
    }


    @SneakyThrows
    private static Map<String,Object> deserialize(byte[] b) {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        Object o = objectInputStream.readObject();

        byteArrayInputStream.close();
        objectInputStream.close();

        return (Map<String, Object>) o;
    }

    @SneakyThrows
    private static byte[] serialize(Map<String,Object> locString) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream (bout);
        out.writeObject (locString);
        out.flush ();
        byte[] bytes = bout.toByteArray ();
        bout.close ();
        out.close ();

        return bytes;
    }

    public static String getTableName() {
        return tableName;
    }
}
