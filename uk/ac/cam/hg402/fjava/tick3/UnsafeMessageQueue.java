package uk.ac.cam.hg402.fjava.tick3;

public class UnsafeMessageQueue<T> implements MessageQueue<T> {
	 private static class Link<L> {
	  L val;
	  Link<L> next;
	  Link(L val) { this.val = val; this.next = null; }
	 }
	 private Link<T> first = null;
	 private Link<T> last = null;

	 public void put(T val) {
	  //TODO: given a new "val", create a new Link<T>
	  //      element to contain it and update "first" and
	  //      "last" as appropriate
		 
		 Link<T> newVal = new Link<T>(val);
		 if(last != null){ last.next = newVal; last = newVal; }
		 else{last = newVal;}
		 
		 if(first == null){ first = newVal;}
		 
	 }

	 public T take() {
	  while(first == null) //use a loop to block thread until data is available
	   try {Thread.sleep(100);} catch(InterruptedException ie) {}
	  //TODO: retrieve "val" from "first", update "first" to refer
	  //      to next element in list (if any). Return "val"
	  
	  Link<T> returnLink = first; 
	  first = first.next;
	  return returnLink.val;
	 }
	}