package automata;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
		NDAutomaton newDeterministic = new NDAutomaton();
		newDeterministic.alphabet = this.alphabet;
		HashMap<Set<State>,State> stateHash = new HashMap<Set<State>,State>();
		for(State ndState : this.states){
			Set<State> setNDState = new PrintSet<State>();
			setNDState.add(ndState);
			State newDeterminsticState = newDeterministic.addNewState();
			if(this.isInitial(ndState)) newDeterministic.setInitial(newDeterminsticState);
			if(this.isAccepting(ndState)) newDeterministic.setAccepting(newDeterminsticState);
		}
		Set<Set<State>> alreadyPassed = new PrintSet<Set<State>>();
		Set<Set<State>> passing = stateHash.keySet();
		passing.removeAll(alreadyPassed);
		while(!passing.isEmpty()){
			Iterator<Set<State>> it = passing.iterator();
			while(it.hasNext()){
				Set<State> key = it.next();
				State newDState = null ;
				if(!alreadyPassed.contains(key)){
					alreadyPassed.add(key);
					for(Character c : this.alphabet){
						Set<State> nextKey = new PrintSet<State>();
						for(State state : key){
							Set<State> followingStates = this.getTransitionSet(state, c);
							for(State remplirSet : followingStates)
								nextKey.add(remplirSet);
						}
						if(!nextKey.isEmpty() && !stateHash.containsKey(nextKey)){
							newDState = newDeterministic.addNewState();
							if(this.isSetStateAccepting(nextKey)) newDeterministic.setAccepting(newDState);
							if(this.isSetStateInitial(nextKey)) newDeterministic.setAccepting(newDState);
							stateHash.put(nextKey, newDState);
							newDeterministic.addTransition(stateHash.get(key), c, newDState);
						}
					}
				}
			}
			passing = stateHash.keySet();
			passing.removeAll(alreadyPassed);
		}
		
		
		
		return newDeterministic;
	}
	
	private boolean isSetStateInitial(Set<State> stateSet){
		if(stateSet.isEmpty()) return false;
		for(State state : stateSet)
			if(this.isInitial(state)) return true;
		return false;
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