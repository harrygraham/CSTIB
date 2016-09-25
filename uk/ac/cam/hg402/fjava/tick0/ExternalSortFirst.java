package uk.ac.cam.hg402.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;




public class ExternalSortFirst {

	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
		RandomAccessFile a1 = new RandomAccessFile(f1, "rw");
		RandomAccessFile b1 = new RandomAccessFile(f2, "rw");
		
		long blockSize = 4L; //block size starts as 4 bytes (1 Int)
		long numOfInts = a1.length();
		long count = 1; // count to determine if file b -> file a copy is needed
		
		while(blockSize < numOfInts){
			merge(f1, f2, blockSize);
			blockSize *=2;
			String temp = f1;
			f1 = f2;
			f2 = temp;
			count++;
		}
		if(count%2 ==0){
			DataInputStream file1 = new DataInputStream(new BufferedInputStream(new FileInputStream(f1)));
		    DataOutputStream file2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f2)));
		    
		    while(file1.available() >0){
		    	int num = file1.readInt();
		    	System.out.println(num);
		    	file2.writeInt(num);
		    }
		    
		    file2.flush();
		    file2.close();
		    file1.close();
		}
		
		
	}
	
	public static void merge(String inputFile, String outputFile, long blockSize)
		    throws IOException{
		
		 DataInputStream a1 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
	     DataInputStream a2 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
	     DataOutputStream b1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));

		 a2.skipBytes((int) blockSize);

		  System.out.println(blockSize);
		  
		  while (a1.available() > 0)
		    {
		      // This deals with case where there are odd number of blocks, last block copied straight over without merge
		      if (a2.available() <= 0)
		        {
		          while (a1.available() > 0){
		            b1.writeInt(a1.readInt());
		          } break;
		          
		        }else{
		        
		          //counts for the number of ints read from each block
		          long count1 = 0;
		          long count2 = 0;
		          
		          //booleans to make logic easier
		          boolean block1End = false;
		          boolean block2End = false;

		          //current ints available from block 1 and block 2
		          int num1 = a1.readInt();
		          int num2 = a2.readInt();

		          
		          while (!block1End | !block2End)
		            {
		             
		              while ( !block1End & ( (num1 <= num2) | block2End) )
		                {
		                  b1.writeInt(num1); 
		                  count1 += 4;
		                  if (count1 < blockSize & a1.available() > 0){
		                    num1 = a1.readInt();
		                  }
		                  else{
		                    block1End = true;
		                  }
		                  
		                }
		              
		              while ( !block2End & ( (num2 < num1) | block1End) )
		                {
		                  b1.writeInt(num2); 
		                  count2 += 4;
		                  if (count2 < blockSize & a2.available() > 0){
		                    num2 = a2.readInt();
		                  }
		                  else{
		                    block2End = true;
		                  }
		                  
		                }
		            }
		        }
		      
		      //go to next two blocks
		      a1.skipBytes((int) blockSize);
		      a2.skipBytes((int) blockSize);
		    }
		  
		  a1.close();
		  a2.close();
		  b1.flush(); // flush data
		  b1.close();
		  
		}
	

	private static String byteToHex(byte b) {
		String r = Integer.toHexString(b);
		if (r.length() == 8) {
			return r.substring(6);
		}
		return r;
	}

	public static String checkSum(String f) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			DigestInputStream ds = new DigestInputStream(
					new FileInputStream(f), md);
			byte[] b = new byte[512];
			while (ds.read(b) != -1)
				;

			String computed = "";
			for(byte v : md.digest()) 
				computed += byteToHex(v);

			return computed;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "<error computing checksum>";
	}
	
	

	public static void main(String[] args) throws Exception {
		String f1 = args[0];
		String f2 = args[1];
		sort(f1, f2);
		System.out.println("The checksum is: "+checkSum(f1));
	}
}