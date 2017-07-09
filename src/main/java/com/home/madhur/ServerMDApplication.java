package com.home.madhur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.NewOrderSingle;

import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by madhur on 2/19/2017.
 */
public class ServerMDApplication implements Application {

    private static volatile SessionID sessionID;

    static Logger log  = LoggerFactory.getLogger("ServerApplication");


    public void onCreate(SessionID sessionID) {
    }


    public void onLogon(SessionID sessionID) {
        System.out.println("OnLogon");
        ServerMDApplication.sessionID = sessionID;
    }


    public void onLogout(SessionID sessionID) {
        System.out.println("OnLogout");
        ServerMDApplication.sessionID = null;
    }


    public void toAdmin(Message message, SessionID sessionID) {
    }


    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }


    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
    }


    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("FromApp: " + message);
        NewOrderSingle ord  = (NewOrderSingle) message;
        ExecutionReport execRpt  = new ExecutionReport();
        execRpt.set(new OrderID(String.valueOf(ord.getClOrdID())));
        execRpt.set(new ExecID(String.valueOf(System.currentTimeMillis())));
        execRpt.set(new ExecType(ExecType.TRADE));
        execRpt.set(new OrdStatus(OrdStatus.FILLED));
        execRpt.set(new Symbol("US2Y"));
        execRpt.set(((NewOrderSingle)message).getSide());
        execRpt.set(new LeavesQty(0));
        execRpt.set(new CumQty(0));
        execRpt.set(new AvgPx((ord.getPrice()).getValue()));

        try {
            Session.sendToTarget(execRpt,sessionID);
        } catch (SessionNotFound sessionNotFound) {
//            Session.sendToTarget(execRpt,sessionID);
        }

    }

    public static void main(String[] args) throws ConfigError, FileNotFoundException, InterruptedException, SessionNotFound {
        SessionSettings settings = new SessionSettings("acceptor.cfg");

        Application application = new ServerMDApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        Acceptor acceptor = new SocketAcceptor(application, messageStoreFactory, settings, logFactory, messageFactory);
        System.out.println("Initiator starting...");
        acceptor.start();
        System.out.println("Initiator started"+ acceptor.toString());
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
