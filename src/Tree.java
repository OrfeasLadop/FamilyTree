/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

/**
 * Family tree structure: practically a hash table with custom methods
 */
public class Tree {

    /* The hash table to store name -> person mappings */
    HashMap<String, Person> tree;

    /**
     * Base constructor that creates the hash table.
     */
    public Tree() {
        this.tree = new HashMap<String, Person>();
    }

    /**
     * Get a person from the family tree (hash table) if it exists.
     *
     * Return null if the person does not exist.
     */
    public Person getPerson(String name) {
        return this.tree.get(name);
    }

    /**
     * Get a person from the family tree (hash table) or add it if it does not
     * exist.
     *
     * Return a reference to the person.
     */
    public Person getOrAddPerson (String name) {
        Person p;
        p = this.tree.get(name);
        if (p == null) {
            p = new Person(name);
            this.tree.put(name, p);
        }
        return p;
    }

    /**
     * Sort all people in the family tree based on their name.
     *
     * Return a sorted list of persons.
     */
    public List sort() {
        /* Create a intermediate list of people so that we can sort it */
        List<Person> people = new ArrayList<>(this.tree.values());
        Collections.sort(people);
        return people;
    }

    /**
     * Static method to add relationship info and connections to persons.
     */
    public static void relate(Person p1, String relationship, Person p2) {
        switch (relationship) {
            case "spouse":
                setCouple(p1, p2);
                break;
            case "mother":
                p1.setSex("woman");
                setMother(p1, p2);
                break;
            case "father":
                p1.setSex("man");
                setFather(p1, p2);
                break;
            default:
                throw new RuntimeException("Invalid relationship " + relationship);
        }
    }

    private static void setCouple(Person p1, Person p2) {
        p1.spouse = p2;
        p2.spouse = p1;
    }

    private static void setMother(Person mother, Person child) {
        child.mother = mother;
        mother.children.add(child);
    }

    private static void setFather(Person father, Person child) {
        child.father = father;
        father.children.add(child);
    }

    /*
     * Methods to check relationships
     */

    public static boolean isParentOf(Person p1, Person p2) {
        return p2.mother == p1 || p2.father == p1;
    }

    public static boolean isChildOf(Person p1, Person p2) {
        /* To check if p1 is child of p2, equivalently check if p2 is parent of p1 */
        return isParentOf(p2, p1);
    }

    /**
     * Check if siblings.
     *
     * To check if they are siblings check if they have either the same
     * mother or the same father. Do not consider the step-siblings as
     * siblings.
     * Do not consider 2 people that both have an unknown father/mother as siblings.
     */
    public static boolean isSiblingOf(Person p1, Person p2) {

        boolean fatherSideSiblings = (p1.father == p2.father && p1.father != null);
        boolean motherSideSiblings = (p1.mother == p2.mother && p1.mother != null);
        return fatherSideSiblings || motherSideSiblings;
    }

    /**
     * Check if cousins.
     *
     * Find a person's cousins by finding all of its uncles/aunts and then
     * compiling a set with all of their children. Finally test if the second
     * person belongs to that set.
     * Consider only the first-degree cousins and ignore step-cousins.
     */
    public static boolean isCousinOf(Person p1, Person p2) {
        HashSet<Person> parentSiblings = p1.getParentSiblings();

        /* Save all of the uncles'/aunts' children */
        HashSet children = new HashSet<Person>();
        for (Person ps: parentSiblings) {
            children.addAll(ps.children);
        }

        return children.contains(p2);
    }

    public static boolean isSpouseOf(Person p1, Person p2) {
        return p1.spouse == p2;
    }

    /**
     * Check if grandparent.
     *
     * Perform null checks to avoid null references on unknown parents.
     */
    public static boolean isGrandparentOf(Person p1, Person p2) {
        boolean fatherSideGrandparent = false;
        if (p2.father != null) {
            fatherSideGrandparent = p1 == p2.father.father || p1 == p2.father.mother;
        }
        boolean motherSideGrandparent = false;
        if (p2.mother != null) {
            motherSideGrandparent = p1 == p2.mother.father || p1 == p2.mother.mother;
        }
        return fatherSideGrandparent || motherSideGrandparent;
    }

    public static boolean isGrandchildOf(Person p1, Person p2) {
        /* To check if p1 is grandchild of p2, equivalently check if p2 is grandparent of p1 */
        return isGrandparentOf(p2, p1);
    }

    /**
     * Check if uncle/aunt.
     *
     * Consider only the first-degree uncles/aunts and ignore spouses.
     */
    public static boolean isParentSiblingOf(Person p1, Person p2) {
        return p2.getParentSiblings().contains(p1);
    }

    public static boolean isNephewNiceOf(Person p1, Person p2) {
        /* To check if p1 is nephew/nice of p2, equivalently check if p2 is uncle/aunt of p1 */
        return isParentSiblingOf(p2, p1);
    }
}
