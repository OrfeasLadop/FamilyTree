/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and.I. open the template in the editor.
 */

/**
 * 1. Are step-siblings considered siblings?
 * 2. Are uncles/aunts that are not first-degree considered valid?
 * 3. Are the spouse of a person considered to have the same nephews/nieces with the person?
 * 4. Are step-cousins considered cousins?
 *
 *
 */

import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.swing.text.Style;
import java.awt.*;
import java.io.File;
import java.text.Format;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import static java.awt.ComponentOrientation.LEFT_TO_RIGHT;

/**
 * Base app class to include main() and the basic methods.
 */
public class App {

    /* XXX: Change to the desired input path */
    String INPUT_PATH = "C:/got.csv";
    /* XXX: Change to the desired output path */
    String OUTPUT_PATH = "C:/valid.csv";

    /* Family tree structure of app */
    Tree tree;

    /**
     * Process each row and populate the family tree.
     *
     * Depending on the row type (2 or 3 params), either add a person and its
     * sex or save the relationship between two people.
     *
     * Use getOrAddPerson() to ensure that we add a person only if it does not
     * already exist in the tree structure (hashtable).
     *
     * Get the lineCnt parameter to display the row of any exception.
     */
    private void processLine(String line, int lineCnt) {
        String[] data = line.split(",");
        String name, sex, relationship;

        switch (data.length) {
            case 2:
                /* Use trim() to remobe leading and trailing whitespace */
                name = data[0].trim();
                Person p = this.tree.getOrAddPerson(name);

                sex = data[1].trim();
                p.setSex(sex);
                break;
            case 3:
                name = data[0].trim();
                Person p1 = this.tree.getOrAddPerson(name);

                name = data[2].trim();
                Person p2 = this.tree.getOrAddPerson(name);

                relationship = data[1].trim();
                Tree.relate(p1, relationship, p2);
                break;
            default:
                throw new RuntimeException("Invalid format in CSV file in line " + lineCnt);
        }
    }

    /**
     * Read from the CSV file and create the family tree in memory.
     *
     * Create the new tree object, read each line from the CSV and use
     * processRow() to process the info of each row to populate the family tree.
     */
    public void loadTree(String path) {
        int lineCnt = 0;
        String line;
        BufferedReader csvReader;

        /* Create a new family tree */
        this.tree = new Tree();

        /* Read rows from CSV */
        try {
            csvReader = new BufferedReader(new FileReader(path));
            while ((line = csvReader.readLine()) != null) {
                processLine(line, lineCnt++);
            }
            csvReader.close();
        } catch (java.io.IOException exc) {
            System.out.println("Cannot load tree: " + exc.toString());
        }
    }

    /**
     * Sort the tree and print it to a file.
     *
     * For each person, sorted in ascending order based on their name, print
     * a line with the name and the sex.
     */
    public void sortAndPrint(String filename) {
        /* Get a sorted list of people. Person.sort() sorts based on name. */
        List<Person> people = this.tree.sort();
        /* Open a text file */
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            /* Iterate over the sorted list and print a line with the name and sex */
            for (Person p: people) {
                writer.println(p.name + " " + p.getSex());
            }
            writer.close();
            System.out.printf("People are sorted in file: %s%n", OUTPUT_PATH);
        } catch (java.io.IOException exc) {
            System.out.printf("Cannot write to file %s: %s\n", filename, exc.toString());
        }
    }

    /**
     * Read two names and find the relationship between those people.
     */
    public void findRelationship() {
        Person p1, p2;

        /* Open stdin to read input */
        Scanner reader = new Scanner(System.in);
        String name, relationship;

        /* Read the first name from stdin until a valid person is found */
        do {
            System.out.print("Give the first name: ");
            name = reader.nextLine();
            p1 = this.tree.getPerson(name);
        } while (p1 == null);

        /* Read the second name from stdin until a valid person is found */
        do {
            System.out.print("Give the second name: ");
            name = reader.nextLine();
            p2 = this.tree.getPerson(name);
        } while (p2 == null);

        /* Deduce their relationship by checking 9 relationship methods */
        if (Tree.isParentOf(p1, p2)) {
            relationship = "parent";
        } else if (Tree.isChildOf(p1, p2)) {
            relationship = "child";
        } else if (Tree.isSiblingOf(p1, p2)) {
            relationship = "sibling";
        } else if (Tree.isCousinOf(p1, p2)) {
            relationship = "cousin";
        } else if (Tree.isSpouseOf(p1, p2)) {
            relationship = "spouse";
        } else if (Tree.isGrandparentOf(p1, p2)) {
            relationship = "grandparent";
        } else if (Tree.isGrandchildOf(p1, p2)) {
            relationship = "grandchild";
        } else if (Tree.isNephewNiceOf(p1, p2)) {
            relationship = "nephew/nice";
        } else if (Tree.isParentSiblingOf(p1, p2)) {
            relationship = "uncle/aunt";
        } else {
            relationship = "unrelated";
        }

        System.out.printf("%s is %s of %s\n", p1.toString(), relationship, p2.toString());
    }

    public void visualize() {

    }

    /**
     * Main loop with text menu
     */
    public void runLoop() {
        int option;
        Scanner reader = new Scanner(System.in);
        do {
            String msg = "Choose between the options:\n"
                    + " 1. Load family tree\n"
                    + " 2. Sort and print\n"
                    + " 3. Find relationship\n"
                    + " 4. Save as DOT and visualize\n"
                    + " 5. Exit\n";
            System.out.println(msg);
            option = reader.nextInt();
            switch (option) {
                case 1:
                    loadTree(INPUT_PATH);
                    break;
                case 2:
                    sortAndPrint(OUTPUT_PATH);
                    break;
                case 3:
                    findRelationship();
                    break;
                case 4:
                    visualize();
                    break;
                default:
            }
        } while (option != 5);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        App app = new App();
        app.runLoop();
    }
}
