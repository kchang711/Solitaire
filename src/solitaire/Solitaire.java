package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	
	void jokerA() {
		if(deckRear == null)
			return;
		CardNode ptr = deckRear;
		CardNode ptr2 = deckRear.next;
		do{
			if (ptr2.cardValue == 27){
				ptr.next = ptr2.next;
				ptr2.next = ptr2.next.next;
				ptr.next.next = ptr2;
				break;
			}
			ptr = ptr2;
			ptr2 = ptr2.next;
		} while (ptr != deckRear);
	}

	void jokerB() {
		if(deckRear == null)
			return;
	    CardNode ptr = deckRear;
	    CardNode ptr2 = deckRear.next;
	    do{
	    	if (ptr2.cardValue == 28){
	    		ptr.next = ptr2.next;
	    		ptr2.next = ptr2.next.next.next;
	    		ptr.next.next.next = ptr2;
	    		break;
	    	}
	    	ptr = ptr2;
	    	ptr2 = ptr2.next;
	    } while (ptr != deckRear);
	}
	
	
	void tripleCut() {
		if(deckRear == null)
			return;
		CardNode ptr = deckRear.next;
		CardNode ptr2 = deckRear.next.next;
		CardNode ptr3 = deckRear;
		int jOne, jTwo;
		if ((ptr3.cardValue == 27 && ptr.cardValue == 28) || (ptr3.cardValue == 28 && ptr.cardValue == 27))
			return;
		else if (ptr.cardValue == 27 || ptr.cardValue == 28){
			jOne = ptr.cardValue;
			if (jOne == 27)
				jTwo = 28;
			else
				jTwo = 27;
			do{
				ptr = ptr.next;
			} while (ptr.cardValue != jTwo);
			deckRear = ptr;
			return;
		}
		else if (ptr3.cardValue == 27 || ptr3.cardValue == 28){
			jOne = ptr3.cardValue;
			if (jOne == 27)
				jTwo = 28;
			else
				jTwo = 27;
			do{
				ptr3 = ptr3.next;
			} while (ptr3.next.cardValue != jTwo);
			deckRear = ptr3;
			return;
		}
		
		do{
			if (ptr2.cardValue == 27){
				ptr3 = ptr3.next;
				if (ptr3.cardValue == 28){
					ptr.next = ptr3.next;
					ptr3.next = deckRear.next;
					deckRear.next = ptr2;
					deckRear = ptr;
					break;
				}
			}
			else if (ptr2.cardValue == 28){
				ptr3 = ptr3.next;
				if (ptr3.cardValue == 27){
					ptr.next = ptr3.next;
					ptr3.next = deckRear.next;
					deckRear.next = ptr2;
					deckRear = ptr;
					break;
				}
			}		
			else{
				ptr = ptr2;
				ptr2 = ptr2.next;
				ptr3 = ptr;
			}
		} while (ptr3 != deckRear);
	}
	
	void countCut() {	
		if(deckRear == null)
			return;
		CardNode ptr = deckRear.next;
		CardNode ptr2 = deckRear.next;
		CardNode ptr3 = deckRear.next.next;
		int lastCardValue = deckRear.cardValue;
		int cnt = 1;
		
		while (ptr2.next != deckRear){
			if (cnt == lastCardValue)
				ptr2 = ptr2.next;
			else{
				ptr2 = ptr2.next;
				ptr = ptr.next;
				ptr3 = ptr3.next;
				cnt++;
			}
			
		}
		ptr2.next = deckRear.next;
		deckRear.next = ptr.next;
		ptr.next = deckRear;
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		int cnt = 1;
		CardNode target = deckRear.next;
		while (true){
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			if (deckRear.next.cardValue == 28)
				cnt = 27;
			else
				cnt = deckRear.next.cardValue;
			for (int cnt2 = 1; cnt2 < cnt; cnt2++){
				target = target.next;
			}
			if (target.next.cardValue != 27 && target.next.cardValue != 28)
				return target.next.cardValue;
		}
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		if (message == null) {
			return "";
		}
		String encrypted = "";
		String msg = "";
		for (int cnt = 0; cnt < message.length(); cnt++){
			if (Character.isLetter(message.charAt(cnt)))
				msg = msg + Character.toUpperCase(message.charAt(cnt));
		}
		for (int cnt = 0; cnt < msg.length(); cnt++){
			/*printList(deckRear);
			jokerA();
			printList(deckRear);
			jokerB();
			printList(deckRear);
			tripleCut();
			printList(deckRear);
			countCut();
			printList(deckRear);
			*/
			
			char ch = msg.charAt(cnt);
			int c = ch - 'A' + 1;
			c = c + getKey();
			if (c > 26)
				c = c - 26;
			encrypted = encrypted + (char)(c-1 + 'A');
		
		}
		
	    return encrypted;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		if (message == null) {
			return "";
		}
		String decrypted = "";
		for (int cnt = 0; cnt < message.length(); cnt++){
			char ch = message.charAt(cnt);
			int c = ch - 'A' + 1;
			c = c - getKey();
			if (c < 0)
				c = c + 26;
			decrypted = decrypted + (char)(c-1 + 'A');
		}
		
	    return decrypted;
	}
}
