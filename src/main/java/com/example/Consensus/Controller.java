package com.example.Consensus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.Session;
import java.math.BigInteger;
import java.sql.*;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/host")
    Integer host(@RequestBody SessionInfo sessionInfo){

        String query = "INSERT INTO SESSIONS(Host_ID, User_Count, Session_Status, Decision_Type, Majority) VALUES (?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY()";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, sessionInfo.getHost_ID());
                        ps.setInt(2, 1);
                        ps.setInt(3, 0);
                        ps.setInt(4, sessionInfo.getDecision_Type());
                        if(1 == sessionInfo.getDecision_Type()){
                            ps.setInt(5, sessionInfo.getMajority());
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

//    @PostMapping("/join")
//    SessionInfo join(@RequestBody SessionInfo sessionInfo){
//
//        String query = "UPDATE SESSIONS SET User_Count = User_Count + 1 OUTPUT INSERTED.* WHERE Session_ID = "+sessionInfo.getSession_ID();
//        final SessionInfo[] joinedSession = new SessionInfo[1];
//        jdbcTemplate.query(
//            query,
//            new RowCallbackHandler() {
//                @Override
//                public void processRow(ResultSet resultSet) throws SQLException {
//                    joinedSession[0] = new SessionInfo(resultSet.getInt("Session_ID"), resultSet.getInt("Host_ID"), resultSet.getInt("User_Count"), resultSet.getInt("Session_Status"), resultSet.getInt("Decision_Type"),resultSet.getInt("Majority"));
//                }
//            }
//        );
//        return joinedSession[0];
//    }

    @PostMapping("/join")
    SessionInfo join(@RequestBody SessionInfo sessionInfo){

        String query = "UPDATE SESSIONS SET User_Count = User_Count + 1 OUTPUT INSERTED.* WHERE Session_ID = "+sessionInfo.getSession_ID();
        System.out.println(query);
        SessionInfo joinedSession;
        try {
            joinedSession = jdbcTemplate.queryForObject(query, (resultSet, i) ->
                    new SessionInfo(
                            resultSet.getInt("Session_ID"),
                            resultSet.getInt("Host_ID"),
                            resultSet.getInt("User_Count"),
                            resultSet.getInt("Session_Status"),
                            resultSet.getInt("Decision_Type"),
                            resultSet.getInt("Majority")
                    ));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return joinedSession;
    }
}
