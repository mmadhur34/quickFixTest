//package com.home.madhur;
//
//
//import quickfix.*;
//import quickfix.field.*;
//import quickfix.fix44.NewOrderSingle;
//
//import java.util.Date;
//
///**
// * Created by madhur on 2/19/2017.
// */
//public class FixInitiatorApplication implements Application {
//    @Override
//    public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
//    }
//
//    @Override
//    public void fromApp(Message message, SessionID arg1) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
//        System.out.println("Received reply from executor");
//    }
//
//    @Override
//    public void onCreate(SessionID arg0) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onLogon(SessionID sessionId) {
//         System.out.println("Initiator LOGGED ON.......");
//        NewOrderSingle order = new NewOrderSingle();
//        order.set(new ClOrdID("MISYS1001"));
//        order.set(new HandlInst(HandlInst.MANUAL_ORDER));
//        order.set(new Symbol("MISYS"));
//        order.set(new Side(Side.BUY));
//        order.set(new TransactTime(new Date()));
//        order.set(new OrdType(OrdType.LIMIT));
//
//        Session.sendToTarget(order, sessionId);
//    }
//
//    @Override
//    public void onLogout(SessionID arg0) {
//        System.out.println("Session logged out");
//    }
//
//    @Override
//    public void toAdmin(Message arg0, SessionID arg1) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void toApp(Message arg0, SessionID arg1) throws DoNotSend {
//        // TODO Auto-generated method stub
//    }
//}
