package de.htwb.ai.kbe.controller;

import de.htwb.ai.kbe.model.FileModel;
import de.htwb.ai.kbe.service.FileService;
import de.htwb.ai.kbe.utils.AuthUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/files")
public class FileController {

    private final DiscoveryClient discoveryClient;
    private final FileService fileService;

    public FileController(DiscoveryClient discoveryClient, FileService fileService) {
        this.discoveryClient = discoveryClient;
        this.fileService = fileService;
    }

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    //post
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.POST,
            consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> addFile(
            @RequestHeader(value = "Authorization", defaultValue = "") String token,
            @PathVariable("id") long id,
            @RequestParam MultipartFile file) {

        String user = AuthUtils.authorize(token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // check if there is meta data on this song, if it exists
        try {
            String url = "http://localhost:8080/songs/" + id;
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", token);

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            String location = fileService.save(file, id);
            if (location.equals("File already exists."))
                return new ResponseEntity<>(location, HttpStatus.CONFLICT);
            headers.add("Location", location);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    //put
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> updateFile(
            @RequestHeader(value = "Authorization", defaultValue = "") String token,
            @PathVariable("id") long id,
            @RequestParam MultipartFile file) {

        String user = AuthUtils.authorize(token);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            String location = fileService.update(file, id);
            if (location.equals("Directory does not exist.") || location.equals("File does not exist."))
                return new ResponseEntity<>(location, HttpStatus.NOT_FOUND);
            headers.add("Location", location);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    //delete
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFiles(
            @RequestHeader(value = "Authorization", defaultValue = "") String token,
            @PathVariable("id") long id) {
        if (AuthUtils.authorize(token) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        fileService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //deleteAll - kill switch
    @RequestMapping(
            method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllFiles(@RequestHeader(value = "Authorization", defaultValue = "") String token) {
        if (AuthUtils.authorize(token) == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        fileService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //get
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE)
    public @ResponseBody
    ResponseEntity getFile(
            @RequestHeader(value = "Authorization", defaultValue = "") String token,
            @PathVariable("id") long id) {
        if (AuthUtils.authorize(token) == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        File resourceFile = null;
        byte[] out = null;
        String type = "";
        String[] arr = null;
        try {
            File dir = new File("files" + File.separator + id);
            if (!dir.isDirectory())
                return new ResponseEntity<>("File Not Found", HttpStatus.NOT_FOUND);
            arr = dir.list();
            if (arr != null && arr.length > 0)
                resourceFile = new File(dir + File.separator + arr[0]);

            type = resourceFile.toURL().openConnection().guessContentTypeFromName(arr[0]);

            InputStream inputStream = new FileInputStream(dir + File.separator + arr[0]);
            out = org.apache.commons.io.IOUtils.toByteArray(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (out != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("content-disposition", "attachment; filename=" + arr[0]);
            headers.add("Content-Type", type);

            return new ResponseEntity<>(out, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("File Not Found", HttpStatus.NOT_FOUND);
        }
    }

    //getAll - dirty
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE)
    public @ResponseBody
    ResponseEntity getFile(@RequestHeader(value = "Authorization", defaultValue = "") String token) {
        if (AuthUtils.authorize(token) == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // get all files from service
        List<FileModel> fileInfos = fileService.loadAll().map(path -> {
            String path_ = path.toString().replace("\\", "/"); // minor correction for hyperlinks

            String url = MvcUriComponentsBuilder.fromMethodName(FileController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileModel(path_, url);
        }).collect(Collectors.toList());

        // patch them into a string for response - ONLY FOR OVERVIEW OF FILESYSTEM
        StringBuilder response = new StringBuilder();
        for (FileModel filemodel : fileInfos) {
            if (filemodel != null) {
                if (filemodel.getName().length() <= 5)
                    response.append(filemodel.getUrl()).append(filemodel.getName()).append(System.lineSeparator());
                else
                    response.append("    ").append(filemodel.getName().split("/")[1]).append(System.lineSeparator());
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
