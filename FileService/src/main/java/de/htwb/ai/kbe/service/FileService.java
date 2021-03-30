package de.htwb.ai.kbe.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService implements IFileService {

    private final Path root = Paths.get("files");

    @Override
    public String save(MultipartFile file, long id) {
        try {
            byte[] bytes = file.getBytes();

            // Creating the directory to store file
            File dir = new File("files" + File.separator + id);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Unable to create folder.");
            }


            String location = dir.getAbsolutePath() + File.separator + file.getOriginalFilename();
            File serverFile = new File(location);

            if (serverFile.exists())
                return "File already exists.";

            // Create the file on server
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
            return location.split("SongsMS")[1];

        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public String update(MultipartFile file, long id) {
        try {
            byte[] bytes = file.getBytes();

            // Creating the directory to store file
            File dir = new File("files" + File.separator + id);
            if (!dir.exists()) {
                return "Directory does not exist.";
            }

            String location = dir.getAbsolutePath() + File.separator + file.getOriginalFilename();
            File serverFile = new File(location);

            if (serverFile.exists()) {
                // Override the file on server
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();
                return location.split("SongsMS")[1];
            } else {
                return "File does not exist.";
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        try {
            FileUtils.deleteDirectory(new File("files"));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete all files. Error: " + e.getMessage());
        }
    }

    @Override
    public void delete(long id) {
        try {
            FileUtils.deleteDirectory(new File("files" + File.separator + id));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete all files. Error: " + e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 2)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load all files.");
        }
    }
}