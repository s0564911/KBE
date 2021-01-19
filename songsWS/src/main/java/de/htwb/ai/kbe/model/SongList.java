package de.htwb.ai.kbe.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.Song.Builder;




@Entity
@Table(name="songLists")
public class SongList {
	//ein user hat mehere songlists, eine songlist hat einen user
	// ein songlist hat mehrere songs, ein song hat mehrer songlists
	@Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
	
	
	 @Column(name="name") private String name;
     @Column(name="priv") private boolean isPrivate;
	

	@OneToMany( 
			targetEntity=Song.class,
            cascade=CascadeType.ALL, 
            orphanRemoval=true, 
            fetch = FetchType.EAGER)
	@JoinColumn(name = "title",referencedColumnName = "id")//putting name=id fetches songs of that id
    private Set<Song> songSet;
	
	@ManyToOne
    @JoinColumn(name = "ownerId")	
    private User ownerId;

	public SongList() {}

    public SongList(Builder builder) {
        this.id=builder.id;
        this.ownerId=builder.ownerid;
        this.name=builder.name;
        this.isPrivate=builder.ispriv;
        this.songSet=builder.songs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getOwneridString() {
    	return ownerId.getUserId();
    }
    public User getOwnerid() {
        return ownerId;
    }

    public void setOwnerid(User id) {
        this.ownerId = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
	public boolean getIspriv() {
		return isPrivate;
	}
	public  void setIspriv(boolean isPriv) {
		this.isPrivate=isPriv;
	}
	public Set<Song> getSongSet() {
        if(songSet == null) {
            songSet = new HashSet<>();
        }
        return songSet;
    }
	public void setSongSet(Set<Song> songSet) {
        this.songSet = songSet;
	}
	public void addSong(Song song) {
        if(songSet == null) {
            songSet = new HashSet<>();
        }
        this.songSet.add(song);
    }
	 public static Builder builder() {
	        return new Builder();
	    }

	 public static final class Builder {
	        private int id;
	        private User ownerid;
	        private String name;
	        private boolean ispriv;
	        private Set<Song> songs;

	        private Builder() {
	        }

	        public Builder withId(int id) {
	            this.id = id;
	            return this;
	        }

	        public Builder withName(String name) {
	            this.name = name;
	            return this;
	        }

	        public Builder withOwnerid(User user) {
	            this.ownerid = user;
	            return this;
	        }

	        public Builder withIsPriv(boolean ispriv) {
	            this.ispriv = ispriv;
	            return this;
	        }

	        public Builder withSongs(Set<Song> songs) {
	            this.songs = songs;
	            return this;
	        }

	        public SongList build() {
	            return new SongList(this);
	        }
	    }
	
	
	
}
