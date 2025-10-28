package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/images")
public class ImageController {

    // ✅ Store uploads inside a safe folder in your project
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    @Autowired
    private ImageRepository imageRepository;

    // 📸 GET an image by ID
    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageById(@PathVariable int id) {
        try {
            Image image = imageRepository.findById(id);
            if (image == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            File imageFile = new File(image.getFilePath());
            if (!imageFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 🧾 GET all image entries (for testing / debugging)
    @GetMapping("/all")
    public List<String> listAllImages() {
        return imageRepository.findAll()
                .stream()
                .map(img -> "ID: " + img.getId() + " | Path: " + img.getFilePath())
                .collect(Collectors.toList());
    }

    // 📤 POST upload an image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> handleFileUpload(@RequestParam("image") MultipartFile imageFile) {
        try {
            // Ensure upload directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file to disk
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            File destinationFile = new File(UPLOAD_DIR + File.separator + fileName);
            imageFile.transferTo(destinationFile);

            // Save info in database
            Image image = new Image();
            image.setFilePath(destinationFile.getAbsolutePath());
            imageRepository.save(image);

            System.out.println("✅ File uploaded: " + destinationFile.getAbsolutePath());

            return ResponseEntity.ok("File uploaded successfully! ID: " + image.getId() +
                    " | Path: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Failed to upload file: " + e.getMessage());
        }
    }
}
