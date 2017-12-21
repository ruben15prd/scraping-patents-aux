package web.scraping.jsoup;

public class Area {

	private String nombre;
	private String enlace;
	
	public Area(String nombre, String enlace) {
		super();
		this.nombre = nombre;
		this.enlace = enlace;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEnlace() {
		return enlace;
	}
	public void setEnlace(String enlace) {
		this.enlace = enlace;
	}
	
	@Override
	public String toString() {
		return "Area [nombre=" + nombre + ", enlace=" + enlace + "]";
	}
}
