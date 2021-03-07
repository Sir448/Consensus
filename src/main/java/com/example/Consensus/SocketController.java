package com.example.Consensus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.sql.Types;
import java.util.*;


@Controller
public class SocketController {

//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(HelloMessage message) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        System.out.println("Test");
//        return new Greeting("Hello, " + message.getName() + "!");
//    }
    

//    @MessageMapping("/hello/{test}")
//    @SendTo("/topic/greetings")
//    public Map<String, Object> test(Map<String, Object> message, @DestinationVariable int test) throws Exception {
//        System.out.println("function: test");
////        Thread.sleep(1000); // simulated delay
//        System.out.println(test);
//        System.out.println(message);
////        return new Greeting("Hello, " + message.getName() + "!");
//        Map<String, Object> output = new HashMap<>();
//        output.put("content", "this is a test");
//        return output;
//    }

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MessageMapping("/hello/{test}")
    public void test(Map<String, Object> message, @DestinationVariable int test) throws Exception {
        System.out.println("function: test");
        System.out.println(test);
        System.out.println(message);
        Map<String, Object> output = new HashMap<>();
        this.simpMessagingTemplate.convertAndSend("/topic/greetings/"+test, output);
        output.put("content", "this is a test");
//        return output;
    }

    @MessageMapping("/changeSettings/session/{id}")
    public void changeSettings(Map<String, Object> message, @DestinationVariable int id) throws Exception {
        System.out.println("function: changeSettings");
        System.out.println(message);
        if(message.containsKey("Decision_Type")){
            int decision_type = Integer.parseInt((String) message.get("Decision_Type"));
            Map<String, Object> session = jdbcTemplate.queryForMap("UPDATE SESSIONS SET Decision_Type = ?, Majority = ? OUTPUT INSERTED.* WHERE Session_ID = ?",decision_type, decision_type == 1 ? Integer.parseInt((String) message.get("Majority")) : null, id);
            this.simpMessagingTemplate.convertAndSend("/topic/session/"+id, session);
        }
    }

    @MessageMapping("/addOptions/session/{id}")
    public void addOption(Map<String, String[]> message, @DestinationVariable int id){

        Integer status = jdbcTemplate.queryForObject("SELECT Session_Status FROM Consensus.dbo.SESSIONS WHERE Session_ID = ?", new Object[] {id}, new int[] {Types.INTEGER}, Integer.class);
        if(1==status){
            return;
        }

        List<String> options = jdbcTemplate.queryForList("SELECT Name FROM Consensus.dbo.OPTIONS WHERE Session_ID = ?", new Object[] {id}, new int[] {Types.INTEGER},String.class);

        String query = "INSERT INTO Consensus.dbo.OPTIONS(Name, approvalCount, rejectionCount, Session_ID) VALUES";
        for(String item : message.get("options")) {
            if(!options.contains(item)){
                query += String.format(" (\'%s\', 0, 0, %d),",item,id);
            }
        }

        if(query.length() == 89){
            return;
        }

        query = query.substring(0,query.length() - 1);
        System.out.println(query);
        jdbcTemplate.update(query);
    }

/*
* Send to different addresses for different functions
* Change Settings
* Start session
* Make choice
* End/leave session
* Add option
*
*
*
*
* Change settings
*   Get new settings
*   Set them in database
*   Broadcast to everyone
*
* Start Session
*   Check if there are enough ppl
*   Check if there are enough options
*   Send list of options to everyone and tell users to start choosing
*
* Make Choice
*   Update in database
*   Check if any option meets requirements
*       If so, broadcast to everyone then delete the session and it's options and make users leave the session
*
* End/leave session
*   Reduce user count
*   If user is not host, set their session ID to null
*   If user is host, kick everyone and delete session and options
*       If session had started, check which option would've won and broadcast that
*   Broadcast session ending
*
* Add option
*   If host creates session based on premade option set, send that when starting the session
*   Receive list of options
*   Add to database
*   Only add if option isn't already in database
*
* Delete option
*   Receive list of options to delete
*   Remove from database
* */
//    @SubscribeMapping("/topic/greetings")


//    https://stackoverflow.com/questions/54763261/how-to-send-custom-message-to-custom-user-with-spring-websocket
//    https://stackoverflow.com/questions/29085791/does-spring-subscribemapping-really-subscribe-the-client-to-some-topic
/*
*Session
* Session Id - ID
* Host Id - ID of hosting user
* User Count - Number of Users in the session
* Session Status - Whether the session is waiting or in progress (if the session is finished, it will send everyone a notification then delete itself
*   0 = waiting
*   1 = in progress
* Decision_Type - How to decide which option to choose
*   0 = Consensus
*   1 = Equal to or above majority
*   2 = Highest approval rating out of all
* Majority - Number of users it takes to be in the majority (see decision type 1)
*
*Users
* User Id - Id
* Session Id - Session the user is currently in
*
*Options
* Option Id - Id
* Name - Name of option to display
* Approval Count - Number of users that approved the option
* Rejection Count - Number of users that rejected the option
* Session Id - Session this option is a part of
*
*
*
* */

}
