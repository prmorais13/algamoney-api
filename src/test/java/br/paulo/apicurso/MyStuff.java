package br.paulo.apicurso;

public class MyStuff {

	public static void main(String[] args) {
		String[] sa = {"tom", "jerry"};
		for (int x = 0; x < 3; x++) {
			for (String s : sa) {
//			for (int a = 0; a <= sa.length; a++) {
//				System.out.println(x + " " + sa[a]);
				System.out.println(x + " " + s);
				if (x == 1) {
					break;
				}
			}
		}
	}
	
	

}
