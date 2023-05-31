package de.yatta.guides.oauth2.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sample")
@Slf4j
public class SampleController {
    @GetMapping
    public ResponseEntity<String> samplePage() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return ResponseEntity.ok("Sample page");
    }
}
