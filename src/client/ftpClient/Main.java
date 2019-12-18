package client.ftpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("\nEnter Host :");
		String h = br.readLine();
		System.out.print("\nEnter port :");
		String p = br.readLine();
		Client ftp = new Client(h,Integer.parseInt(p));
		ftp.Menu("1","1");
	}
}
