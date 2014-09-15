/*
Written by: Thomas Spooner
Email: thomas.spooner@colorado
CSCI-4830-005
9/12/14
*/

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SeqAlign {

    /*Structure to hold info about each pair of letters
        Contains value of longest common subsequence,
        parent node(s), and direction of traversal
    */
    class Node {
        int number;
        int value;
        String direction;
        List<String> prev = new ArrayList<String>();
        Node(int number, int value) {
            this.number = number;
            this.value = value;
        }
    }

    //List of all match strings containing gaps
    public static List<String> strings = new ArrayList<String>();

    //List containing the LCS table
    private List<Node> table2;

    private int length;
    private int width;

    //Value of the string pair LCS
    private int lcsInt;

    private String str1;
    private String str2;

    SeqAlign(String string1, String string2) {
        this.str1 = string1;
        this.str2 = string2;
        this.length = string1.length();
        this.width = string2.length();
        table2 = new ArrayList<Node>();

        //Initialize table full of 0 values
        for(int i = 0; i < (length + 1) * (width + 1); i++) {
            Node newNode = new Node(i, 0);
            table2.add(newNode);
        }

        lcsInt = lcs();

        //Keep track of location for backtrack purposes
        LinkedList<String> path = new LinkedList<String>();
        backtrack(str1, str2, length, width, (length + 1) * (width + 1) - 1, path);
    }

    public int lcs() {
        int i, j;
        for(i = 1; i < (width + 1); i++) {
            for(j = 1; j < (length + 1); j++) {
                int index = i * (length + 1) + j;

                //If strings are equal, add diagonal node lcs
                if (str1.charAt(j - 1) == str2.charAt(i - 1)) {
                    table2.get(index).value = 1 + table2.get(index - length - 2).value;
                    table2.get(index).prev.add("diagonal");
                    if(table2.get(index).value == table2.get(index - 1).value) {
                        table2.get(index).prev.add("left");
                    }
                }
                else {
                    if(table2.get(index - 1).value > table2.get(index - length - 1).value) {
                        table2.get(index).value = table2.get(index - 1).value;
                        table2.get(index).prev.add("left");
                    }
                    else if(table2.get(index - length - 1).value > table2.get(index - 1).value) {
                        table2.get(index).value = table2.get(index - length - 1).value;
                        table2.get(index).prev.add("up");
                    }
                    else {
                        table2.get(index).value = table2.get(index - 1).value;
                        table2.get(index).prev.add("left");
                        table2.get(index).prev.add("up");
                    }
                }
            }
        }
        return table2.get((1 + length) * (width + 1) - 1).value;
    }

    public void printTable() throws IOException {
        PrintWriter out = new PrintWriter("output.txt", "UTF-8");

        for(int i = 0; i < width + 1; i++) {
            for(int j = 0; j < length + 1; j++) {
                int index = i * (length + 1) + j;
                out.print(table2.get(index).value + " ");
            }
            out.println();
        }
        out.close();
    }

    public void getLcs() {
        System.out.println("The lcs of these 2 strings is: " + lcsInt);
    }

    public void backtrack(String string1, String string2, int len, int wid, int bottom, LinkedList<String> path) {
        if (len == 0 || wid == 0) {
            String out = "";
            for (String s : path) {
                out += s;
            }
            strings.add(out);
            return;
        }
        else if (string1.charAt(len - 1) == string2.charAt(wid - 1)) {
            path.addFirst(string1.charAt(len - 1) + "");
        }
        for(String s : table2.get(bottom).prev) {
            //Recurse to left node, add '-' if necessary
            if(s == "left") {
                if(table2.get(bottom).value == table2.get(bottom - 1).value)
                    path.addFirst("-");

                backtrack(string1, string2, len - 1, wid, bottom - 1, path);
            }
            //Recurse to up/left node
            if(s == "diagonal") {
                backtrack(string1, string2, len - 1, wid - 1, bottom - length - 2, path);
            }
            //Backtrack branch by removing nodes from path
            path.removeFirst();
        }
    }

    public void printMatch(String string1, String string2) {
        System.out.println(string1);
        for(int i = 0; i < string2.length(); i++) {
            if (string1.charAt(i) == string2.charAt(i))
                System.out.print("|");
            else
                System.out.print(" ");
        }
        System.out.println();
        System.out.println(string2);
    }

    public static void main(String[] args) throws IOException{
	    Scanner keyboardInput = new Scanner(System.in);

        System.out.println("Enter first string: ");
        String string1 = keyboardInput.next();
        System.out.println("Enter second string: ");
        String string2 = keyboardInput.next();

        if (string2.length() > string1.length()) {
            String temp;
            temp = string1;
            string1 = string2;
            string2 = temp;
        }

        SeqAlign table = new SeqAlign(string1, string2);

        table.getLcs();
        table.printTable();

        for(String s : strings) {
            table.printMatch(s, string1);
            System.out.println();
        }
    }
}
