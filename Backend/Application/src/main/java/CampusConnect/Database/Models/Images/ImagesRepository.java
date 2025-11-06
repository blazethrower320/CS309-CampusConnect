package CampusConnect.Database.Models.Images;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagesRepository extends JpaRepository<Images, Long> {
    Images findById(int id);
}

