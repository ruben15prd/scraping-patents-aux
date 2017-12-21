package web.scraping.jsoup;

public class ImgInfo {
	private String src;

	public ImgInfo(String src) {
		super();
		this.src = src;
	}
	
	public ImgInfo() {
		super();
	}
	
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	@Override
	public String toString() {
		return "ImgInfo [src=" + src + "]";
	}

}
