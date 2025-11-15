package CampusConnect.Database.Models.Images;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class ImagesController
{
    // replace this! careful with the operating system in use
    private static String directory = "/home/target/ImageDatabase";

    @Autowired
    private ImagesRepository imageRepository;

    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getImageById(@PathVariable int id) throws IOException {
        Images image = imageRepository.findById(id);
        File imageFile = new File(image.getFilePath());
        return Files.readAllBytes(imageFile.toPath());
    }

    @PostMapping("/images")
    public String handleFileUpload(@RequestParam("image") MultipartFile imageFile) {
        try {

            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);

            Images image = new Images();
            image.setFilePath(destinationFile.getAbsolutePath());
            Images savedImage = imageRepository.save(image);

            return String.valueOf(savedImage.getId());
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }

}
