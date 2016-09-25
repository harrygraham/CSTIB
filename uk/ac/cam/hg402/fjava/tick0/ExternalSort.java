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



public class ExternalSort {
	private static long LENGTH = 0L;
	
	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
		RandomAccessFile a1 = new RandomAccessFile(f1, "rw");
		RandomAccessFile b1 = new RandomAccessFile(f2, "rw");
		
		long blockSize = 4L; //block size starts as 4 bytes (1 Int)
		 LENGTH = a1.length(); //length of file in bytes
		
		long count = 1; // count to determine if file b -> file a copy is needed
		
		//while blocksize is less than length of file, repeatedly merge, alternating input and out files.
		while(blockSize < LENGTH){
			merge(f1, f2, blockSize);
			blockSize *=2;
			String temp = f1;
			f1 = f2;
			f2 = temp;
			count++;
		}
		
		//Check to see if a file b -> file a copy is needed and then iterates through file.
		if(count%2 ==0){
			DataInputStream file1 = new DataInputStream(new BufferedInputStream(new FileInputStream(f1)));
		    DataOutputStream file2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f2)));
			long currentBytes1 = 0; // current amount of bytes read

			
		    while(currentBytes1 < LENGTH){
		    	int num = file1.readInt();
		    	file2.writeInt(num);
		    	currentBytes1+=4;
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
	     	
		long currentBytes1 = 0; // current amount of bytes read for input 1
		long currentBytes2 = 0; // current amount of bytes read for input 2
		
		//setup block 2 to one blocksize ahead and increment the second bytes counter by blocksize amount
		 a2.skipBytes((int) blockSize); currentBytes2 += blockSize;

		 // System.out.println(blockSize);
		  
		  while (currentBytes1 < LENGTH)
		    {
		      // This deals with case where there are odd number of blocks, last block copied straight over without merge
		      if (currentBytes2 >=LENGTH)
		        {
		          while (currentBytes1 < LENGTH){
		            b1.writeInt(a1.readInt());
		            currentBytes1 +=4;
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
		          currentBytes1 += 4;
		          int num2 = a2.readInt();
		          currentBytes2 +=4;
		          
		          while (!block1End | !block2End)
		            {
		             //while block1 is not finished and either the number from block1 is less than num2 or block2 has ended
		              while ( !block1End & ( (num1 <= num2) | block2End) )
		                {
		                  b1.writeInt(num1); 
		                  count1 += 4;
		                  if (count1 < blockSize & currentBytes1 < LENGTH){
		                    num1 = a1.readInt();
		                    currentBytes1 +=4;
		                  }
		                  else{
		                    block1End = true;
		                  }
		                  
		                }
		              //similar with block2
		              while ( !block2End & ( (num2 < num1) | block1End) )
		                {
		                  b1.writeInt(num2); 
		                  count2 += 4;
		                  if (count2 < blockSize & currentBytes2 < LENGTH){
		                    num2 = a2.readInt();
		                    currentBytes2 +=4;
		                  }
		                  else{
		                    block2End = true;
		                  }
		                  
		                }
		            }
		        }
		      
		      //go to next two blocks
		      a1.skipBytes((int) blockSize);
		      currentBytes1 += blockSize;
		      a2.skipBytes((int) blockSize);
		      currentBytes2 += blockSize;
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