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


public class TestctxWasteService {
	private CoapConnection connT;
	private CoapConnection connW;

	@Before
	public void setup() {
		ColorsOut.outappl("setting up...", ColorsOut.YELLOW);
		CommSystemConfig.tracing = false;
		ColorsOut.outappl("setting up coap...", ColorsOut.YELLOW);
		startObserverCoapW("wasteservice", "localhost", "8032", "ctxwasteservice", new CoapResObserver());
		startObserverCoapT("transporttrolley", "localhost", "8032", "ctxwasteservice", new CoapResObserver());
		ColorsOut.outappl("coap started", ColorsOut.GREEN);

		new Thread(){
			public void run(){
				MainCtxwasteserviceKt.main();
			}
		}.start();
		ColorsOut.outappl("waiting for context started...", ColorsOut.YELLOW);
		waitForApplStarted("wasteservice");
		waitForApplStarted("transporttrolley");
		ColorsOut.outappl("context started", ColorsOut.GREEN);
		getNextStateT();
	}

	protected void waitForApplStarted(String actorName){
		ActorBasic act = QakContext.Companion.getActor(actorName);
		while( act == null ){
			ColorsOut.outappl("TestTransportTrolley waits for appl ... " , ColorsOut.GREEN);
			CommUtils.delay(200);
			act = QakContext.Companion.getActor(actorName);
		}

	}
	@After
	public void finish() {
		ColorsOut.outappl("TestTransportTrolley ENDS" , ColorsOut.BLUE);
	}

	@Test
	public void testWaste() {
		ColorsOut.outappl("testWaste STARTS" , ColorsOut.BLUE);
		double currentLoad = WasteState.fromStringRepresentation(getCurrentStateW()).getLoadOf(Material.GLASS);
		double truckload= 21.0;
		String truckRequestStr = "msg(deposit,request,python,wasteservice,deposit("+truckload+",glass),1)";
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8032);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testLoadok answer=" + answer , ColorsOut.GREEN);
			connTcp.close();
			assertTrue(answer.contains("loadaccept"));

			assertEquals( currentLoad + truckload , WasteState.fromStringRepresentation(getCurrentStateW()).getLoadOf(Material.GLASS),0.1);
			assertEquals("trolleyPos(indoor)",getNextStateT());

		}catch(Exception e){
			ColorsOut.outerr("testLoadok ERROR:" + e.getMessage());
		}
	}

//---------------------------------------------------

	protected String getNextStateT(){
		String state = connT.request("");
		ColorsOut.outappl("T current state=" + state, ColorsOut.CYAN);
		return state;
	}

	protected String getCurrentStateW(){
		String state = connW.request("");
		ColorsOut.outappl("W current state=" + state, ColorsOut.CYAN);
		return state;
	}

	protected void startObserverCoapW(String qakdestination, String addr, String applPort, String ctxqakdest, CoapHandler handler){
		new Thread(){
			public void run(){
				try {
					String path             = ctxqakdest+"/"+qakdestination;
					connW = new CoapConnection(addr+":"+applPort, path);
					connW.observeResource( handler );
					ColorsOut.outappl("connected via Coap conn:" + connW, ColorsOut.CYAN);
				}catch(Exception e){
					ColorsOut.outerr("connectUsingCoap ERROR:"+e.getMessage());
				}
			}
		}.start();
	}

	protected void startObserverCoapT(String qakdestination, String addr, String applPort, String ctxqakdest, CoapHandler handler){
		new Thread(){
			public void run(){
				try {
					String path             = ctxqakdest+"/"+qakdestination;
					connT = new CoapConnection(addr+":"+applPort, path);
					connT.observeResource( handler );
					ColorsOut.outappl("connected via Coap conn:" + connT, ColorsOut.CYAN);
				}catch(Exception e){
					ColorsOut.outerr("connectUsingCoap ERROR:"+e.getMessage());
				}
			}
		}.start();
	}
}


