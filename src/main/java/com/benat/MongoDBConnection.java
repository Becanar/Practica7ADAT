package com.benat;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Scanner;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://<username>:<password>@<cluster>.mongodb.net/test?retryWrites=true&w=majority"; // Reemplaza con tu conexión de MongoDB Atlas
    private static final String DATABASE_NAME = "sample_airbnb";
    private static final String COLLECTION_NAME = "listingsAndReviews";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBConnection() {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(COLLECTION_NAME);
    }

    // Crear: Inserta un nuevo alojamiento con detalles personalizados
    public void insertListing(Document listing) {
        collection.insertOne(listing);
        System.out.println("--- Crear (Insertar) ---");
        System.out.println("Nuevo alojamiento insertado: " + listing.toJson());
    }

    // Leer: Consulta alojamientos por ciudad y rango de precio
    public void readListingsByCityAndPrice(String city, double minPrice, double maxPrice) {
        Bson filter = Filters.and(
                Filters.eq("city", city),
                Filters.gte("price", minPrice),
                Filters.lte("price", maxPrice)
        );
        MongoCursor<Document> cursor = collection.find(filter).iterator();
        System.out.println("--- Leer (Consultar) ---");
        while (cursor.hasNext()) {
            Document listing = cursor.next();
            System.out.println(listing.toJson());
        }
    }

    // Actualizar: Cambia el precio de un alojamiento
    public void updateListing(String id, String field, Object newValue) {
        Bson filter = Filters.eq("_id", id);
        Bson updateOperation = Updates.set(field, newValue);
        collection.updateOne(filter, updateOperation);
        System.out.println("--- Actualizar ---");
        System.out.println("Alojamiento actualizado: " + collection.find(filter).first().toJson());
    }

    // Eliminar: Elimina un alojamiento
    public void deleteListing(String id) {
        collection.deleteOne(Filters.eq("_id", id));
        System.out.println("--- Eliminar ---");
        System.out.println("Alojamiento eliminado.");
    }

    // Cerrar la conexión con la base de datos
    public void close() {
        mongoClient.close();
    }

    // Método para mostrar el menú
    public static void showMenu() {
        System.out.println("Seleccione una operación:");
        System.out.println("1. Crear un nuevo alojamiento");
        System.out.println("2. Leer alojamientos por ciudad y rango de precio");
        System.out.println("3. Actualizar un alojamiento");
        System.out.println("4. Eliminar un alojamiento");
        System.out.println("5. Salir");
    }

    public static void main(String[] args) {
        MongoDBConnection db = new MongoDBConnection();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            showMenu(); // Muestra el menú
            choice = scanner.nextInt(); // Obtiene la opción seleccionada por el usuario
            scanner.nextLine(); // Limpiar el buffer

            switch (choice) {
                case 1: // Crear
                    System.out.println("Ingrese el nombre del alojamiento:");
                    String name = scanner.nextLine();
                    System.out.println("Ingrese un resumen del alojamiento:");
                    String summary = scanner.nextLine();
                    System.out.println("Ingrese el precio del alojamiento:");
                    double price = scanner.nextDouble();
                    scanner.nextLine(); // Limpiar el buffer
                    System.out.println("Ingrese la ciudad del alojamiento:");
                    String city = scanner.nextLine();
                    System.out.println("Ingrese el número de habitaciones:");
                    int rooms = scanner.nextInt();
                    scanner.nextLine(); // Limpiar el buffer

                    // Crear el nuevo alojamiento
                    Document newListing = new Document("name", name)
                            .append("summary", summary)
                            .append("price", price)
                            .append("city", city)
                            .append("rooms", rooms);
                    db.insertListing(newListing);
                    break;

                case 2: // Leer
                    System.out.println("Ingrese la ciudad para consultar:");
                    city = scanner.nextLine();
                    System.out.println("Ingrese el precio mínimo:");
                    double minPrice = scanner.nextDouble();
                    System.out.println("Ingrese el precio máximo:");
                    double maxPrice = scanner.nextDouble();
                    scanner.nextLine(); // Limpiar el buffer

                    db.readListingsByCityAndPrice(city, minPrice, maxPrice);
                    break;

                case 3: // Actualizar
                    System.out.println("Ingrese el ID del alojamiento que desea actualizar:");
                    String id = scanner.nextLine();
                    System.out.println("Ingrese el campo que desea actualizar (por ejemplo, 'price'):");
                    String field = scanner.nextLine();
                    System.out.println("Ingrese el nuevo valor para el campo:");
                    String newValue = scanner.nextLine();

                    db.updateListing(id, field, newValue);
                    break;

                case 4: // Eliminar
                    System.out.println("Ingrese el ID del alojamiento que desea eliminar:");
                    id = scanner.nextLine();
                    db.deleteListing(id);
                    break;

                case 5: // Salir
                    System.out.println("Saliendo...");
                    db.close();
                    break;

                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (choice != 5); // Salir cuando se elija la opción 5
    }
}
