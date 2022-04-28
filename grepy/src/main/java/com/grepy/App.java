package com.grepy;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {


        System.out.println("TEST");

        // Regular Expression
        System.out.println(args[0]);

        // Input File 
        System.out.println(args[1]);

        // Output File
        try {
            System.out.println(args[2]);
        }
        catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("\nNo Output File");
        }

        FiveTuple fiveTuple = new FiveTuple(args[1]);

        fiveTuple.getAlphabet();
        regexToNFA(fiveTuple, args[0]);

        String[] lines = fileReader(args[1]);

        // for(String line: lines) {
        //     System.out.println("\nLINES: " + line);
        // }
        
    }

    public static DFA nfaToDfa(FiveTuple nfaTuple) {
        DFA dfaTuple = new DFA(nfaTuple);
        ArrayList<String[]> nfaDelta = nfaTuple.getDelta();
        String currentState = nfaTuple.getStartState();
        String nextState = "";
        Queue<String> statesQueue = new PriorityQueue<String>();

        String newStartState = epsilonStates(currentState, nfaTuple);
        statesQueue.add(newStartState);

        while(statesQueue.peek() != null) {
            String[] deltaItem = new String[3];
            deltaItem[0] = statesQueue.peek();
            String[] statesToCheck = statesQueue.poll().split(",", 0); // s,f
            for(String stateToCheck: statesToCheck) {
                nextState += epsilonStates(stateToCheck, nfaTuple);
            }

            dfaTuple.setDelta(deltaItem);
        }

        return dfaTuple;
    }

    public static String epsilonStates (String stateToCheck, FiveTuple nfaTuple) { // s,nfaTuple
        Queue<String> epsilonQueue = new PriorityQueue<String>();
        ArrayList<String[]> nfaDelta = nfaTuple.getDelta();
        String epsilonStates = stateToCheck;
        epsilonQueue.add(stateToCheck);

        while(epsilonQueue.peek() != null) {
            Iterator<String[]> itr = nfaDelta.iterator();
            while(itr.hasNext()) {
                String[] itrItem = itr.next();
                if(itrItem[0] == epsilonQueue.peek()) {
                    if(itrItem[1] == "eps") {
                        epsilonStates += "," + itrItem[2];
                        epsilonQueue.add(itrItem[2]);
                    }
                }
            }
            epsilonQueue.poll();
        }

        return epsilonStates;
    }


    public static void regexToNFA(FiveTuple nfaTuple, String regex) {
        ArrayList<Character> alphabet = nfaTuple.getAlphabet();
        nfaTuple.setStartState(nfaTuple.getState().toString());

        int count = 0;
        int openBracket = -1;

        while(true) {
            if(count != regex.length()) {
                Character currentLetter = regex.charAt(count);
                if(alphabet.contains(currentLetter)) {
                    Integer currentState = nfaTuple.getState();
                    Integer nextState = nfaTuple.nextState();
                    String[] deltaItem = {currentState.toString(), currentLetter.toString(), nextState.toString()};
                    nfaTuple.setDelta(deltaItem);

                    // System.out.println("\n" + currentLetter);
                }
                if(currentLetter == '*') {
                    Integer currentState = nfaTuple.getState();
                    Integer nextState = currentState - 1;
                    String[] deltaItem = {currentState.toString(), "eps", nextState.toString()};
                    nfaTuple.setDelta(deltaItem);

                    // System.out.println("\n*");
                }
                if(currentLetter == '+') {
                    Integer currentState = nfaTuple.getState(); // 1
                    Integer nextState = nfaTuple.nextState(); // 2
                    Character nextChar = regex.charAt(++count);

                    String[] deltaItem1 = {Integer.toString(currentState + 1), "eps", Integer.toString(nextState - 2)};
                    nfaTuple.setDelta(deltaItem1);
                    String[] deltaItem2 = {Integer.toString(currentState + 1), "eps", Integer.toString(nextState + 1)};
                    nfaTuple.setDelta(deltaItem2);
                    String[] deltaItem3 = {Integer.toString(currentState + 2), nextChar.toString(), Integer.toString(nextState + 2)};
                    nfaTuple.setDelta(deltaItem3);
                    String[] deltaItem4 = {Integer.toString(currentState), "eps", Integer.toString(nextState + 3)};
                    nfaTuple.setDelta(deltaItem4);
                    String[] deltaItem5 = {Integer.toString(currentState + 3), "eps", Integer.toString(nextState + 3)};
                    nfaTuple.setDelta(deltaItem5);

                    if(currentState - 2 >= 0) {
                        String[] deltaItem6 = {Integer.toString(currentState - 2), Character.toString(regex.charAt(count - 4)), Integer.toString(nextState)};
                        nfaTuple.setDelta(deltaItem6);
                        String[] remElem1 = {Integer.toString(currentState - 2), Character.toString(regex.charAt(count - 4)), Integer.toString(nextState - 2)};
                        nfaTuple.removeDelta(remElem1);

                        int backCount = count - 3;
                        String special = "+*()";
                        // System.out.println("\nYEAH" + regex.charAt(backCount));
                        if(special.indexOf(regex.charAt(backCount)) == 1) {
                            // System.out.println("\nHERE");
                            String[] deltaItem7 = {Integer.toString(nextState), "eps", Integer.toString(currentState - 2)};
                            nfaTuple.setDelta(deltaItem7);
                            // System.out.println("\nTXT" + Integer.toString(nextState - 2) + "eps" + Integer.toString(currentState - 2));
                            String[] remElem2 = {Integer.toString(nextState - 2), "eps", Integer.toString(currentState - 2)};
                            nfaTuple.removeDelta(remElem2);
                        }
                    }
                    else {
                        nfaTuple.setStartState(Integer.toString(nextState));
                    }

                    nfaTuple.setState(currentState + 4);
                    // System.out.println("\n+");
                }
                if(currentLetter == '(') {
                    openBracket = nfaTuple.getState();
                    if(regex.charAt(count + 2) == '+') {
                        openBracket += 2;
                    }
                    // System.out.println("\n(");
                }
                if(currentLetter == ')') {
                    if(regex.charAt(count + 1) == '*') {
                        Integer currentState = nfaTuple.getState();
                        String[] deltaItem1 = {Integer.toString(currentState), "eps", Integer.toString(openBracket)};
                        nfaTuple.setDelta(deltaItem1);
                        count++;
                    }
                    // System.out.println("\n)");
                }

                count++;
            }
            else {
                break;
            }
        }
        nfaTuple.setAcceptingStates(Integer.toString(nfaTuple.getState()));

        System.out.println("\nDELTA: ");
        printArrayList(nfaTuple.getDelta());
    }

    public static String[] fileReader(String fileName) {
        ArrayList<String> lines = new ArrayList<String>();

        try {
            File file = new File(fileName);    //creates a new file instance  
            FileReader fr = new FileReader(file);   //reads the file  
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream 
            String line;
            while((line = br.readLine()) != null) { 
                lines.add(line);
            }
            fr.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        String[] arr = new String[lines.size()];
        Iterator<String> itr = lines.iterator();
        int count = 0;
        while(itr.hasNext()) {
            arr[count] = itr.next();
            count++;
        }

        return arr;
    }

    public static void printArrayList(ArrayList<String[]> arrayList) {
        for(String[] arrayItem:arrayList) {
            System.out.println("\n" + Arrays.toString(arrayItem));
        }
    }

    /*
    public static String[] checkNFA(FiveTuple nfaTuple, String[] lines) {
        for(String line: lines) {
            for(int i = 0; i < line.length(); i++) {
                String currentLetter = Character.toString(line.charAt(i));

            }
        }

        return null;
    }
    */
}