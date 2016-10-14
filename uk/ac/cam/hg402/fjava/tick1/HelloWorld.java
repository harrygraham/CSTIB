package uk.ac.cam.hg402.fjava.tick1;

public class HelloWorld {

	public static void main(String[] args) {
		if(args.length ==1){
			System.out.println("Hello, " + args[0].toString());
		}else{
			System.out.println("Hello, world");
		}
	}

}
