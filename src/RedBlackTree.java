// --== File Header Information ==--
// File Name: RedBlackTree.java
// Name: Gavin Austin
// Email: graustin2@wisc.edu

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * This class represents a red black tree. It extends the BinarySearchTree class.
 * @extends BinarySearchTree<T>
 * @param <T> the type of data to be stored in the tree
 */
public class RedBlackTree<T extends Comparable<T>> extends BinarySearchTree<T> {

  /**
   * This class represents a node in a red black tree. It extends the Node class.
   * @param <T> the type of data to be stored in the node
   */
  protected static class RBTNode<T> extends Node<T> {
    public int blackHeight = 0;
    public RBTNode(T data) { super(data); }
    public RBTNode<T> getUp() { return (RBTNode<T>)this.up; }
    public RBTNode<T> getDownLeft() { return (RBTNode<T>)this.down[0]; }
    public RBTNode<T> getDownRight() { return (RBTNode<T>)this.down[1]; }
  }

  /**
   * Recursive method that corrects the red black tree using red black tree
   * properties after an insertion of a node
   * @param node the node to start the correction from
   * @return true if the properties are corrected, false otherwise
   */
  protected boolean enforceRBTreePropertiesAfterInsert(RBTNode<T> node) {
    // New reference fields
    RBTNode<T> aunt = null;
    RBTNode<T> parent = null;
    RBTNode<T> grandparent = null;
    RBTNode<T> brother = null;

    // Base cases to determine parent and root
    if (node.getUp() == null) {
      root = node;
      return true;
    } else {
      parent = node.getUp();
    }

    // Finding aunt reference
    if (node.getUp().isRightChild()) {
      try {
        aunt = node.getUp().getUp().getDownLeft();
      } catch (NullPointerException e) {
        aunt = null;
      }
    } else {
      try {
        aunt = node.getUp().getUp().getDownRight();
      } catch (NullPointerException e) {
        aunt = null;
      }
    }

    // Finding brother reference
    if (node.isRightChild()) {
      try {
        brother = node.getUp().getDownLeft();
      } catch (NullPointerException e) {
        brother = null;
      }
    } else {
      try {
        brother = node.getUp().getDownRight();
      } catch (NullPointerException e) {
        brother = null;
      }
    }

    // Finding grandparent reference
    try {
      grandparent = node.getUp().getUp();
    } catch (NullPointerException e) {
      grandparent = null;
    }

    // Single Rotate case
    if (((node.isRightChild() && !parent.isRightChild())
            || (!node.isRightChild() && parent.isRightChild()))
            && node.blackHeight == 0 && parent.blackHeight == 0
            && (aunt == null || aunt.blackHeight == 1)) {
      rotate(node, parent);

      enforceRBTreePropertiesAfterInsert(parent);

    }

    // Double rotate color swap case
    if (((!node.isRightChild() && !parent.isRightChild())
    || (node.isRightChild() && parent.isRightChild()))
    && node.blackHeight == 0 && parent.blackHeight == 0
    && (aunt == null || aunt.blackHeight == 1)) {
      int tempParentColor = parent.blackHeight;
      int tempGParentColor = grandparent.blackHeight;

      rotate(parent, grandparent);

      grandparent.blackHeight = tempParentColor;
      parent.blackHeight = tempGParentColor;
    }

    // Recolor case
    if (((aunt != null && aunt.blackHeight == 0) && node.blackHeight == 0
            && parent.blackHeight == 0)) {
      parent.blackHeight = 1;
      grandparent.blackHeight = 0;
      aunt.blackHeight = 1;
      node.blackHeight = 0;

      enforceRBTreePropertiesAfterInsert(grandparent);
    }

    return true;
  }

  /**
   * Inserts a new node into the tree
   * @param data the data to be inserted
   * @return true if the data was inserted, false otherwise
   */
  @Override
  public boolean insert(T data) {
    RBTNode<T> node = new RBTNode<T>(data);

    insertHelper(node);
    enforceRBTreePropertiesAfterInsert(node);

    boolean bool = true;
    RBTNode<T> newRoot = node;

    while (newRoot.getUp() != null) {
      newRoot = newRoot.getUp();
    }
    newRoot.blackHeight = 1;

    return true;
  }

  /**
   * JUnit test: Tests that the insertion and recolor works as expected
   */
  @Test
  public void testRecolor() {
    RedBlackTree<Integer> rbt = new RedBlackTree<Integer>();

    // inserts the nodes
    rbt.insert(3);
    rbt.insert(2);
    rbt.insert(1);
    rbt.insert(5);

    RBTNode<Integer> root = (RBTNode<Integer>)rbt.root;

    // checks all the black heights and the level order string to ensure the recolor worked
    if ((root.blackHeight == 1 && root.getDownLeft().blackHeight == 1
            && root.getDownRight().blackHeight == 1
            && root.getDownRight().getDownRight().blackHeight == 0)
            && ((rbt.toLevelOrderString()).equals("[ 2, 1, 3, 5 ]"))) {
    } else {
      // fail JUnit test if it does not work
      System.out.print(root.blackHeight + " " + root.getDownLeft().blackHeight + " " + root.getDownRight().blackHeight
              + " " + root.getDownRight().getDownRight().blackHeight);
      Assertions.fail("Recolor is not correct!");
    }

  }

  /**
   * JUnit test: Tests that the single rotation and color swap insertion works as expected
   */
  @Test
  public void testSingleRotateColorSwap() {
    RedBlackTree<Integer> rbt = new RedBlackTree<Integer>();

    // inserts the nodes
    rbt.insert(7);
    rbt.insert(14);
    rbt.insert(18);
    rbt.insert(23);
    rbt.insert(26);
    rbt.insert(1);
    rbt.insert(9);

    RBTNode<Integer> root = (RBTNode<Integer>)rbt.root;

    // checks all the black heights and the level order string to ensure the
    // single rotate and color swap worked
    if ((root.blackHeight == 1 && root.getDownLeft().blackHeight == 1
            && root.getDownRight().blackHeight == 1
            && root.getDownRight().getDownLeft().blackHeight == 0
            && root.getDownRight().getDownRight().blackHeight == 0)
            && ((rbt.toLevelOrderString()).equals("[ 14, 7, 23, 1, 9, 18, 26 ]"))) {
    } else {
      // fail JUnit test if it does not work
      Assertions.fail("Single rotate and color swap not fixed!");
    }

  }

  /**
   * JUnit test: Tests that the double rotation and color swap insertion works as expected
   */
  @Test
  public void testDoubleRotateColorSwap() {
    // Create new Red Black Tree
    RedBlackTree<Integer> rbt = new RedBlackTree<Integer>();

    // inserts the nodes
    rbt.insert(7);
    rbt.insert(14);
    rbt.insert(18);
    rbt.insert(23);
    rbt.insert(1);
    rbt.insert(11);
    rbt.insert(20);

    RBTNode<Integer> root = (RBTNode<Integer>)rbt.root;

    // checks all the black heights and the level order string to ensure the
    // double rotate and color swap worked
    if ((root.blackHeight == 1 && root.getDownLeft().blackHeight == 1
            && root.getDownRight().blackHeight == 1
            && root.getDownLeft().getDownLeft().blackHeight == 0
            && root.getDownRight().getDownRight().blackHeight == 0
            && root.getDownLeft().getDownRight().blackHeight == 0
            && root.getDownRight().getDownLeft().blackHeight == 0)
            && ((rbt.toLevelOrderString()).equals("[ 14, 7, 20, 1, 11, 18, 23 ]"))) {
    } else {
      // fail JUnit test if it does not work
      Assertions.fail("Double rotate and color swap not fixed!");
    }

  }

  @Test
  public void testLargeTree() {
    // Create new Red Black Tree
    RedBlackTree<Integer> rbt = new RedBlackTree<Integer>();

    // inserts the nodes
    rbt.insert(13);
    rbt.insert(20);
    rbt.insert(1);
    rbt.insert(10);
    rbt.insert(11);
    rbt.insert(5);
    rbt.insert(32);
    rbt.insert(30);
    rbt.insert(25);
    rbt.insert(15);
    rbt.insert(2);
    rbt.insert(35);
    rbt.insert(7);
    rbt.insert(12);
    rbt.insert(8);
    rbt.insert(17);
    rbt.insert(22);
    rbt.insert(27);
    rbt.insert(37);
    rbt.insert(3);
    rbt.insert(6);

    RBTNode<Integer> root = (RBTNode<Integer>)rbt.root;

    // checks all the black heights and the level order string to ensure the
    // methods worked correctly

    // check left subtree
    if ((root.blackHeight == 1 && root.getDownLeft().blackHeight == 1
            && root.getDownLeft().getDownLeft().blackHeight == 0
            && root.getDownLeft().getDownRight().blackHeight == 0
            && root.getDownLeft().getDownLeft().getDownLeft().blackHeight == 1
            && root.getDownLeft().getDownLeft().getDownRight().blackHeight == 1
            && root.getDownLeft().getDownLeft().getDownRight().getDownRight().blackHeight == 0
            && root.getDownLeft().getDownLeft().getDownRight().getDownLeft().blackHeight == 0
            && root.getDownLeft().getDownRight().getDownLeft().blackHeight == 1
            && root.getDownLeft().getDownRight().getDownRight().getDownRight().blackHeight == 0
            && root.getDownLeft().getDownRight().getDownRight().blackHeight == 1
            && root.getDownLeft().getDownRight().blackHeight == 0)) {
    } else {
      // fail JUnit test if it does not work
      Assertions.fail("Large tree test failed!");
    }

    // check right subtree
    if ((root.blackHeight == 1 && root.getDownRight().blackHeight == 1
            && root.getDownRight().getDownRight().blackHeight == 1
            && root.getDownRight().getDownRight().getDownLeft().blackHeight == 0
            && root.getDownRight().getDownRight().getDownRight().blackHeight == 0
            && root.getDownRight().getDownLeft().blackHeight == 0
            && root.getDownRight().getDownLeft().getDownLeft().getDownRight().blackHeight == 0
            && root.getDownRight().getDownLeft().getDownRight().blackHeight == 1
            && root.getDownRight().getDownLeft().getDownLeft().blackHeight == 1
            && root.getDownRight().getDownLeft().getDownRight().getDownLeft().blackHeight == 0
            && root.getDownRight().getDownLeft().getDownRight().getDownRight().blackHeight == 0)) {
    } else {
      // fail JUnit test if it does not work
      Assertions.fail("Large tree test failed!");
    }

    // check level order string
    if ((rbt.toLevelOrderString()).equals("[ 13, 7, 30, 2, 10, 20, 35, 1, 5, 8, 11, 15, 25, " +
            "32, 37, 3, 6, 12, 17, 22, 27 ]")) {
    } else {
      // fail JUnit test if it does not work
      Assertions.fail("Large tree test failed!");
    }
  }

}


