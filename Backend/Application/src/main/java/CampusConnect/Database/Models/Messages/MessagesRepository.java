package CampusConnect.Database.Models.Messages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Messages, Long>
{
    List<Messages> findBySessionId(long sessionId);
}


