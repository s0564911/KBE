package de.htwb.ai.kbe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "songLists")
@JsonPropertyOrder({"id", "ownerId", "name", "isPrivate", "songList"})
public class SongList {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;


    @Column(name = "name")
    private String name;
    @Column(name = "isPrivate")
    private boolean isPrivate;


    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinTable(name = "songLists_songs",
            joinColumns = @JoinColumn(name = "songListsId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "songsId", referencedColumnName = "id"))
    private Set<Song> songList;

//    @ManyToOne
//    @JoinColumn(name = "ownerId")
    @Column
    private String ownerId;

    public SongList() {
    }

    public SongList(Builder builder) {
        this.id = builder.id;
        this.ownerId = builder.ownerId;
        this.name = builder.name;
        this.isPrivate = builder.isPrivate;
        this.songList = builder.songs;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public String getOwnerIdObject() {
        return ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String id) {
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

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
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
        private long id;
        private String ownerId;
        private String name;
        private boolean isPrivate;
        private Set<Song> songs;

        private Builder() {
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withOwnerId(String user) {
            this.ownerId = user;
            return this;
        }

        public Builder withIsPrivate(boolean isPrivate) {
            this.isPrivate = isPrivate;
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
