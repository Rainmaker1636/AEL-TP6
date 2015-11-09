package automata;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class testNDAutomaton {
	
	private NDAutomaton testAutomate;
	
	private void createAutomate(){
		List<State> etats = new ArrayList<State>();
		testAutomate = new NDAutomaton();
		try {
			for(int i = 0; i< 7;i++){
				etats.add(testAutomate.addNewState());
			}
			
			testAutomate.setInitial(etats.get(0));
			testAutomate.addTransition(etats.get(0), 'c', etats.get(1));
			testAutomate.addTransition(etats.get(1), 'o', etats.get(2));
			testAutomate.addTransition(etats.get(2), 'c', etats.get(3));
			testAutomate.addTransition(etats.get(3), 'a', etats.get(4));
			testAutomate.addTransition(etats.get(2), 'i', etats.get(5));
			testAutomate.addTransition(etats.get(5), 'n', etats.get(6));
			
			testAutomate.setAccepting(etats.get(4));
		} catch (StateException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void returnsTrueWordAccepted() {
		this.createAutomate();
		assertTrue(this.testAutomate.accept("coca"));
	}
	
	@Test
	public void returnsFalseWordInAutomataButLastStateNotAccepting(){
		this.createAutomate();
		assertFalse(this.testAutomate.accept("coin"));
	}
	
	@Test
	public void returnsFalseWordNotAccepted(){
		this.createAutomate();
		assertFalse(this.testAutomate.accept("prout"));
	}
	
	@Test
	public void testDeterminist(){
		this.createAutomate();
		System.out.println(this.testAutomate.toGraphviz());
		System.out.println(this.testAutomate.deterministic().toGraphviz());		
	}

}
