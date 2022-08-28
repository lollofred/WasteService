package wasteservice;


import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import unibo.comm22.utils.ColorsOut;

import java.util.ArrayList;


public class CoapResObserver implements CoapHandler{
protected String history = "";
protected String currentState = "";
private static ArrayList<String> queueState = new ArrayList<String>(10);

    @Override
    public void onLoad(CoapResponse response) {
        currentState = response.getResponseText();
        queueState.add(currentState);
        ColorsOut.outappl("CoapResObserver currentState=" + currentState, ColorsOut.MAGENTA);
    }

    @Override
    public void onError() {
        ColorsOut.outerr("CoapObserver observe error!");
    }



}
