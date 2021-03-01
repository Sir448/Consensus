package com.example.Consensus;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Controller
public class ConsensusController {

//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(HelloMessage message) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        System.out.println("Test");
//        return new Greeting("Hello, " + message.getName() + "!");
//    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Map<String, Object> test(Map<String, Object> message) throws Exception {
//        Thread.sleep(1000); // simulated delay

        System.out.println(message);
//        return new Greeting("Hello, " + message.getName() + "!");
        Map<String, Object> test = new HashMap<>();
        test.put("content", "this is a test");
        return test;
    }

/*
* Send to different addresses for different functions
* Change Settings
* Start session
* Make choice
* End/leave session
*
* */
//    @SubscribeMapping("/topic/greetings")


//    https://stackoverflow.com/questions/54763261/how-to-send-custom-message-to-custom-user-with-spring-websocket

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
