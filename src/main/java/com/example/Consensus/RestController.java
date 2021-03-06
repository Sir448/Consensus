package com.example.Consensus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import javax.websocket.Session;
import java.math.BigInteger;
import java.sql.*;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/host")
    Integer host(@RequestBody Map<String,Integer> data){

        String query = "INSERT INTO SESSIONS(Host_ID, User_Count, Session_Status, Decision_Type, Majority) VALUES (?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY()";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, data.get("Host_ID"));
                        ps.setInt(2, 1);
                        ps.setInt(3, 0);
                        ps.setInt(4, data.get("Decision_Type"));
                        if(1 == data.get("Decision_Type")){
                            ps.setInt(5, data.get("Majority"));
                        }else{
                            ps.setNull(5, Types.INTEGER);
                        }
                        return ps;
                    }
                },
                keyHolder
        );

        return keyHolder.getKey().intValue();
    }

    @PostMapping("/join")
    Map<String, Object> join(@RequestBody Map<String,Object> data){

        String[] query = {
                "SELECT * FROM SESSIONS WHERE Session_ID = " + data.get("Session_ID") + " AND EXISTS (SELECT * FROM USERS WHERE Session_ID IS NULL AND User_ID = " + data.get("User_ID")+")",
                "UPDATE SESSIONS SET User_Count = User_Count + 1 WHERE Session_ID = " + data.get("Session_ID"),
                "UPDATE USERS SET Session_ID = "+ data.get("Session_ID") +" WHERE User_ID = "+ data.get("User_ID")
        };

        Map<String, Object> session;
        try{
            System.out.println(query[0]);
            session = jdbcTemplate.queryForMap(query[0]);
            System.out.println(query[1]);
            jdbcTemplate.update(query[1]);
            System.out.println(query[2]);
            jdbcTemplate.update(query[2]);
            session.put("User_Count", (int)session.get("User_Count") + 1);
            return session;
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @PutMapping("/addUser")
    int addUser(){
        String query = "INSERT INTO USERS(Session_ID) VALUES (NULL)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                        return ps;
                    }
                },
                keyHolder
        );
        return keyHolder.getKey().intValue();
    }

//    @PostMapping("/addOption")
//    public void addOption(@RequestBody Map<String,String[]> message){
//        var options = message.get("options");
////        System.out.println(message);
////        System.out.println(message.keySet());
////        System.out.println(message.get("test"));
////        System.out.println(message.get("options"));
//        for(String item : options){
//            System.out.println(item);
//        }
//    }

    @PostMapping("/addOption/session")
    public String addOption(@RequestBody Map<String, String[]> message){
        String query = "INSERT INTO Consensus.dbo.OPTIONS(Name, approvalCount, rejectionCount, Session_ID) VALUES";
        int id = 2;
        for(String item : message.get("options")) {
//            System.out.println(item);
            query += String.format(" (\'%s\', 0, 0, %d),",item,id);
        }
        query = query.substring(0,query.length() - 1);
        System.out.println(query);
        return query;
    }

    @PostMapping("/test")
    public void test(){
        List<String> options = jdbcTemplate.queryForList("SELECT Name FROM Consensus.dbo.OPTIONS WHERE Session_ID = ?", new Object[] {2}, new int[] {Types.INTEGER},String.class);
        System.out.println(options);
        System.out.println(options.contains("test"));
        System.out.println(options.contains("hello"));
        System.out.println("INSERT INTO Consensus.dbo.OPTIONS(Name, approvalCount, rejectionCount, Session_ID) VALUES".length());
    }
}
