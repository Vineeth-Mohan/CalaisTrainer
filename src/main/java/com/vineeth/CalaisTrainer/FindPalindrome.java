package com.vineeth.CalaisTrainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class FindPalindrome {
	
	/*
	 *  Map palindromes will track all the palindromes detected so far.
	 *  Its key value is the starting position of the palindrom and value is the length of palindrome
	 */
	public Map<Integer,Integer> palindromes = null;
	String checkString;

	public List<String> getPalindromes(String checkString){
		this.checkString = checkString;
		this.palindromes = new HashMap<Integer,Integer>();
	    char[] c = checkString.toCharArray();
	    traverse(c,0,c.length-1);
        Set<Integer> keys = new TreeSet<Integer>(palindromes.keySet());
        System.out.println("Plaindromes are " + palindromes);
        // Removing smaller palindromes
        for(Integer start : keys){
        	if(palindromes.get(start) == null){
        		continue;
        	}
        	for(int i = start +1 ; i < (start + palindromes.get(start)) ; i++){
        		palindromes.remove(new Integer(i));
        	}
        }
        System.out.println("Plaindromes are " + palindromes);
        List<String> partitions = new ArrayList<String>();
        
        //Sorting the keys so we get a ordered output
        for(Integer start : new TreeSet<Integer>(palindromes.keySet())){
        	partitions.add(checkString.substring(start, start + palindromes.get(start)));
        }
	    return partitions;
		
	}
	
    void traverse(char[] a ,int i , int j)
    {
        if(i==j) //If only 1 letter
        {
        	Integer size = palindromes.get(i);
        	if(size == null || size < 1){
            	palindromes.put(i, 1);
        	}
            return;
        }
        if(a[i] == a[j] && (i+1) == j) // if there are 2 character and both are equal
        {
        	Integer size = palindromes.get(i);
        	if(size == null || size < 2){
        		palindromes.remove(i+1);
            	palindromes.put(i, 2);
        	}
            return;   
        }
        if(a[i] == a[j]){ // If first and last char are equal
        	traverse(a , i+1 , j-1);
        	Integer size = palindromes.get(i+1);
        	if(size != null && size == (j-i-1)){
        		palindromes.remove(i+1); // remove any previous entry of smaller palindrome
        		palindromes.remove(j-1);
        		palindromes.put(i, size + 2);
                return;
        	}  
        }
        traverse(a,i+1 ,j); // Check for the rest of 2 combinations
        traverse(a,i,j-1); 
        return;
    }
	
	
	public static void main(String args[]){
        String s = "malayalam is my language";
        FindPalindrome p = new FindPalindrome();
        System.out.print("Strings after parition are " + p.getPalindromes(s));           
	}

}
