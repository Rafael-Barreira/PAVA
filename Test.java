import java.io.UnsupportedEncodingException;
public class Test {
	/*public static void main (String[] args)throws UnsupportedEncodingException {
		Integer x = 1;
		x.intValue();
		System.out.println("POOOOOOOOOOP");
		lel();
	}*/
	
	public static String lel()throws UnsupportedEncodingException{
		String a = "2";
	    a.getBytes();
	    a.getBytes("we");
	    System.out.println(a);
	    int t = Integer.parseInt(a);
	    return a;
	}
}