package CampusConnect.WebSockets.Push;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushRepository extends JpaRepository<Push, Long>{
    List<Push> findAllByTutorIdAndReadFalseOrderByCreatedAtAsc(long tutorId);
}
