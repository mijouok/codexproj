package com.example.pac;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class PacController {

    @Value("${pac.file}")
    private String pacFile;

    @GetMapping(value = "/proxy.pac")
    public ResponseEntity<byte[]> pac() throws IOException {
        byte[] body = Files.readString(Path.of(pacFile), StandardCharsets.UTF_8)
                .getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/x-ns-proxy-autoconfig; charset=utf-8"));

        // Disable caching (Windows can cache PAC aggressively)
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(body);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
