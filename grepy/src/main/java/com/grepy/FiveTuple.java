package com.grepy;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class FiveTuple {
    private Integer state = 0;

    private static ArrayList<Character> alphabet = new ArrayList<Character>();
    private ArrayList<String[]> delta = new ArrayList<String[]>();
    private Integer startState = 0;
    private ArrayList<String> acceptingStates;

    public FiveTuple (String fileName) {
        this.setAlphabet(fileName);
    }
    

    public Integer getState() {
        return state;
    }
    public Integer nextState() {
        return ++this.state;
    }
    public void setState(Integer num) {
        this.state = num;
    }
    public ArrayList<Character> getAlphabet() {
        return alphabet;
    }
    public void printAlphabet() {
        for(int i = 0; i < alphabet.size(); i++) {
            System.out.println("\n" + alphabet.get(i));
        }
    }

    public ArrayList<String[]> getDelta() {
        return delta;
    }
    public void setDelta(String[] deltaItem) {
        delta.add(deltaItem);
    }
    public String getStartState() {
        return startState.toString();
    }
    public void setStartState(String startState) {
        this.startState = Integer.parseInt(startState);
    }
    public ArrayList<String> getAcceptingStates() {
        return acceptingStates;
    }
    public void setAcceptingStates(ArrayList<String> acceptingStates) {
        this.acceptingStates = acceptingStates;
    }

    public void removeDelta(String[] remElem) {
        Iterator<String[]> itr = delta.iterator();
        while(itr.hasNext()) {
            String[] item = itr.next();
            if(Arrays.equals(item, remElem)) {
                itr.remove();
                break;
            }
        }

    }

    public void setAlphabet(String fileName) {
        try {
            File file = new File(fileName);    //creates a new file instance  
            FileReader fr = new FileReader(file);   //reads the file  
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream 
            String line;
            while((line = br.readLine()) != null) { 
                for(int i = 0; i < line.length(); i++) {
                    if(!alphabet.contains(line.charAt(i))) {
                        alphabet.add(line.charAt(i));
                    }
                }
            }
            fr.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}
