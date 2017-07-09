package com.home.madhur;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by madhur on 2/19/2017.
 */
public class ClientApplication implements Application {

    public static final int ITERATIONS = 1;
    private static volatile SessionID sessionID;


    public void onCreate(SessionID sessionID) {
        System.out.println("OnCreate");
    }


    public void onLogon(SessionID sessionID) {
        System.out.println("OnLogon");
        ClientApplication.sessionID = sessionID;
    }


    public void onLogout(SessionID sessionID) {
        System.out.println("OnLogout");
        ClientApplication.sessionID = null;
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
//        System.out.println("FromApp");
    }

    public static void main(String[] args) throws ConfigError, FileNotFoundException, InterruptedException, SessionNotFound {
        SessionSettings settings = new SessionSettings("initiator.cfg");

        Application application = new ClientApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();

        Initiator initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
        initiator.start();

        while (sessionID == null) {
            Thread.sleep(1000);
        }
        //0th order
        System.out.println("Before 0th order- "+ System.currentTimeMillis());
        NewOrderSingle ord = new NewOrderSingle();
//        long start1 = System.nanoTime();
        ord.set(new ClOrdID("MISYS1000"));
        ord.set(new HandlInst(HandlInst.MANUAL_ORDER));
        ord.set(new Symbol("MISYS"));
        ord.set(new Price(100.00));
        ord.set(new Side(Side.BUY));
        ord.set(new TransactTime(new Date()));
        ord.set(new OrdType(OrdType.LIMIT));
//        Session.sendToTarget(order, sessionID);
        System.out.println("zeroth time - " + System.currentTimeMillis());


        System.out.println("Before 1st order- "+ System.currentTimeMillis());
        NewOrderSingle order = new NewOrderSingle();
//        long start1 = System.nanoTime();
        order.set(new ClOrdID("MISYS1001"));
        order.set(new HandlInst(HandlInst.MANUAL_ORDER));
        order.set(new Symbol("MISYS"));
        order.set(new Price(100.00));
        order.set(new Side(Side.BUY));
        order.set(new TransactTime(new Date()));
        order.set(new OrdType(OrdType.LIMIT));
        Session.sendToTarget(order, sessionID);
//        System.out.println("first time - " + (System.nanoTime() - start1)/1000);


        //order -2
        System.out.println("Before 2nd order- "+ System.currentTimeMillis());
        NewOrderSingle order1 = new NewOrderSingle();
//        long start1 = System.nanoTime();
        order1.set(new ClOrdID("MISYS1002"));
        order1.set(new HandlInst(HandlInst.MANUAL_ORDER));
        order1.set(new Symbol("MISYS"));
        order1.set(new Price(100.00));
        order1.set(new Side(Side.BUY));
        order1.set(new TransactTime(new Date()));
        order1.set(new OrdType(OrdType.LIMIT));

        Session.sendToTarget(order1, sessionID);
//        System.out.println("first time - " + (System.nanoTime() - start1)/1000);

        long[] runtimes = new long[ITERATIONS];
        final String orderId = "342";
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            order.set(new ClOrdID("MISYS100X"));
            order.set(new HandlInst(HandlInst.MANUAL_ORDER));
            order.set(new Symbol("MISYS"));
            order.set(new Price(100.00));
            order.set(new Side(Side.BUY));
            order.set(new TransactTime(new Date()));
            order.set(new OrdType(OrdType.LIMIT));
            Session.sendToTarget(order, sessionID);
//            runtimes[i] = System.nanoTime() - start;
        }
//        Statistics statistics = new Statistics(runtimes);
//        System.out.println("print statistics");
//        System.out.println("statistics:avg " + statistics.avg / 1000);
//        System.out.println("statistics:min " + statistics.min / 1000);
//        System.out.println("statistics:max " + statistics.max / 1000);
//        for (int j = 0; j < ITERATIONS; j++) {
//            System.out.println(j + "-" + runtimes[j] / 1000);
//        }
        //order -3
        System.out.println("Before 3rd order- "+ System.currentTimeMillis());
        NewOrderSingle order2 = new NewOrderSingle();
//        long start1 = System.nanoTime();
        order2.set(new ClOrdID("MISYS1003"));
        order2.set(new HandlInst(HandlInst.MANUAL_ORDER));
        order2.set(new Symbol("MISYS"));
        order2.set(new Price(100.00));
        order2.set(new Side(Side.BUY));
        order2.set(new TransactTime(new Date()));
        order2.set(new OrdType(OrdType.LIMIT));

        Session.sendToTarget(order2, sessionID);
//        System.out.println("first time - " + (System.nanoTime() - start1)/1000);
        System.out.println("Game over");

    }
}


