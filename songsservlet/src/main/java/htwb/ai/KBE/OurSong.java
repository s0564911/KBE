package htwb.ai.KBE;

public class OurSong {

	private Integer id;
	private String title;
	private String artist;
	private String label;
	private Integer released;

	public OurSong() {}

	public OurSong(Integer id, String title, String artist, String label, Integer released) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.label = label;
        this.released = released;
    }
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
		return title;
	}
	
    public void setTitle(String title) {
        this.title = title;
    }

	public String getArtist() {
		return artist;
	}
	
	public void setArtist(String artist) {
        this.artist = artist;
    }

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
        this.label = label;
    }

	public Integer getReleased() {
		return released;
	}

	public void setReleased(Integer released) {
        this.released = released;
    }
    
    @Override
    public String toString() {
        return "OurSong [id=" + id + ", title=" + title + ", artist=" + artist + ", label=" + label + ", released="
                + released + "]";
    }
}
