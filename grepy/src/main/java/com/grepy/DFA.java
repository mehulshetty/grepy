package com.grepy;
import java.util.ArrayList;

public class DFA {
    private String state;
    private String stateTracker = "0";
    private static ArrayList<Character> alphabet = new ArrayList<Character>();
    private ArrayList<String[]> delta = new ArrayList<String[]>();
    private String startState;
    private ArrayList<String> acceptingStates = new ArrayList<String>();;

    public DFA (FiveTuple nfaTuple) {
        alphabet = nfaTuple.getAlphabet();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateTracker() {
        return stateTracker;
    }

    public void setStateTracker(String stateTracker) {
        this.stateTracker = stateTracker;
    }

    public ArrayList<Character> getAlphabet() {
        return alphabet;
    }

    public static void setAlphabet(ArrayList<Character> alphabet) {
        DFA.alphabet = alphabet;
    }

    public ArrayList<String[]> getDelta() {
        return delta;
    }

    public void setDelta(String[] deltaItem) {
        this.delta.add(deltaItem);
    }

    public String getStartState() {
        return startState;
    }

    public void setStartState(String startState) {
        this.startState = startState;
    }

    public ArrayList<String> getAcceptingStates() {
        return acceptingStates;
    }

    public void setAcceptingStates(String acceptingState) {
        this.acceptingStates.add(acceptingState);
    }
}
