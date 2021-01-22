package de.htwb.ai.kbe.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/version")
public class GitVersionController {

    private String gitVersion;

    public GitVersionController() {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("git.properties")) {
            this.gitVersion = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            this.gitVersion = "Could not load git.properties, check logs!";
            e.printStackTrace();
        }
    }

    @GetMapping
    public ResponseEntity<String> getGitProperties() throws IOException {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(this.gitVersion);
    }
}
