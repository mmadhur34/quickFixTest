package com.home.madhur;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix50.component.MDReqGrp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by madhur on 2/19/2017.
 */
public class ClientMDApplication implements Application {

    public static final int ITERATIONS = 1;
    private static volatile SessionID sessionID;


    public void onCreate(SessionID sessionID) {
        System.out.println("OnCreate");
    }


    public void onLogon(SessionID sessionID) {
        System.out.println("OnLogon");
        ClientMDApplication.sessionID = sessionID;
    }


    public void onLogout(SessionID sessionID) {
        System.out.println("OnLogout");
        ClientMDApplication.sessionID = null;
    }


    public void toAdmin(Message message, SessionID sessionID) {
        System.out.println("ToAdmin");
    }


    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
//        System.out.println("FromAdmin");
    }


    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
//        System.out.println("ToApp: " + message);
    }


    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

    }

    public static void main(String[] args) throws ConfigError, FileNotFoundException, InterruptedException, SessionNotFound {
        SessionSettings settings = new SessionSettings("initiator.cfg");

        Application application = new ClientMDApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        Initiator initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
        initiator.start();

        while (sessionID == null) {
            Thread.sleep(1000);
        }
        List<String> instruments = Arrays.asList("USTB2Y","USTB3Y","USTB5Y","USTB7Y","USTB10Y","USTB30Y");
        for(String symbol:instruments){
            MarketDataRequest mdReq = getMarketDataRequest(symbol);
            Session.sendToTarget(mdReq, sessionID);
            System.out.println("MD Request sent");
        }
        try {
            System.out.println("Press any key to exit");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MarketDataRequest getMarketDataRequest(String symbol) {
        MarketDataRequest mdReq = new MarketDataRequest();
        mdReq.set(new MDReqID("MDReq_"+symbol));
        mdReq.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
        mdReq.set(new MarketDepth(5));
        MarketDataRequest.NoRelatedSym relatedSymbols = new
                MarketDataRequest.NoRelatedSym();
        relatedSymbols.set(new Symbol(symbol));

        MarketDataRequest.NoMDEntryTypes mdEntryTypes = new
                MarketDataRequest.NoMDEntryTypes();
        mdEntryTypes.set(new MDEntryType('0')); // bid
        mdReq.addGroup(mdEntryTypes);
        mdEntryTypes.set(new MDEntryType('1')); // Offer

        mdReq.addGroup(mdEntryTypes);
        mdReq.addGroup(relatedSymbols);
        return mdReq;
    }
}


