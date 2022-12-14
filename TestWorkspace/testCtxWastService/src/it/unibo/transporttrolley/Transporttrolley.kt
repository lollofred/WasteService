/* Generated by AN DISI Unibo */ 
package it.unibo.transporttrolley

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Transporttrolley ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "idle"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
					lateinit var TrolleyPos : String
		return { //this:ActionBasciFsm
				state("idle") { //this:State
					action { //it:State
						 TrolleyPos = "home"  
						updateResourceRep( "trolleyPos(home)"  
						)
					}
					 transition(edgeName="t2",targetState="move",cond=whenDispatch("goto"))
				}	 
				state("move") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("goto(POSITION)"), Term.createTerm("goto(home)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep( "trolleyPos(home)"  
								)
						}
						if( checkMsgContent( Term.createTerm("goto(POSITION)"), Term.createTerm("goto(indoor)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep( "trolleyPos(indoor)"  
								)
						}
						forward("executeaction", "executeaction(_)" ,"wasteservice" ) 
					}
					 transition(edgeName="t3",targetState="move",cond=whenDispatch("goto"))
				}	 
			}
		}
}
