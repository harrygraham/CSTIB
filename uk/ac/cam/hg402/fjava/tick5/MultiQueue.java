package uk.ac.cam.hg402.fjava.tick5;

import java.util.HashSet;
import java.util.Set;

public class MultiQueue<Message> {
 private Set<MessageQueue<Message>> outputs = new HashSet<MessageQueue<Message>>();
 public synchronized void register(MessageQueue<Message> q) { 
  outputs.add(q);
 }
 public synchronized void deregister(MessageQueue<Message> q) {
  outputs.remove(q);
 }
 public synchronized void put(Message message) {
  //TODO: copy "message" to all elements in "outputs"
	 for (MessageQueue<Message> messageQueue : outputs) {
		messageQueue.put(message);
	}
 }
}