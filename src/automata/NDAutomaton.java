package automata;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Implémentation d'un automate non déterministe.
 * Version à compléter ou à étendre.
 * 
 * @author Bruno.Bogaert (at) univ-lille1.fr
 *
 */
public class NDAutomaton extends AbstractAutomaton implements Recognizer, AutomatonBuilder {

	protected Set<State> initialStates;
	protected HashMap<Key, Set<State>> delta;

	public NDAutomaton() {
		super();
		initialStates = new PrintSet<State>();
		delta = new HashMap<Key, Set<State>>();
	}
	@Override
	public boolean isInitial(String name) throws StateException {
		return isInitial(states.get(name));
	}

	@Override
	public boolean isInitial(Integer id) throws StateException {
		return isInitial(states.get(id));
	}

	@Override
	public Set<State> getTransitionSet(State from, char letter) {
		Set<State> s = delta.get(new Key(from, letter));
		if (s==null)
			return Collections.emptySet();
		else
			return Collections.unmodifiableSet(s);
	}

	@Override
	public Set<State> getTransitionSet(String from, char letter) {
		return getTransitionSet(states.get(from), letter);
	}

	@Override
	public Set<State> getTransitionSet(Integer from, char letter) {
		return getTransitionSet(states.get(from), letter);
	}

	@Override
	public Set<State> getInitialStates() {
		return Collections.unmodifiableSet(this.initialStates);
	}

	@Override
	public void setInitial(State s) {
		initialStates.add(s);
	}

	@Override
	public boolean isInitial(State s) {
		return initialStates.contains(s);
	}

	@Override
	public void addTransition(State from, Character letter, State to) {
		alphabet.add(letter);
		Key k = new Key(from, letter);
		Set<State> arrival = delta.get(k);
		if (arrival == null) {
			arrival = new PrintSet<State>();
		}
		if (!arrival.contains(to)) {
			arrival.add(to);
			delta.put(k, arrival);
		}
	}

	@Override
	public boolean accept(String word) {
		boolean valRet = false;
		for(State initialState : this.getInitialStates())
			valRet |= this.acceptUtil(initialState, word);
		return valRet;
	}
	
	private boolean acceptUtil(State currentState, String word){
		boolean valRet = false;
		try {
			if(word.length() == 0)
				return this.isAccepting(currentState);
		} catch (StateException e) {
			return false;
		}

		Set<State> newStates = this.getTransitionSet(currentState, word.charAt(0));
		for(State state : newStates)
			valRet |= this.acceptUtil(state, word.substring(1));
		return valRet;
	}

	
	public NDAutomaton deterministic(){
		NDAutomaton dAutomaton = new NDAutomaton();
		HashMap<Set<State>, State> hashMap = new HashMap<Set<State>, State>();
		List<Set<State>> stateList = new ArrayList<Set<State>>();
		Set<State> nextSetState ;
		State deterministState ;
		for(State initialState : this.initialStates){
			nextSetState = new PrintSet<State>();
			nextSetState.add(initialState);
			stateList.add(nextSetState);
			deterministState = dAutomaton.addNewState();
			dAutomaton.setInitial(deterministState);
			hashMap.put(nextSetState, deterministState);
		}
		
		while(!stateList.isEmpty()){
			Set<State> tmp = stateList.remove(0);
			for(Character letter : this.alphabet){
				nextSetState = getFollowingSet(tmp, letter);
				if(!nextSetState.isEmpty()){
					if(!hashMap.containsKey(nextSetState)){
						deterministState = dAutomaton.addNewState();
						hashMap.put(nextSetState, deterministState);
						stateList.add(nextSetState);
					}
					if(this.isSetStateAccepting(nextSetState)) dAutomaton.setAccepting(hashMap.get(nextSetState));
					dAutomaton.addTransition(hashMap.get(tmp), letter, hashMap.get(nextSetState));
				}
			}
		}
		return dAutomaton;
	}
	
	private Set<State> getFollowingSet(Set<State> states, Character c){
		Set<State> stateList = new PrintSet<State>();
		for(State state : states){
			stateList.addAll(this.getTransitionSet(state, c));
		}
		return stateList;	
	}
	
	private boolean isSetStateAccepting(Set<State> stateSet){
		if(stateSet.isEmpty()) return false;
		for(State state : stateSet)
			if(this.isAccepting(state)) return true;
		return false;
	}


	public Writer writeGraphvizTransitions(Writer buff) {
		PrintWriter out = new PrintWriter(buff);
		for (Map.Entry<Key, Set<State>> entry : delta.entrySet()) {
			for (State dest : entry.getValue()) {
				out.print("  " + entry.getKey().from.getId() + " -> " + dest.getId());
				out.println(" [label = \"" + entry.getKey().letter + "\" ]");
			}
		}
		return buff;
	}

	public Writer writeGraphvizInner(Writer buff) {
		writeGraphvizStates(buff, true);
		writeGraphvizInitials(buff);
		writeGraphvizTransitions(buff);
		return buff;
	}


}
