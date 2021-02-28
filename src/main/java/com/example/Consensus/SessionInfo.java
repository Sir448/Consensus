package com.example.Consensus;

import org.apache.catalina.Host;
import org.apache.catalina.User;

public class SessionInfo {
    private int Session_ID;
    private int Host_ID;
    private int User_Count;
    private int Session_Status;
    private int Decision_Type;
    private int Majority;

    SessionInfo(int Session_ID, int Host_ID, int User_Count, int Session_Status, int Decision_Type, int Majority){
        this.Session_ID = Session_ID;
        this.Host_ID = Host_ID;
        this.User_Count = User_Count;
        this.Session_Status = Session_Status;
        this.Decision_Type = Decision_Type;
        this.Majority = Majority;
    }

    public int getSession_ID(){
        return this.Session_ID;
    }

    public int getHost_ID(){
        return this.Host_ID;
    }

    public int getUser_Count(){
        return this.User_Count;
    }

    public int getSession_Status(){
        return this.Session_Status;
    }

    public int getDecision_Type(){
        return this.Decision_Type;
    }

    public int getMajority(){
        return this.Majority;
    }

    @Override
    public String toString(){
        return String.format("Session ID: %d | Host ID: %d | User Count: %d | Session Status: %d | Decision Type: %d | Majority: %d",this.Session_ID, this.Host_ID, this.User_Count, this.Session_Status, this.Decision_Type, this.Majority);
    }
}
