//===========================================================================================================================
//	Program : SkipListImpl
//===========================================================================================================================
//	@author: Karthika Karunakaran
// 	Date created: 2016/11/05
//===========================================================================================================================
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Random;
// Skeleton for skip list implementation.

public class SkipListImpl<T extends Comparable<? super T>> implements SkipList<T> {
	/** Class Entry holds a single node of the list */
	public class Entry<T> {
		public T element;
		public int level;
		public Entry<T>[] next;
		public Integer[] width;
		
		Entry(T x, int level) {
			this.element = x;
			this.level = level + 1;
			next = (Entry[])Array.newInstance(Entry.class, this.level);
			width = new Integer[this.level];
		}
	}
	
	// Dummy header is used.  tail stores reference of tail element of list
	public Entry<T> head, tail;
	private int size;
	private T last;
	static private int MAXLEVEL = 10;
	public SkipListImpl() {
		head = new Entry<>(null, MAXLEVEL);
		tail = new Entry<>(null, MAXLEVEL);
		size = 0;
		last = null;
		for (int i = 0; i < head.level; i++) {
			head.next[i] = tail;
			head.width[i] = 1;
		}
		/* for (int i = 0; i < head.level; i++) { head.width[i] = 1; }*/
	}
	
	/** Procedures to random choice level
	 * Runs in time O(maxlevel) 
	 * @param x : maxlevel
	 * @return maxlevel : int, new max level at random
	 */
	int levelChoice (int maxLevel) {
		int l = 0;
		Random rand = new Random();
		//P (choosing level of i) = 1/2 P (Choosing level of i - 1)
		while (l < maxLevel) {
			if (rand.nextBoolean())
				break;
			else
				l++;
		}
		return l;
	}
	
	/** Procedures to perform find previous or that element
	 * Runs in time O(logn) 
	 * @param x : element to find
	 * @return prev : Entry[] of previous or that element
	 */ 
	Entry<T>[] find (T x) {
		Entry<T> p = head;
		Entry<T>[] prev = (Entry[])Array.newInstance(Entry.class, head.level);
		for (int i = 0; i < head.level; i++) {
			prev[i] = head;
		}
		if (isEmpty()) return prev;
		for (int i = p.level - 1; i >= 0; i--) {
			while (p.next[i] != tail && p.next[i].element.compareTo(x) < 0) {
				p = p.next[i];
			}
			prev[i] = p;
		}
		return prev;
	}

	  /** Procedures to perform find element by index
		 * Runs in time O(logn) 
		 * @param n : position to find
		 * @return elemetnt : T at that position
		 */
	 public T findIndex(int n) {
	     Entry<T> node = head;
	     n = n + 1;                           // don't count the head as a step
	     for(int level = head.level - 1; level >= 0 ; level--) {
	          while (node != tail && n >= node.width[level]) { // if next step is not too far
	              n = n - node.width[level];  // subtract the current width
	              node = node.next[level];    // traverse forward at the current level
	          }
	     }
	     //System.out.println("***" + node.element);
	     return node.element;
	  }
	
	 /** Procedures to perform add
		 * Runs in time O(logn) 
		 * @param x : T to add
		 * @return true/false : boolean added or replaced
		 */
    @Override
    public boolean add(T x) {
    	int maxLevel = 0;
    	Entry<T>[] prev = find (x);
    	if (prev[0].next[0] != tail && prev[0].next[0].element.compareTo(x) == 0) {
    		prev[0].next[0].element = x;
    		return false;
    	} else {
    		maxLevel = levelChoice(MAXLEVEL);
    		Entry<T> newNode = new Entry<T>(x, maxLevel);
    		
    		for (int i = 0 ;i < newNode.level; i++) {
    			newNode.next[i] = prev[i].next[i];
    			prev[i].next[i] = newNode;
    		}
    		if (newNode.next[0] == tail)
    			last = newNode.element;
    		//Updating the width of stooping down levels
    		updateLevel(newNode, prev, newNode.level);
    	}
    	this.size++;
    	if((int)Math.pow(2, MAXLEVEL) < size ) { 
    		rebuild();	//if ((int)Math.pow(2, MAXLEVEL) < size )
    	}
		
    	return true;
    }
    
    /** Procedures to perform update of weights after add
   	 * @param newNode : Entry<T> : new node added
   	 * @param prev : Entry[] : prev pointers
   	 * @param newLevel : int : length of prev
   	 */  
    private void updateLevel(Entry<T> newNode, Entry<T>[] prev, int newLevel) {
    	for (int i = 0; i <= MAXLEVEL; i++) {
    		//Nodes passing above the new node level
    		if (i > newLevel - 1) {
    			prev[i].width[i] = prev[i].width[i] + 1; 
    		} else {
    			//Nodes passing through the new node level
    				if (i == 0) {
    					newNode.width[i] = prev[i].width[i];
    					prev[i].width[i] = 1;
    				}
    			   else {
    				   int prevWidth = prev[i].width[i];
    				   if (prev[i].element == prev[i - 1].element) {
    				    	prev[i].width[i] = prev[i - 1].width[i - 1];
    				    	newNode.width[i] = prevWidth - prev[i].width[i] + 1;
    				   } else {
    					   Entry<T> down = prev[i].next[0];
    					   int gap = 1;
    					   while (down != newNode) {
    						   down = down.next[0];
    						   gap++;
    					   }
    					   prev[i].width[i] = gap;
    					   newNode.width[i] = prevWidth - prev[i].width[i] + 1;
    				   }
	    		  }
    			}
    	}
	}

    /** Procedures to perform update of weights after remove
   	 * @param newNode : Entry<T> : new node added
   	 * @param prev : Entry[] : prev pointers
   	 * @param newLevel : int : length of prev
   	 */  
    private void updateLevelRemove(Entry<T> newNode, Entry<T>[] prev, int newLevel) {
    	for (int i = 0; i <= MAXLEVEL; i++) {
    		//Nodes passing above the new node level
    		if (i > newLevel - 1) {
    			prev[i].width[i] = prev[i].width[i] - 1; 
    		} else {
    			//Nodes passing through the new node level
    			prev[i].width[i] = 	prev[i].width[i] + newNode.width[i] - 1;
    		}
    	}
	}
    
    /** Procedures to perform remove
	 * Runs in time O(logn) 
	 * @param x : T to remove
	 * @return x : T this is removed
	 */
	@Override
    public T remove(T x) {
    	if (isEmpty()) return null;
    	Entry<T>[] prev = find (x);
    	Entry<T> curr = prev[0].next[0];
    	if (curr != tail && curr.element.compareTo(x) == 0) {
    		updateLevelRemove(curr, prev, curr.level);
    		for (int i = 0; i < head.level; i++) {
    			
    			if (prev[i].next[i] == curr)
    				prev[i].next[i] = curr.next[i];
    			else {
    				break;
    			}
    		}
    		if (curr.next[0] == tail)
    			last = prev[0].element;
    		size--;
    		return curr.element;
    	} else
    		return null;
    }
    
	/** Procedures to perform ceiling
	 * Runs in time O(logn) 
	 * @param x : T to find ceiling for
	 * @return x : T is the ceiling
	 */	
    @Override
    public T ceiling(T x) {
    	if (isEmpty()) return null;
    	Entry<T>[] prev = find (x);
    	if (prev[0].next[0] != tail && prev[0].next[0].element.compareTo(x) >= 0) {
    		return prev[0].next[0].element;
    	} else
			return null;
    }

    /** Procedures to perform contains
	 * Runs in time O(logn) 
	 * @param x : T to check contains
	 * @return true/false : boolean contains or not
	 */
    @Override
    public boolean contains(T x) {
    	if (isEmpty()) return false;
    	Entry<T>[] prev = find(x);
    	if (prev[0].next[0] != tail && prev[0].next[0].element.compareTo(x) == 0) {
    		 return true;
    	}
    	return false;
    }
    
    /** Procedures to find first element
	 * Runs in time O(1) 
	 * @return x : T is the first element
	 */
    @Override
    public T first() {
    	return head.next[0].element;
    }

    /** Procedures to perform flooring
	 * Runs in time O(logn) 
	 * @param x : T to find flooring for
	 * @return x : T is the flooring
	 */
    @Override
    public T floor(T x) {
    	if (isEmpty()) return null;
    	Entry<T>[] prev = find (x);
    	if (prev[0].next[0] != tail && prev[0].next[0].element.compareTo(x) == 0) {
    		return prev[0].next[0].element;
    	} else {
    		return prev[0].element;
    	}
    }

    /** Procedures to check isEmpty
	 * Runs in time O(1) 
	 * @return true/false : boolean to return isEmpty or not
	 */
    @Override
    public boolean isEmpty() {
    	if (head.next[0] == tail) return true;
    	else return false;
    }

    /** Procedures to get iterator 
	 * Runs in time O(logn) 
	 * @return iterator : Iterator<T> for the object
	 */
    @Override
    public Iterator<T> iterator() {
    	return new IteratorClass<>(head);
    }

    public class IteratorClass<T> implements Iterator<T> {
    	Entry<T> curr, prev;
    	
		public IteratorClass(Entry<T> head) {
			this.curr = head.next[0];
			this.prev = null;
		}

		@Override
		public boolean hasNext() {
			if (curr != tail) return true;
			else return false;
		}

		@Override
		public T next() {
			if (curr != tail) {
				prev = curr;
				curr = curr.next[0];
			} else {
				prev = null;
			}
			return prev.element;
		}
    	
    }
    
    /** Procedures to find last element
	 * Runs in time O(1) 
	 * @return x : T is the last element
	 */
    @Override
    public T last() {
    	if (isEmpty()) return null;
    	return last;
    }

    /** Procedures to rebuild
   	 */
    @Override
    public void rebuild() {
    	//MaxLevel should be approx. = log(N)
    	int k = (int) Math.ceil((Math.log(size + 1) / Math.log(2)));
		MAXLEVEL = k;
		SkipListImpl<T> reOrgSkipList = new SkipListImpl<>();
		reCreateSkipList(reOrgSkipList, this.head, 0, size - 1, k);
		this.head = reOrgSkipList.head;
		this.tail = reOrgSkipList.tail;
    }

    /** Procedures to recreate skiplist
	 * Runs in time O(logn) 
	 * @param : reOrgSkipList : SkipListImpl<T> 
	 * @param : head : Entry<T> 
     * @param : p : int
     * @param : r : int
     * @param : k : int
	 */
    public void reCreateSkipList(SkipListImpl<T> reOrgSkipList, Entry<T> head, int p, int r, int k) {
    	int q = 0;
    	T element = null;
    	if (p <= r) {
    		if (k == 0) {
    			Iterator<T> it = this.iterator();
    			while (it.hasNext()) {
    				element = it.next();
    				reOrgSkipList.addElement(reOrgSkipList.head, element, 0);
    				reOrgSkipList.size++;
    			}
    		} else {
    			q = ( p + r ) / 2;
    			element = findIndex(q);
    			if (element != null) {
    				reOrgSkipList.addElement(reOrgSkipList.head, element, k);
    				reOrgSkipList.size++;
    			}
    			
    			reCreateSkipList(reOrgSkipList, reOrgSkipList.head, p, q - 1, k - 1);
    			reCreateSkipList(reOrgSkipList, reOrgSkipList.head, q + 1, r, k - 1);
    		}
    	}
    }
    
    public void addElement(Entry<T> head, T element, int maxLevel) {
    	Entry<T>[] prev = findElement (head, element);
    	if (prev[0].next[0] != tail && prev[0].next[0].element.compareTo(element) == 0) {
    		prev[0].next[0].element = element;
    	} else {
			Entry<T> newNode = new Entry<T>(element, maxLevel);
			for (int i = 0 ;i < newNode.level; i++) {
				newNode.next[i] = prev[i].next[i];
				prev[i].next[i] = newNode;
			}
			if (newNode.next[0] == tail)
    			last = newNode.element;
    		//Updating the width of stooping down levels
    		updateLevel(newNode, prev, newNode.level);
    	}
    	this.size++;
    }
    
    Entry<T>[] findElement(Entry<T> head, T x) {
		Entry<T> p = head;
		Entry<T>[] prev = (Entry[])Array.newInstance(Entry.class, head.level);
		for (int i = 0; i < head.level; i++) {
			prev[i] = head;
		}
		if (isEmpty()) return prev;
		for (int i = p.level - 1; i >= 0; i--) {
			while (p.next[i] != tail && p.next[i].element.compareTo(x) < 0) {
				p = p.next[i];
			}
			prev[i] = p;
		}
		return prev;
	}
    
   /* @Override
    public void rebuild() {
    	//MaxLevel should be approx. = log(N)
    	int k = (int) Math.ceil((Math.log(size) / Math.log(2)));
		MAXLEVEL = k; 
		SkipListImpl<T> reOrgSkipList = new SkipListImpl<>();
		 //arr of size + 2 , p = 1, r = size
		Entry[] arr = new Entry[size];
		
		makeArray (arr, 0, size - 1, k);
		reOrgSkipList.makeLink (arr);
		
		head = reOrgSkipList.head;
		tail = reOrgSkipList.tail;
		size = reOrgSkipList.size;
    }
    
    public void makeArray(Entry<T>[] arr, int p, int r, int k) {
    	int q = 0;
    	T element;
    	if (p <= r) {
	    	if (k == 0) {
		    	for (int i = p; i < r; i++) {
		    		element = findIndex(i);
		    		Entry<T> newNode = new Entry<T>(element, 0);
		    		arr[i] = newNode;
		    	}
	    	} else {
	    		q = ( p + r ) / 2;
	    		element = findIndex(q);
	    		Entry<T> newNode = new Entry<T>(element, k);
	    		arr[q] = newNode;
	    		makeArray(arr, p, q - 1, k - 1);
	    		makeArray(arr, q + 1, r, k - 1);
	    	}
    	}
    }
    
    public void makeLink(Entry[] arr) {
    	Entry[] array = new Entry[arr.length + 2];
    	array[0] = this.head;
    	array[arr.length + 1] = this.tail;
    	
    	System.arraycopy(arr, 0, array, 1, arr.length);
    	for (int i = 0; i < array.length; i++) {
	    	for (int j = 0; j < array[i].level; j++) {
	    		
	    		array[i].next[j] = array[i + (int)Math.pow(2, j)];
		    	array[i].width[j] =  (int)Math.pow(2, j);
	    	}
    	}
    } */
    
    /** Procedures to get size
 	 * Runs in time O(1) 
 	 * @return x : int the size of the skip list
 	 */
    @Override
    public int size() {
    	return this.size;
    }
}
