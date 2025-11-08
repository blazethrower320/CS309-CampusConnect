package CampusConnect.WebSockets.Push;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testpush")
public class PushTestController {

    @GetMapping("/{tutorId}")
    public String testPush(@PathVariable long tutorId, @RequestParam String msg) {
        PushSocket.sendNotificationToTutor(tutorId, msg);
        return "Sent push to tutor " + tutorId;
    }
}
