package web.scraping.jsoup;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import utils.Utils;

public class Programacion {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	public static final String URL_BASE = "http://investigacion.us.es/sisius/sisius.php";
	private static final String URL_QUERY = "http://investigacion.us.es";

	public void beepForAnHour() {
		final Runnable beeper = new Runnable() {
			public void run() {
				System.out.println("beep");
				
				
				

				List<String> researchersLink = new ArrayList<>();
				
				org.jsoup.nodes.Document researchers = null;
				try {
					researchers = Jsoup.connect(URL_BASE).data("text2search", "%%%").data("en", "1")
							.data("inside", "1").maxBodySize(10 * 1024 * 1024).post();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				Elements elements = researchers.select("td.data a");
				int i = 0;

				for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {

					Element researcher = iterator.next();

					if (i % 2 != 1) {

						String link = researcher.attr("href");

						if (link.contains("sis_showpub.php")) {

							researchersLink.add(link);
						}
					}

					i++;
				}

				Map<Patent, List<Researcher>> patentsMap = new HashMap<Patent, List<Researcher>>();

				// Entramos en cada pagina de cada investigador

				for (String link : researchersLink) {

					org.jsoup.nodes.Document doc = null;
					try {
						doc = Jsoup.parse(new URL(URL_QUERY + link), 10000);
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// Document doc = Jsoup.parse(new
					// URL("https://investigacion.us.es/sisius/sis_showpub.php?idpers=4051"),
					// 10000);

					Element filtered = doc.getElementsByTag("p").get(0);
					Elements filtered2 = filtered.getElementsByTag("a");
					String ORCIDOrId = null;

					// Obtenemos el ORCIDOrId
					for (Element e : filtered2) {
						if (e.attr("href").contains("https://orcid.org/")) {
							ORCIDOrId = e.text();
						}

					}
					// Si no tiene ORCID le asociamos la id que aparece en la url
					if (ORCIDOrId == null) {
						String[] urlSplit = link.split("=");
						ORCIDOrId = urlSplit[1];

					}

					// Ahora sacamos los datos de las patentes

					Elements filtered3 = doc.body().select("div[align]");
					boolean dentroPatentes = false;
					boolean finalizadasPatentes = false;
					boolean primeraIteracion = true;
					// System.out.println(filtered3);
					// Obtenemos los elements que estan entre patentes

					for (String s : filtered3.toString().split("\\n")) {
						if (s.contains("Patentes:") && dentroPatentes == false) {

							// Entro en las patentes
							dentroPatentes = true;
							primeraIteracion = false;
							System.out.println("---------------------------------------------------------------------------");
							System.out.println("Patentes del investigador con id: " + ORCIDOrId);

						}
						if (dentroPatentes == true && finalizadasPatentes == false) {
							String noHtml = s.replaceAll("\\<.*?>", "");

							if (noHtml.length() > 3 && primeraIteracion == true && noHtml.contains("Solicitud:")) {

								String[] split = noHtml.split("Solicitud:");
								String titulo = split[0].trim();
								String fecha = split[1].trim();

								String idPatent = generaIdPatent(titulo, fecha);
								//System.out.println(idPatent);

								Patent patent = new Patent(titulo, fecha, idPatent);
								System.out.println(patent);
								Researcher researcher = new Researcher(ORCIDOrId);

								// Añadimos los elementos al map
								List<Researcher> patents;
								if (patentsMap.containsKey(patent)) {
									// if the key has already been used,
									// we'll just grab the array list and add the value to it
									patents = patentsMap.get(patent);
									patents.add(researcher);

								} else {
									// if the key hasn't been used yet,
									// we'll create a new ArrayList<String> object, add the value
									// and put it in the array list with the new key
									patents = new ArrayList<Researcher>();
									patents.add(researcher);
									patentsMap.put(patent, patents);

								}

							}

							if (s.contains("</div>") || s.contains("<div>")
									|| s.contains("<h3>") && !primeraIteracion == false) {
								finalizadasPatentes = true;
								// System.out.println("terminado");
							}
						}
						primeraIteracion = true;

					}

					// break;
				}

				// Mostramos los elementos del map
				for (Entry<Patent, List<Researcher>> entry : patentsMap.entrySet()) {
					// System.out.println(entry.getKey() + ", " + entry.getValue());
				}

				// Persistimos los elementos del map en la base de datos en mongo

				// Creating a Mongo client
				MongoClientURI uri = new MongoClientURI("mongodb://rrv:rrv@ds255455.mlab.com:55455/si1718-rrv-patents");

				MongoClient client = new MongoClient(uri);

				MongoDatabase db = client.getDatabase(uri.getDatabase());

				// Retrieving a collection

				MongoCollection<Document> patentsAux = db.getCollection("patentsAux");

				// Note that the insert method can take either an array or a document.
				List<String> researchers2 = new ArrayList<String>();

				Researcher r1 = new Researcher("9999-9999-9999-9999");

				Researcher r2 = new Researcher("0000-0000-0000-0000");

				researchers2.add(r1.getORCIDOrId());
				researchers2.add(r2.getORCIDOrId());

				Patent p1 = new Patent("prueba", "10-10-2010", "prueba10-10-2010");

				// Test
				/*
				 * Document docu2 = new Document() .append("title", p1.getTitle())
				 * .append("date", p1.getDate()) .append("inventors", researchers2);
				 * patents.insertOne(docu2);
				 */

				// Insertamos el map en la bd
				/*
				DBObject document1 = new BasicDBObject();
				document1.put("name", "Kiran");
				document1.put("age", 20);

				DBObject document2 = new BasicDBObject();
				document2.put("name", "John");

				List<Document> documents = new ArrayList<>();
				documents.add(document1);
				documents.add(document2);
				collection.insert(documents);
				*/
				List<Document> documents = new ArrayList<>();

				for (Entry<Patent, List<Researcher>> entry : patentsMap.entrySet()) {

					List<Researcher> inventors = entry.getValue();
					List<String> idInventors = new ArrayList<String>();

					// Cogemos todas ORDICOrID de los researchers y los metemos en una lista de
					// string

					for (Researcher inv : inventors) {
						idInventors.add(inv.getORCIDOrId());
					}
					// Creamos el document
					List<String> keywords = new ArrayList<String>();
					
					// Generamos las keywords
					
					
					
					
					
					String[] titleSplit = entry.getKey().getTitle().split("\\.");
					
					
					List<String> titleList = Arrays.asList(titleSplit).subList(0, titleSplit.length -1);
					
					for(String kw: titleList) {
						String[] k = kw.split(" ");
						for(String s1 :k) {
							s1.replaceAll("[^a-zA-Z]+","").trim();
							if(s1.length() > 4) {
								keywords.add(s1);
							}
							
						}
						
					}
					
					
					
					
					System.out.println("Creando el documento: " + "Titulo: " + entry.getKey().getTitle() + " Fecha: "
							+ entry.getKey().getDate() + " IdPatent: "+ entry.getKey().getTitle() + " Investigadores: " + idInventors + " Keywords: " + keywords);
					
					/*
					DBObject docu= new BasicDBObject();
					docu.put("idPatent", entry.getKey().getIdPatent());
					docu.put("title", entry.getKey().getTitle());
					docu.put("date", entry.getKey().getDate());
					docu.put("inventors", idInventors);
					*/
					
					Document docu = new Document().append("idPatent", entry.getKey().getIdPatent()).append("title", entry.getKey().getTitle())
							.append("date", entry.getKey().getDate()).append("inventors", idInventors).append("keywords", keywords);
					documents.add(docu);
					
					
					/*
					Document docu = new Document().append("idPatent", entry.getKey().getIdPatent()).append("title", entry.getKey().getTitle())
							.append("date", entry.getKey().getDate()).append("inventors", idInventors);
					
					patents.insertOne(docu);
					*/
				}
				
				
				Bson filter = new Document();
				patentsAux.deleteMany(filter);
				
				patentsAux.insertMany(documents);
				
				
				
				
				client.close();
				
				//Utils.interseccionPatentes();

				// System.out.println("Document inserted successfully");

			}

		


				
			
		};
		final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 60*60*24, SECONDS);
		/*
		scheduler.schedule(new Runnable() {
			public void run() {
				beeperHandle.cancel(true);
			}
		}, 999999999 * 99999999, SECONDS);
		*/
	}
public static String generaIdPatent(String titulo, String fecha) {
	String tituloFormat = titulo.trim().toLowerCase().replace("/[^a-zA-Z ]/g", "")
			.replaceAll("[-\\[\\]^/,'*:.!><~@#$%+=?¿|\"\\\\()]+", "").replaceAll(" ", "")
			.replaceAll("[^\\p{ASCII}]", "").replaceAll("/", "");

	String fechaFormat = fecha.trim().replaceAll("[/-]", "");

	return tituloFormat + fechaFormat;
}

}
