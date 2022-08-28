package wasteservice;

import it.unibo.ctxwasteservice.MainCtxwasteserviceKt;
import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.QakContext;
import org.eclipse.californium.core.CoapHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unibo.comm22.coap.CoapConnection;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;
import wasteservice.state.Material;
import wasteservice.state.WasteState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestTransportTrolley {
	private CoapConnection conn;

	@Before
	public void setup() {
		ColorsOut.outappl("setting up...", ColorsOut.YELLOW);
		CommSystemConfig.tracing = false;
		ColorsOut.outappl("setting up coap...", ColorsOut.YELLOW);
		startObserverCoap("transporttrolley", "localhost", "8032", "ctxwasteservice", new CoapResObserver());
		ColorsOut.outappl("coap started", ColorsOut.GREEN);

		new Thread(){
			public void run(){
				MainCtxwasteserviceKt.main();
			}
		}.start();
		ColorsOut.outappl("waiting for context started...", ColorsOut.YELLOW);
		waitForApplStarted();
		ColorsOut.outappl("context started", ColorsOut.GREEN);
		getNextState();
	}

	protected void waitForApplStarted(){
		ActorBasic transporttrolley = QakContext.Companion.getActor("transporttrolley");
		while( transporttrolley == null ){
			ColorsOut.outappl("TestTransportTrolley waits for appl ... " , ColorsOut.GREEN);
			CommUtils.delay(200);
			transporttrolley = QakContext.Companion.getActor("transporttrolley");
		}

	}
	@After
	public void finish() {
		ColorsOut.outappl("TestTransportTrolley ENDS" , ColorsOut.BLUE);
	}

	@Test
	public void testLoadok() {
		ColorsOut.outappl("testLoadok STARTS" , ColorsOut.BLUE);
		String truckRequestStr= "";
		String answer= "";
		try{
			ConnTcp connTcp  = new ConnTcp("localhost", 8032);

			//pickup
			truckRequestStr = "msg(pickup,request,python,transporttrolley,pickup(_),1)";
			answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testLoadok answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("executeaction(donePickup)"));

			truckRequestStr = "msg(deposita,request,python,transporttrolley,deposita(GLASS),3)";
			answer = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN);
			assertTrue(answer.contains("executeaction(doneDeposit)"));
			//---

			truckRequestStr = "msg(pickup,request,python,transporttrolley,pickup(_),5)";
			answer = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN);
			assertTrue(answer.contains("executeaction(donePickup)"));

			truckRequestStr = "msg(deposita,request,python,transporttrolley,deposita(PLASTIC),7)";
			answer = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN);
			assertTrue(answer.contains("executeaction(doneDeposit)"));

			//home
			truckRequestStr = "msg(home,request,python,transporttrolley,home(_),9)";
			answer = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN);
			assertTrue(answer.contains("executeaction(doneHome)"));

			connTcp.close();

		}catch(Exception e){
			ColorsOut.outerr("testLoadok ERROR:" + e.getMessage());
		}
	}

//---------------------------------------------------

	protected String getNextState(){
		String state = conn.request("");
		ColorsOut.outappl("current state=" + state, ColorsOut.CYAN);
		return state;
	}

	protected void startObserverCoap(String qakdestination, String addr, String applPort, String ctxqakdest, CoapHandler handler){
		new Thread(){
			public void run(){
				try {
					String path             = ctxqakdest+"/"+qakdestination;
					conn                    = new CoapConnection(addr+":"+applPort, path);
					conn.observeResource( handler );
					ColorsOut.outappl("connected via Coap conn:" + conn , ColorsOut.CYAN);
				}catch(Exception e){
					ColorsOut.outerr("connectUsingCoap ERROR:"+e.getMessage());
				}
			}
		}.start();
	}
}


