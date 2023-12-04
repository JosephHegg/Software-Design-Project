package Presentation;

import java.util.Scanner;

public class Console implements FrontEnd {

	private Scanner scanner = new Scanner(System.in);
	@Override
	public String getInput() {
		return scanner.nextLine();
	}

	@Override
	public void showOutput(String msg) {
		System.out.println(msg);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.scanner.close();
	}

}
