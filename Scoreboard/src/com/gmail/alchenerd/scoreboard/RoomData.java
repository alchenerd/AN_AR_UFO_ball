package com.gmail.alchenerd.scoreboard;

public class RoomData {
	private int player1ID;
	private int player2ID;
	private int player1Score;
	private int player2Score;
	private int port;
	public RoomData(int a, int b, int c, int d, int e) {
		// private member init
		if(a>b){
			int t = a;a = b; b = t;
			t = c; c = d; d = t;
		}
		player1ID=a;
		player2ID=b;
		player1Score=c;
		player2Score=d;
		port = e;
	}
	public void setData(String s, int input) {
		// sets private member value
		if(s == "Player1ID"){
			player1ID = input;
		}
		else if(s == "Player2ID"){
			player2ID = input;
		}
		else if(s == "Player1Score"){
			player1Score = input;
		}
		else if(s == "Player2Score"){
			player2Score = input;
		}
		else if(s == "port"){
			port = input;
		}
	}
	public String getData(String s) {
		// returns requested private member value
		// return "" if not hit
		if(s.equals("player1ID")){
			return String.valueOf(player1ID);
		}
		else if(s.equals("player2ID")){
			return String.valueOf(player2ID);
		}
		else if(s.equals("player1Score")){
			return String.valueOf(player1Score);
		}
		else if(s.equals("player2Score")){
			return String.valueOf(player2Score);
		}
		else if(s.equals("port")){
			return String.valueOf(port);
		}
		else return "";
	}

}
