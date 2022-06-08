package main;

import main.dto.DtoMessage;
import main.dto.DtoUser;
import main.dto.MessageMapper;
import main.dto.UserMapper;
import main.model.Message;
import main.model.User;
import main.repository.MessageRepository;
import main.repository.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;


    @GetMapping("/init")
    public HashMap<String, Boolean> init() {

        HashMap<String, Boolean> response = new HashMap<>();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Optional<User> userOptional = userRepository.findBySessionId(sessionId);
        response.put("result", userOptional.isPresent());

        return response;
    }

    @PostMapping("/auth")
    public HashMap<String, Boolean> auth(@RequestParam String name){

        HashMap<String, Boolean> response = new HashMap<>();
        if (!Strings.isNotEmpty(name)){
            response.put("result", false);
            return response;
        }

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        User user = new User();
        user.setName(name);
        user.setSessionId(sessionId);
        userRepository.save(user);
        response.put("result", true);

        return response;
    }

    @PostMapping("/message")
    public Map<String, Boolean> sendMessage(@RequestParam String message) {

        if (Strings.isEmpty(message)){
            return Map.of("result", false);
        }

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        User user = userRepository.findBySessionId(sessionId).get();

        Message msg = new Message();
        msg.setMessage(message);
        msg.setLocalDateTime(LocalDateTime.now());
        msg.setId(user.getId());
        msg.setUser(user);
        messageRepository.saveAndFlush(msg);

        return Map.of("result", true);
    }
    @GetMapping("/message")
    public List<DtoMessage> getMessagesList() {

        return messageRepository
                .findAll(Sort.by(Sort.Direction.ASC, "localDateTime"))
                .stream()
                .map(MessageMapper::map)
                .collect(Collectors.toList());
    }


    @GetMapping("/user")
    public List<DtoUser> getUsersList() {

        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::map)
                .collect(Collectors.toList());
    }
}
