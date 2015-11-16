package automata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class DAutomaton extends AbstractAutomaton implements DeterministicAutomaton{
	protected State initialState;
	protected HashMap<Key, State> delta;

	public DAutomaton() {
		super();
		this.initialState = null;
		delta = new HashMap<Key,State>();
	}
	
	@Override
	public Set<State> getInitialStates() {
		Set<State> initialStates = new PrintSet<State>();
		if(this.initialState != null) initialStates.add(this.initialState);
		return initialStates;
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
		State s = delta.get(new Key(from, letter));
		if (s==null)
			return Collections.emptySet();
		else{
			Set<State> valRet = new PrintSet<State>();
			valRet.add(s);
			return valRet;
		}
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
	public State getInitialState() {
		return this.initialState;
	}
	
	@Override
	public State getTransition(State s, char letter) throws StateException {
		State val = this.delta.get(new Key(s, letter));
		return val;
	}

	@Override
	public State getTransition(String name, char letter) throws StateException {
		return this.getTransition(this.states.get(name), letter);
	}

	@Override
	public State getTransition(Integer id, char letter) throws StateException {
		return this.getTransition(this.states.get(id), letter);
	}

	@Override
	public void setInitial(State s) {
		if(this.initialState != null) throw new IllegalStateException();
		this.initialState = s;
	}

	@Override
	public boolean isInitial(State s) {
		return this.initialState.equals(s);
	}

	@Override
	public void addTransition(State from, Character letter, State to) {
		Key k = new Key(from, letter);
		if(this.delta.containsKey(k)) throw new IllegalStateException();
		this.alphabet.add(letter);
		this.delta.put(k, to);
	}

	@Override
	public boolean accept(String word) throws StateException {
		if(this.initialState == null || word.isEmpty()) return false;
		return this.acceptUtil(this.initialState, word);
	}
	
	private boolean acceptUtil(State currentState, String word){
		if(word.length() == 0)
			return this.isAccepting(currentState);

		State newState = this.getTransition(currentState, word.charAt(0));
		if(newState == null) return false;
		return this.acceptUtil(newState, word.substring(1));
	}


}
