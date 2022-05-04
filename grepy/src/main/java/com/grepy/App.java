package com.grepy;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public final class App {
    private App() {
    }

    /**
     * Takes in a regular expression, an input file, and optionally an output file for a dfa and a nfa and 
     * prints the lines in the input files that match the regular expression.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        // Regular Expression
        System.out.println("REGEX: " + args[0]);

        // Input File
        System.out.println("INPUT: " + args[1]);

        // Creates the NFA for the regex
        FiveTuple fiveTuple = new FiveTuple(args[1]);
        fiveTuple.getAlphabet();
        regexToNFA(fiveTuple, args[0]);

        // Creates the DFA from the NFA
        DFA dfaTuple = nfaToDfa(fiveTuple);

        // Checks all the lines of the input file for a match for the Regex
        String[] lines = fileReader(args[1]);
        checkStrings(lines, dfaTuple);

        // DFA Output File
        try {
            // System.out.println(args[2]);

            if(!args[2].equals("none")) {
                // Creates a file with the DFA outputted in the DOT format
                createDot(dfaTuple, args[2]);
                System.out.println("DFA: " + args[2]);
            }
        }
        catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("\nNo Output DFA File");
        }

        // NFA Output File
        try {
            // System.out.println(args[3]);

            if(!args[3].equals("none")) {
                // Creates a file with the NFA outputted in the DOT format
                createDot(fiveTuple, args[3]);
                System.out.println("NFA: " + args[3]);
            }
        }
        catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("\nNo Output NFA File");
        }
        
    }

    /**
     * Checks what strings are part of the DFA and prints out all matches
     * @param lines the array of strings containing each line from the input file
     * @param dfaTuple the DFA to check the lines against
     */
    public static void checkStrings(String[] lines, DFA dfaTuple) {
        ArrayList<String[]> dfaDelta = dfaTuple.getDelta();
        ArrayList<String> acceptingStates = dfaTuple.getAcceptingStates();

        // Iterates through each line in the input string to find matches
        for(String line: lines) {
            String currentState = dfaTuple.getStartState();
            // Iterates through each letter in the string and finds a path on the DFA for it to travel on
            for(int count = 0; count < line.length(); count++) {
                String currentLetter = String.valueOf(line.charAt(count));
                Iterator<String[]> itr = dfaDelta.iterator();
                while(itr.hasNext()) {
                    String[] itrItem = itr.next();
                    if(itrItem[0].equals(currentState) && itrItem[1].equals(currentLetter)) {
                        currentState = itrItem[2];                                           
                        break;                                                               
                    }                                                                         
                }
            }
            
            // Prints out the line if the final state is the accepting state
            if(acceptingStates.contains(currentState)) {
                System.out.println("\n" + line);
            }
        }
    }

    /**
     * Converts the NFA to a DFA
     * @param nfaTuple the NFA to be converted
     * @return returns the DFA that the NFA was converted into
     */
    public static DFA nfaToDfa(FiveTuple nfaTuple) {
        DFA dfaTuple = new DFA(nfaTuple);
        ArrayList<Character> alphabet = dfaTuple.getAlphabet();
        ArrayList<String[]> nfaDelta = nfaTuple.getDelta();
        String currentState = nfaTuple.getStartState();
        Queue<String> statesQueue = new PriorityQueue<String>();    // The queue of all the states that need to be checked
        ArrayList<String> statesList = new ArrayList<String>();     // The list of all states that have been checked

        String[] newStartState = epsilonStates(currentState, nfaTuple); // Checks if the start state has any epsilon transitions
        String stateString = String.join(",", newStartState);
        statesList.add(stateString);
        dfaTuple.setStartState(stateString);
        statesQueue.add(dfaTuple.getStartState());

        currentState = stateString; // Sets the start state as the current state

        // Iterates while there still are elements to process in the statesQueue
        while(statesQueue.peek() != null) {
            String[] currentStateArray = statesQueue.peek().split(",", 0); // Sets the first element in the queue as the current state and then splits it up by commas

            // Iterates through every letter in the alphabet
            for(int count = 0; count < alphabet.size(); count++) {

                ArrayList<String> nextStates = new ArrayList<String>();     // List of all the next states in the NFA that the current state in the DFA can go to
                String alphaString = Character.toString(alphabet.get(count));
                // System.out.println("\n" + alphaString);

                // Iterates through every element in the current state array
                for(String state: currentStateArray) {
                    Iterator<String[]> itr = nfaDelta.iterator();
                    while(itr.hasNext()) {
                        String[] itrItem = itr.next();
                        if(itrItem[0].equals(state) && itrItem[1].equals(alphaString) && !nextStates.contains(itrItem[2])) {
                            nextStates.add(itrItem[2]);
                        }
                    }
                }

                // System.out.println("\n" + nextStates.toString());

                // If there are next states it checks for any epsilon paths that go from the next state and adds them to the nextStatesString
                if(!nextStates.isEmpty()) {
                    for(int j = 0; j < nextStates.size(); j++) {
                        String stateToCheck = nextStates.get(j);
                        String[] newEpsilonStates = epsilonStates(stateToCheck, nfaTuple);
                        if(newEpsilonStates != null) {
                            // System.out.println("\nRepeat");
                            for(String newEpsilonState: newEpsilonStates) {
                                // System.out.println("\nKLKL" + nextStates.toString() + " " + newEpsilonState);
                                if(!nextStates.contains(newEpsilonState)) {
                                    nextStates.add(newEpsilonState);
                                }
                            }
                        }
                    }
                
                    String[] nextStatesArray = new String[nextStates.size()];
                    for(int i = 0; i < nextStates.size(); i++) {
                        nextStatesArray[i] = nextStates.get(i);
                    }

                    Arrays.sort(nextStatesArray);
                    String nextStateString = String.join(",", nextStatesArray);


                    // System.out.println("\nHERE" + Arrays.toString(nextStatesArray));

                    String[] deltaData = {currentState, alphaString, nextStateString};
                    dfaTuple.setDelta(deltaData);

                    if(!statesList.contains(nextStateString)) {
                        statesList.add(nextStateString);
                        statesQueue.add(nextStateString);
                    }

                }
                // If there are no next states then it makes a connection for the currentState to the TRAP state
                else {
                    String[] trapDelta = {currentState, alphaString, "TRAP"};
                    dfaTuple.setDelta(trapDelta);
                }
            }

            // Takes out the first element in the statesQueue
            statesQueue.poll();

            // If the statesQueue isn't null, sets the currentState to it's head
            if(statesQueue.peek() != null) {
                currentState = statesQueue.peek();
            }

        }

        for(String stateItem: statesList) {
            dfaTuple.setAllStates(stateItem);
        }

        // Checks if any state in the DFA is formed by one of the accepting states in the NFA, and sets it to an acceptingState if it does
        for(String stateItem: statesList) {
            String[] stateItemArray = stateItem.split(",", 0);
            List<String> stateItemList = Arrays.asList(stateItemArray);
            if(stateItemList.contains(nfaTuple.getAcceptingStates())) {
                if(!dfaTuple.getAcceptingStates().contains(stateItem)) {
                    dfaTuple.setAcceptingStates(stateItem);
                }
            }
        }

        return dfaTuple;
    }

    /**
     * Checks what states have an epsilon transition path to the state to be checked and returns these states
     * @param stateToCheck the state that is currently being checked for epsilon transitions
     * @param nfaTuple the NFA to which the state belongs
     * @return an array of all the states that have an epsilon transition path to stateToCheck
     */
    public static String[] epsilonStates (String stateToCheck, FiveTuple nfaTuple) { // s,nfaTuple
        Queue<String> epsilonQueue = new PriorityQueue<String>();
        ArrayList<String[]> nfaDelta = nfaTuple.getDelta();
        ArrayList<String> epsilonStates = new ArrayList<String>(); // A list of all states connected to stateToCheck (directly or indirectly) via epsilon transition
        epsilonStates.add(stateToCheck);
        epsilonQueue.add(stateToCheck);

        // Iterates while there are states to check
        while(epsilonQueue.peek() != null) {
            Iterator<String[]> itr = nfaDelta.iterator();
            while(itr.hasNext()) {
                String[] itrItem = itr.next();
                if(itrItem[0].equals(epsilonQueue.peek())) {
                    if(itrItem[1].equals("eps")) {
                        if(!epsilonStates.contains(itrItem[2])) {
                            epsilonStates.add(itrItem[2]);
                            epsilonQueue.add(itrItem[2]);
                        }
                    }
                }
            }
            epsilonQueue.poll();
        }

        // Converts the epsilonStates list to an array
        String[] epsilonStatesArray = new String[epsilonStates.size()];
        for(int i = 0; i < epsilonStates.size(); i++) {
            epsilonStatesArray[i] = epsilonStates.get(i);
        }

        // Sorts the array in the ascending order (in-place)
        Arrays.sort(epsilonStatesArray);

        // System.out.println("\nDELTA2: " + Arrays.toString(epsilonStatesArray));

        return epsilonStatesArray;
    }

    /**
     * Convert the regex to an NFA
     * @param nfaTuple the NFA that has to be constructd
     * @param regex the regex that will be used to create the NFA
     */
    public static void regexToNFA(FiveTuple nfaTuple, String regex) {
        ArrayList<Character> alphabet = nfaTuple.getAlphabet();
        nfaTuple.setStartState(nfaTuple.getState().toString());

        int count = 0;
        Stack<Integer> openBracket = new Stack<Integer>();

        while(true) {
            if(count != regex.length()) {
                Character currentLetter = regex.charAt(count);

                // Handles non-special characters
                if(alphabet.contains(currentLetter)) {
                    Integer currentState = nfaTuple.getState();
                    Integer nextState = nfaTuple.nextState();
                    String[] deltaItem = {currentState.toString(), currentLetter.toString(), nextState.toString()};
                    nfaTuple.setDelta(deltaItem);

                    nfaTuple.setAcceptingStates(Integer.toString(nextState));

                    // System.out.println("\n" + currentLetter);
                }

                // Handles the kleene star
                if(currentLetter == '*') {
                    Integer currentState = nfaTuple.getState();
                    Integer nextState = currentState - 1;
                    String[] deltaItem = {currentState.toString(), "eps", nextState.toString()};
                    nfaTuple.setDelta(deltaItem);
                    
                    // System.out.println("\nGRRRRR: " + Integer.toString(currentState) + " | " + Integer.toString(nextState) + " | " + Arrays.toString(deltaItem));
                    nfaTuple.setAcceptingStates(Integer.toString(nextState));

                    // System.out.println("\n*");
                }

                // Handles the OR/+ sign
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

                    nfaTuple.setAcceptingStates(Integer.toString(nextState + 3));

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
                    // SPECIAL CASE: If current state is 1
                    else {
                        nfaTuple.setStartState(Integer.toString(nextState));
                    }

                    nfaTuple.setState(currentState + 4);
                    // System.out.println("\n+");
                }

                // Handles the open bracket
                if(currentLetter == '(') {
                    Integer openBracketNum = nfaTuple.getState();
                    if(regex.charAt(count + 2) == '+') {
                        openBracketNum += 2;
                    }
                    openBracket.push(openBracketNum);
                    // System.out.println("\n(");
                }

                // Handles the closed bracket
                if(currentLetter == ')') {
                    if(regex.charAt(count + 1) == '*') {
                        Integer currentState = nfaTuple.getState();
                        String[] deltaItem1 = {Integer.toString(currentState), "eps", Integer.toString(openBracket.peek())};
                        nfaTuple.setDelta(deltaItem1);
                        String[] deltaItem2 = {Integer.toString(openBracket.peek()), "eps", Integer.toString(currentState)};
                        nfaTuple.setDelta(deltaItem2);
                        count++;
                        nfaTuple.setAcceptingStates(Integer.toString(openBracket.pop()));
                    }
                    // System.out.println("\n)");
                }

                count++;
            }
            else {
                break;
            }
        }

        // System.out.println("\nDELTA3: ");
        // printArrayList(nfaTuple.getDelta());
    }

    /**
     * Reads a file and outputs each line as a string in an array
     * @param fileName the location of the input file
     * @return an array of strings containing each line of the input file
     */
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

        // Converts the list of strings lines to an array of strings arr
        String[] arr = new String[lines.size()];
        Iterator<String> itr = lines.iterator();
        int count = 0;
        while(itr.hasNext()) {
            arr[count] = itr.next();
            count++;
        }

        return arr;
    }

    /**
     * Prints an array list
     * @param arrayList the array list to be printed
     */
    public static void printArrayList(ArrayList<String[]> arrayList) {
        for(String[] arrayItem:arrayList) {
            System.out.println("\n" + Arrays.toString(arrayItem));
        }
    }

    /**
     * Creates a DOT file based on an NFA
     * @param nfaTuple the NFA to be converted to DOT
     * @param fileName the name of the newly created output file
     */
    public static void createDot(FiveTuple nfaTuple, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName))))) {

            writer.write("node [shape=none]; start;");
            writer.newLine();
            writer.write("node [shape=doublecircle]; " + nfaTuple.getAcceptingStates() + ";");
            writer.newLine();
            writer.write("node [shape=circle]; ");
            String nonAcceptingStates = "";
            for(int stateNum = 0; stateNum <= nfaTuple.getState(); stateNum++) {
                if(stateNum != Integer.parseInt(nfaTuple.getAcceptingStates())) {
                    nonAcceptingStates += Integer.toString(stateNum) + ", ";
                }
            }
            nonAcceptingStates = nonAcceptingStates.substring(0, nonAcceptingStates.length() - 2) + ";";
            writer.write(nonAcceptingStates);
            writer.newLine();
            writer.write("start -> " + nfaTuple.getStartState() + ";");
            writer.newLine();
            ArrayList<String[]> nfaDelta = nfaTuple.getDelta();
            for(int count = 0; count < nfaDelta.size(); count++) {
                String[] deltaItem = nfaDelta.get(count);
                writer.write(deltaItem[0] + " -> " + deltaItem[2] + " [label=\"" + deltaItem[1] + "\"];");
                writer.newLine();
            }
        } catch (IOException e) {}
    }

    /**
     * Creates a DOT file based on an DFA
     * @param dfaTuple the DFA to be converted to DOT
     * @param fileName the name of the newly created output file
     */
    public static void createDot(DFA dfaTuple, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName))))) {
            writer.write("node [shape=none]; start;");
            writer.newLine();
            writer.write("node [shape=doublecircle]; ");
            String acceptingStates = "";
            for(String oneState: dfaTuple.getAcceptingStates()) {
                acceptingStates += oneState + ", ";
            }
            acceptingStates = acceptingStates.substring(0, acceptingStates.length() - 2) + ";";
            writer.write(acceptingStates);
            writer.newLine();
            writer.write("node [shape=circle]; ");
            String nonAcceptingStates = "";
            for(String oneState: dfaTuple.getAllStates()) {
                if(!dfaTuple.getAcceptingStates().contains(oneState)) {
                    nonAcceptingStates += oneState + ", ";
                }
            }
            nonAcceptingStates = nonAcceptingStates.substring(0, nonAcceptingStates.length() - 2) + ";";
            writer.write(nonAcceptingStates);
            writer.newLine();
            writer.write("start -> " + dfaTuple.getStartState() + ";");
            writer.newLine();
            ArrayList<String[]> dfaDelta = dfaTuple.getDelta();
            for(int count = 0; count < dfaDelta.size(); count++) {
                String[] deltaItem = dfaDelta.get(count);
                writer.write(deltaItem[0] + " -> " + deltaItem[2] + " [label=\"" + deltaItem[1] + "\"];");
                writer.newLine();
            }
        } catch (IOException e) {}
    }
}
