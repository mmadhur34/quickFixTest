package com.home.madhur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataSnapshotFullRefresh;
import quickfix.fix44.NewOrderSingle;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by madhur on 2/19/2017.
 */
public class ServerMDApplication implements Application {

    private static volatile SessionID sessionID;

    static Logger log  = LoggerFactory.getLogger("ServerApplication");
    static List<String> instrument = new ArrayList<String>();
    MDPublisher publisher = new MDPublisher();
    Thread mdt = new Thread(publisher);

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
        String msgType = message.getHeader().getString(55);
        if(message instanceof MarketDataRequest){
            MarketDataRequest mdr = (MarketDataRequest)message;
            NoRelatedSym noRelatedSym = new NoRelatedSym();
            mdr.get(noRelatedSym);
            if(noRelatedSym.getValue()==1){
                MarketDataRequest.NoRelatedSym symGrp = new MarketDataRequest.NoRelatedSym();
                mdr.getGroup(1,symGrp);
                Symbol symbol = symGrp.getSymbol();
                System.out.println("received subscri[ption request for "+symbol.getValue());
                instrument.add(symbol.getValue());
            }
            if(!mdt.isAlive()){
                mdt.start();
            }
        }
    }

    public static void main(String[] args) throws ConfigError, FileNotFoundException, InterruptedException, SessionNotFound {
        SessionSettings settings = new SessionSettings("acceptor.cfg");

        Application application = new ServerMDApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new ScreenLogFactory(true, true, true);
        MessageFactory messageFactory = new DefaultMessageFactory();


        Acceptor acceptor = new SocketAcceptor(application, messageStoreFactory, settings, logFactory, messageFactory);
        System.out.println("Acceptor starting...");
        acceptor.start();
        System.out.println("Acceptor started"+ acceptor.toString());
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    static class MDPublisher implements Runnable{
        public void run(){
            MarketDataSnapshotFullRefresh snapshotFullRefresh = new MarketDataSnapshotFullRefresh();
            MarketDataSnapshotFullRefresh.NoMDEntries mdEntries = new MarketDataSnapshotFullRefresh.NoMDEntries();
            while (true){
                for(String symbol:instrument){
                    try {
                        snapshotFullRefresh.setString(55,symbol);
                        mdEntries.set(new MDEntryPx(100.00));
                        mdEntries.set(new MDEntrySize(5));
                        mdEntries.set(new MDEntryType(MDEntryType.BID));
                        snapshotFullRefresh.addGroup(mdEntries);
                        mdEntries.set(new MDEntryPx(101.00));
                        mdEntries.set(new MDEntrySize(6));
                        mdEntries.set(new MDEntryType(MDEntryType.OFFER));
                        snapshotFullRefresh.addGroup(mdEntries);
                        Session.sendToTarget(snapshotFullRefresh,sessionID);
                    } catch (SessionNotFound sessionNotFound) {
                    }
                }
            }
        }
    }
}
