package web.scraping.jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TestJsoupScholar {
	
	private static final String URL_BASE = "https://scholar.google.com/";
	private static final String URL_SCHOLAR = URL_BASE + "citations?user=";

	public static ImgInfo getImgInfoFromGoogleScholar(String id) throws MalformedURLException, IOException {
		Document doc = Jsoup.parse(new URL(URL_SCHOLAR), 1000);
		/*
		String idPablo = "y2qDI6IAAAAJ";
		String idJorge = "0719x-wAAAAJ";
		*/
		ImgInfo aux = new ImgInfo();
		
		aux.setSrc(doc.getElementsByAttributeValue("id","gsc_prf_pup-img").attr("src"));
	
		return aux;
	}
	
}
