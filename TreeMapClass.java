//===========================================================================================================================
//	Program : TreeMap class
//===========================================================================================================================
//	@author: Karthika Karunakaran, driver program fiven by Professor Dr.Balaji
// 	Date created: 2016/10/23
//===========================================================================================================================
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class TreeMapClass<T extends Comparable<? super T>> {

	private TreeMap<T, T> treeMap = new TreeMap<>();
	
	/** Procedures to perform contains
	 * Runs in time O(logn) 
	 * @param x : T to check contains
	 * @return true/false : boolean contains or not
	 */
	boolean contains(T x) {
		return treeMap.containsKey(x);
	}
	 
	/** Procedures to perform add
	 * Runs in time O(logn) 
	 * @param x : T to add
	 * @return true/false : boolean added or replaced
	 */
	boolean add(T x) {
		return treeMap.put(x, x) == null;
	}
	
	/** Procedures to perform remove
	 * Runs in time O(logn) 
	 * @param x : T to remove
	 * @return x : T this is removed
	 */
	T remove(T x) {
		return treeMap.remove(x);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc;
		if (args.length > 0) {
			File file = new File(args[0]);
			sc = new Scanner(file);
		} else {
			sc = new Scanner(System.in);
		}
		String operation = "";
		long operand = 0;
		int modValue = 999983;
		long result = 0;
		TreeMapClass<Long> treeMapObj = new TreeMapClass<>();
		// Initialize the timer
		Timer timer = new Timer();

		while (!((operation = sc.next()).equals("End"))) {
			switch (operation) {
				case "Add": {
					operand = sc.nextLong();
					if (treeMapObj.add(operand)) {
						result = (result + 1) % modValue;
					}
					break;
				}
				case "Remove": {
					operand = sc.nextLong();
					if (treeMapObj.remove(operand) != null) {
						result = (result + 1) % modValue;
					}
					break;
				}
				case "Contains": {
					operand = sc.nextLong();
					if (treeMapObj.contains(operand)) {
						result = (result + 1) % modValue;
					}
					break;
				}
			}
		}
		// End Time
		timer.end();

		System.out.println(result);
		System.out.println(timer);
		sc.close();
	}
}
