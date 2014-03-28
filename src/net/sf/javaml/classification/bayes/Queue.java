/**
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2012, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 *  
 */
package net.sf.javaml.classification.bayes;

/*************************************************************************
 *  Compilation:  javac Queue.java
 *  Execution:    java Queue
 *
 *  A generic queue, implemented using a linked list. Each queue
 *  element is of type Item.
 *
 *************************************************************************/

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author Lieven Baeyens
 * @author Thomas Abeel
 * 
 */
class Queue<Item> implements Iterable<Item> {
	private int N; // number of elements on queue
	private Node first; // beginning of queue
	private Node last; // end of queue

	// helper linked list class
	private class Node {
		private Item item;
		private Node next;
	}

	// create an empty queue
	Queue() {
		first = null;
		last = null;
	}

	// is the queue empty?
	boolean isEmpty() {
		return first == null;
	}

	int length() {
		return N;
	}

	int size() {
		return N;
	}

	// add an item to the queue
	void enqueue(Item item) {
		Node x = new Node();
		x.item = item;
		if (isEmpty()) {
			first = x;
			last = x;
		} else {
			last.next = x;
			last = x;
		}
		N++;
	}

	// remove and return the least recently added item
	Item dequeue() {
		if (isEmpty())
			throw new RuntimeException("Queue underflow");
		Item item = first.item;
		first = first.next;
		N--;
		return item;
	}

	// string representation (inefficient because of string concatenation)
	@Override
	public String toString() {
		String s = "";
		for (Node x = first; x != null; x = x.next)
			s += x.item + " ";
		return s;
	}

	public Iterator<Item> iterator() {
		return new QueueIterator();
	}

	// an iterator, doesn't implement remove() since it's optional
	private class QueueIterator implements Iterator<Item> {
		private Node current = first;

		public boolean hasNext() {
			return current != null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Item next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Item item = current.item;
			current = current.next;
			return item;
		}
	}
}