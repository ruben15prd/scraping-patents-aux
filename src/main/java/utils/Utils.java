package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Utils {

	
	
	
	public static void interseccionPatentes() {

		// Creating a Mongo client
		MongoClientURI uri = new MongoClientURI("mongodb://rrv:rrv@ds255455.mlab.com:55455/si1718-rrv-patents");
		MongoClient client = new MongoClient(uri);
		
		System.out.println("------------------------------------------------------");
		System.out.println("aaaaa");

		MongoDatabase db = client.getDatabase(uri.getDatabase());

		MongoCollection<Document> patentsAux = db.getCollection("patentsAux");
		
		MongoCollection<Document> patents = db.getCollection("patents");
		
		
		Set<Document> patsAux = (Set<Document>) patentsAux.find().into(new HashSet<Document>());
		
		Set<Document> pats = (Set<Document>) patents.find().into(new HashSet<Document>());
		
		Set<Document> newPatents = new HashSet<Document>();
		List<Document> listNewPatents = new ArrayList<Document>();
		
		
		
		
		newPatents = Utils.difference(patsAux, pats);
		
		
		
		listNewPatents.addAll(newPatents);
		
		Bson filter4 = new Document();
		patentsAux.deleteMany(filter4);
		System.out.println("------------------------------------------------------");
		System.out.println("Hacemos la diferencia de las patentes");

		patentsAux.insertMany(listNewPatents);
		System.out.println("Hecho");
		client.close();

	}
	
	
	 public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
		    Set<T> tmp = new TreeSet<T>(setA);
		    tmp.removeAll(setB);
		    return tmp;
		  }
	

}
