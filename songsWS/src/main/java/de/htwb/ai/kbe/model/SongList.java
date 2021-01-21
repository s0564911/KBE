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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import de.htwb.ai.kbe.model.Song;
import de.htwb.ai.kbe.model.Song.Builder;


@Entity
@Table(name = "songLists")
@JsonPropertyOrder({"id", "ownerId", "name", "isPrivate", "songList"})
public class SongList {
    //ein user hat mehere songlists, eine songlist hat einen user
    // ein songlist hat mehrere songs, ein song hat mehrer songlists
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;


    @Column(name = "name")
    private String name;
    @Column(name = "isPrivate")
    private boolean isPrivate;


    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "songLists_songs", joinColumns = {@JoinColumn(name = "songListsId", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "songsId", referencedColumnName = "id")})
    private Set<Song> songList;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private User ownerId;

    public SongList() {
    }

    public SongList(Builder builder) {
        this.id = builder.id;
        this.ownerId = builder.ownerid;
        this.name = builder.name;
        this.isPrivate = builder.ispriv;
        this.songList = builder.songs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public User getOwneridObject() {
        return ownerId;
    }

    public String getOwnerId() {
        return ownerId.getUserId();
    }

    public void setOwnerId(User id) {
        this.ownerId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPriv) {
        this.isPrivate = isPriv;
    }

    public Set<Song> getSongList() {
        if (songList == null) {
            songList = new HashSet<>();
        }
        return songList;
    }

    public void setSongList(Set<Song> songSet) {
        this.songList = songSet;
    }

    public void addSong(Song song) {
        if (songList == null) {
            songList = new HashSet<>();
        }
        this.songList.add(song);
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
