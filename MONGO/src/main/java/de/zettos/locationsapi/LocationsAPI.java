package de.zettos.locationsapi;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Map;

public final class LocationsAPI extends JavaPlugin {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;

    @SneakyThrows
    public static void connect(String host, int port, String username, String password, String databasename, String collectionname){

        if(username.isEmpty() && password.isEmpty())
            mongoClient = MongoClients.create("mongodb://"+host+":"+port);
        else mongoClient = MongoClients.create("mongodb://"+username+":"+password+"@"+host+":"+port);
        database = mongoClient.getDatabase(databasename);
        collection = database.getCollection(collectionname);
    }

    public static void disconnect(){
        mongoClient.close();
    }

    public static void saveLocation(String ID, Location location){
        collection.insertOne(new Document("_id", ID).append("location", Base64.getEncoder().encodeToString(serialize(location.serialize()))));
    }

    public static void deleteLocation(String ID){
        collection.deleteOne(Filters.eq("_id", ID));
    }

    public static Location getLocation(String ID){
        Document document = collection.find(Filters.eq("_id", ID)).first();
        String locString = document.get("location").toString();
        return Location.deserialize(deserialize(Base64.getDecoder().decode(locString)));
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

}
