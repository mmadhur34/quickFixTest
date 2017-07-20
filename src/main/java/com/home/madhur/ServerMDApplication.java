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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by madhur on 2/19/2017.
 */
public class ServerMDApplication implements Application {

    private static volatile SessionID sessionID;

    static Logger log  = LoggerFactory.getLogger("ServerApplication");
    static List<String> instrument = new ArrayList<String>();
    public static AtomicInteger subscriptionCount = new AtomicInteger(0);
    MDPublisher publisher = new MDPublisher();
    Thread mdt = new Thread(publisher,"MD Publisher");

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
        String msgType = message.getHeader().getString(35);
        if(message instanceof MarketDataRequest){
            MarketDataRequest mdr = (MarketDataRequest)message;
            NoRelatedSym noRelatedSym = new NoRelatedSym();
            mdr.get(noRelatedSym);
            if(noRelatedSym.getValue()==1){
                MarketDataRequest.NoRelatedSym symGrp = new MarketDataRequest.NoRelatedSym();
                mdr.getGroup(1,symGrp);
                Symbol symbol = symGrp.getSymbol();
                System.out.println("received subscri[ption request for "+symbol.getValue());
                synchronized (instrument){
                    instrument.add(symbol.getValue());
                    subscriptionCount.incrementAndGet();
                }
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
            boolean increase = true;
            double px1= 100.00; double px2=101.00;
            int size1=5; int size2=6;
            try{
                while (true){
                    if(subscriptionCount.get()!=6)
                        Thread.sleep(100);
                    for(String symbol:instrument){
                        try {
                            MarketDataSnapshotFullRefresh snapshotFullRefresh = new MarketDataSnapshotFullRefresh();
                            MarketDataSnapshotFullRefresh.NoMDEntries mdEntries = new MarketDataSnapshotFullRefresh.NoMDEntries();
                            snapshotFullRefresh.setString(55,symbol);
                            mdEntries.set(new MDEntryPx(increase?px1+1.0:px1-1.0));
                            mdEntries.set(new MDEntrySize(increase?size1+1:size1-1));
                            mdEntries.set(new MDEntryType(MDEntryType.BID));
                            snapshotFullRefresh.addGroup(mdEntries);
                            mdEntries.set(new MDEntryPx(increase?px2+1:px2-1));
                            mdEntries.set(new MDEntrySize(increase?size2+1:size2-1));
                            mdEntries.set(new MDEntryType(MDEntryType.OFFER));
                            snapshotFullRefresh.addGroup(mdEntries);
                            Session.sendToTarget(snapshotFullRefresh,sessionID);

                        } catch (SessionNotFound sessionNotFound) {
                        }

                    }
                    increase = !increase;
//                    Thread.sleep(10);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
