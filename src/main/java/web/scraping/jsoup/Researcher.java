package web.scraping.jsoup;

public class Researcher {
	
	private String ORCIDOrId;
	
	
	public Researcher(String oRCIDOrId) {
		super();
		ORCIDOrId = oRCIDOrId;
	}
	
	public String getORCIDOrId() {
		return ORCIDOrId;
	}

	public void setORCIDOrId(String oRCIDOrId) {
		ORCIDOrId = oRCIDOrId;
	}

	@Override
	public String toString() {
		return "Researcher [ORCIDOrId=" + ORCIDOrId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ORCIDOrId == null) ? 0 : ORCIDOrId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Researcher other = (Researcher) obj;
		if (ORCIDOrId == null) {
			if (other.ORCIDOrId != null)
				return false;
		} else if (!ORCIDOrId.equals(other.ORCIDOrId))
			return false;
		return true;
	}
	
	

	
	
	
	

}
