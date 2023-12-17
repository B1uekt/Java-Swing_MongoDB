package Mongodb;

import com.mongodb.*;

public class MongoDBManager {
    private MongoClient mongoClient;
    private DB database;

    public MongoDBManager(String dbName) {
        try {
            mongoClient = new MongoClient("localhost", 27017);
            System.out.println("Kết nối thành công đến MongoDB!");
            database = mongoClient.getDB(dbName);
        } catch (Exception ex) {
            System.err.println("Lỗi kết nối đến MongoDB: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public DBCollection getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Đã đóng kết nối đến MongoDB!");
        }
    }
}
