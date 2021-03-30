package de.htwb.ai.kbe.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface IFileService {
//    public void init();

    public String save(MultipartFile file, long id);

//    File load(long id);

    public void deleteAll();

    void delete(long id);

    public Stream<Path> loadAll();
}
