/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.HashSet;

/**
 * Class to represent a Person.
 *
 * Contains parent-children and spouse references.
 * Uses the 'name' to lexicographically compare.
 */
public class Person  implements Comparable<Person> {
    String name;
    private String sex;

    Person spouse;
    Person mother;
    Person father;
    HashSet<Person> children;

    /**
     * Helper method for constructors
     */
    private void initializePerson() {
        this.spouse = null;
        this.mother = null;
        this.father = null;
        this.children = new HashSet<Person>();
    }

    /**
     * Base constructor
     */
    public Person(String name) {
        this.name = name;
        this.initializePerson();
    }

    /**
     * Constructor that also contains sex
     */
    public Person(String name, String sex) {
        this.name = name;
        setSex(sex);
        this.initializePerson();
    }

    /**
     * Setter for sex attribute
     */
    public void setSex(String sex) {
        if (! sex.equals("man") && (! sex.equals("woman"))) {
            throw new RuntimeException("Invalid sex '" + sex + "': Sex can be either 'man' or 'woman'");
        }
        this.sex = sex;
    }

    /**
     * Getter for sex attribute
     */
    public String getSex() {
        return this.sex;
    }

    /**
     * Get siblings.
     *
     * To exclude step-siblings:
     * - If both father and mother are known, consider siblings both the mother's
     *   children and the father's children.
     * - If only father/mother is known, consider siblings the mother's/father's
     *   children.
     */
    public HashSet getSiblings() {
        HashSet<Person> motherChildren, fatherChildren;
        HashSet<Person> siblings = new HashSet<Person>();

        motherChildren = null;
        if (this.mother != null)
            motherChildren = this.mother.children;

        fatherChildren = null;
        if (this.father != null)
            fatherChildren = this.father.children;

        if (motherChildren != null && fatherChildren != null) {
            /* Check that the child is also the father's child to exclude
             * step-siblings from siblings.
             */
            for (Person p: motherChildren) {
                if (fatherChildren.contains(p)) {
                    siblings.add(p);
                }
            }
        } else if (motherChildren != null) {
            siblings.addAll(motherChildren);
        } else if (fatherChildren != null) {
            siblings.addAll(fatherChildren);
        }

        return siblings;
    }

    /**
     * Get parents' siblings which are practically uncles/aunts.
     *
     * Return a HashSet with all siblings of all known parents.
     */
    public HashSet getParentSiblings() {
        HashSet parentSiblings = new HashSet<Person>();
        if (this.father != null)
            parentSiblings.addAll(this.father.getSiblings());
        if (this.mother != null)
            parentSiblings.addAll(this.mother.getSiblings());
        return parentSiblings;
    }

    @Override
    /**
     * Override comparison method to be able and sort collections of people.
     * Lexicographically compare the names.
     */
    public int compareTo(Person p){
        return name.compareTo(p.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
