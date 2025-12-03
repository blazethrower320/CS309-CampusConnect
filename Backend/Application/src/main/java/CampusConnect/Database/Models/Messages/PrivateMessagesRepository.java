package CampusConnect.Database.Models.Messages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivateMessagesRepository extends JpaRepository<PrivateMessages, Long>{
    List<PrivateMessages> findAllByUserIdAndReceiverUserId(long userId, long receiverUserId);
    List<PrivateMessages> findByUserIdOrReceiverUserId(long userId, long receiverUserId);
}
