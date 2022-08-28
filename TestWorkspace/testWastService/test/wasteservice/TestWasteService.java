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


public class TestWasteService {
private CoapConnection conn;

	@Before
	public void setup() {
		CommSystemConfig.tracing=false;

		ColorsOut.outappl("setting up...", ColorsOut.YELLOW);
		CommSystemConfig.tracing = false;
		ColorsOut.outappl("setting up coap...", ColorsOut.YELLOW);
		startObserverCoap("wasteservice", "localhost", "8032", "ctxwasteservice", new CoapResObserver());
		ColorsOut.outappl("coap started", ColorsOut.GREEN);

		new Thread(){
			public void run(){
				MainCtxwasteserviceKt.main();
			}
		}.start();
		ColorsOut.outappl("waiting for context started...", ColorsOut.YELLOW);
		waitForApplStarted();
		ColorsOut.outappl("context started", ColorsOut.GREEN);
  	}

 	protected void waitForApplStarted(){
		ActorBasic wasteservice = QakContext.Companion.getActor("wasteservice");
		while( wasteservice == null ){
			ColorsOut.outappl("TestWasteService waits for appl ... " , ColorsOut.GREEN);
			CommUtils.delay(200);
			wasteservice = QakContext.Companion.getActor("wasteservice");
		}

	}
	@After
	public void finish() {
		ColorsOut.outappl("TestWasteService ENDS" , ColorsOut.BLUE);
	}

	@Test
	public void testLoadok() {
		ColorsOut.outappl("testLoadok STARTS" , ColorsOut.BLUE);
		double currentLoad = WasteState.fromStringRepresentation(getCurrentState()).getLoadOf(Material.GLASS);
		double truckload= 21.0;
		String truckRequestStr = "msg(deposit,request,python,wasteservice,deposit(" + truckload +",glass),1)";
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8032);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testLoadok answer=" + answer , ColorsOut.GREEN);
			connTcp.close();
			assertTrue(answer.contains("loadaccept"));

			assertEquals( (currentLoad + truckload), WasteState.fromStringRepresentation(getCurrentState()).getLoadOf(Material.GLASS),0.1);
		}catch(Exception e){
			ColorsOut.outerr("testLoadok ERROR:" + e.getMessage());
		}
	}

//---------------------------------------------------

protected String getCurrentState(){
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