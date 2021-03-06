package skaipeuh;

import java.net.*;

public class Skaipeuh {

	private static final String IPgroup = "224.5.6.7";
	private static final int PORTgroup = 9876;

	private Recepteur recepteur;
	private Emetteur emetteur;
	private String IAM, HLO, BYE, BAN;

	public Skaipeuh(String user, String port) {
		try {
			String machine;
			InetAddress localeAdresse;

			ServeurTCP serveur;
			Thread t1, t2;

			localeAdresse = InetAddress.getLocalHost();
			machine = localeAdresse.getHostAddress();
			user = Bourrage.bourrageUser(user);
			machine = Bourrage.bourrageMachine(machine);
			port = Bourrage.leftBourrage(port, 5, "0");

			IAM = "IAM " + user + " " + machine + " " + port;
			HLO = "HLO " + user + " " + machine + " " + port;
			BYE = "BYE " + user;
			BAN = "BAN " + user;

			recepteur = new Recepteur(IAM, BYE, BAN, IPgroup, PORTgroup);
			t1 = new Thread(recepteur);
			t1.start();

			emetteur = new Emetteur(IPgroup, PORTgroup);
			emetteur.lance(HLO);

			serveur = new ServeurTCP(Integer.parseInt(port));
			t2 = new Thread(serveur);
			t2.start();

			new MyScanner(this);
		
		} catch (UnknownHostException e) {
			System.out.println("Skaipeuh getLocalHost - ");
			e.printStackTrace();
		}
	}

	static void lanceClient(String clientAdresse, String personAdresse, int port) {
		try {
			ServeurTCP.running = true;
			Socket s = new Socket(clientAdresse, port);

			ThreadWRI wri = new ThreadWRI(s, personAdresse);
			MyScanner.setClient(wri);
			ThreadREAD read = new ThreadREAD(s, personAdresse, wri);
			MyScanner.setFileAccepter(read);
			Thread t = new Thread(read);
			t.start();
		} catch (Exception e) {
			System.out.println("Erreur ClientTCP\n" + e);
		}
	}

	public void lance(String s) {
		if (s.equals("BYE")) {
			emetteur.lance(BYE);
			System.exit(0);
		} 
		else if (s.equals("RFH")) {
			emetteur.lance("RFH");
		}
		else if (s.startsWith("BAN")) {
			BAN = s.substring(4);
			BAN = "BAN " + Bourrage.bourrageUser(BAN);
			emetteur.lance(BAN);
		}
		else if (s.equals("AFF")) {
			recepteur.tab.afficheUserConnect(true);
		}
		else if (s.startsWith("TCP")) {
			int id = Integer.parseInt(s.substring(4));
			String portUser = (recepteur.tab.returnInfoUser(id)).substring(29);
			String adresse = (recepteur.tab.returnInfoUser(id)).substring(13,
					28);
			lanceClient(adresse, adresse, Integer.parseInt(portUser));
		}
	}

	public void refresh() {
		emetteur.lance("RFH");
	}
}
