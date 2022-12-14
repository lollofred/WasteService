/* Generated by AN DISI Unibo */ 
package it.unibo.wasteservice

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Wasteservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "idle"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
					val MAX = mapOf(wasteservice.state.Material.PLASTIC to 22.0,
						wasteservice.state.Material.GLASS to 22.0)
					val currentState = wasteservice.state.WasteState()
					lateinit var truckType : wasteservice.state.Material //glass, plastic
					var truckLoad : Double = 0.0 	
					
		return { //this:ActionBasciFsm
				state("idle") { //this:State
					action { //it:State
						updateResourceRep( currentState.toStringRepresentation()  
						)
					}
					 transition(edgeName="t0",targetState="control",cond=whenRequest("deposit"))
				}	 
				state("control") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("deposit(TRUCKLOAD,TRUCKTYPE)"), Term.createTerm("deposit(TRUCKLOAD,TRUCKTYPE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												 
												 try {
												 	truckType = wasteservice.state.Material.valueOf(payloadArg(1).trim().uppercase())
												 	truckLoad = payloadArg(0).trim().toDouble()
												 	if(currentState.canStore(truckType, MAX[truckType]!!, truckLoad)) {
												 		currentState.addLoad(truckType, truckLoad)
								updateResourceRep( currentState.toStringRepresentation()  
								)
								answer("deposit", "loadaccept", "loadaccpet(_)"   )  
								forward("goto", "goto(indoor)" ,"transporttrolley" ) 
								
												 	} else {
								answer("deposit", "loadreject", "loadreject(_)"   )  
								
												 	}
												 } catch(e : Exception) {
								answer("deposit", "loadreject", "loadreject(_)"   )  
								
												 }
												 
						}
					}
					 transition(edgeName="t01",targetState="deposit",cond=whenDispatch("executeaction"))
				}	 
				state("deposit") { //this:State
					action { //it:State
						println("deposit.....")
						if(  truckType.toString() == "GLASS"  
						 ){println(truckType)
						}
						else
						 {println(truckType)
						 }
					}
				}	 
			}
		}
}
