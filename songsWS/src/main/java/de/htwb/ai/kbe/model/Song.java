package de.htwb.ai.kbe.model;

import javax.persistence.*;

@Entity
@Table(name="songs")
public class Song {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Override
    public String toString() {
        return "Song [id=" + id + ", title=" + title + ", artist=" + artist + ", label=" + label + ", released="
                + released + "]";
    }


    @Column(name="title")
    private String title;

    @Column(name="artist")
    private String artist;

    @Column(name="label")
    private String label;

    @Column(name="released")
    private String released;

    public Song() {}

    public Song(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.artist = builder.artist;
        this.label = builder.label;
        this.released = builder.released;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getReleased() { return released; }

    public void setReleased(String released) {
        this.released = released;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int id;
        private String songId;
        private String title;
        private String artist;
        private String label;
        private String released;

        private Builder() {
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder withLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder withReleased(String released) {
            this.released = released;
            return this;
        }

        public Song build() {
            return new Song(this);
        }
    }
}
