package de.hska.iwi.vislab.lab1.example;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Random;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import de.hska.iwi.vislab.lab1.example.ws.BuchDO;
import de.hska.iwi.vislab.lab1.example.ws.BuecherServiceImpl;
import de.hska.iwi.vislab.lab1.example.ws.BuecherServiceIntf;
import de.hska.iwi.vislab.lab1.example.ws.BuecherTO;


/** Testclient fuer den Webservice */
public class TestWsClient {
	
	static {
		// dump http on client
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		// dump http on server		
		//System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
	}
	
	public static void main(final String[] args) throws Exception {
		String url = (args.length > 0) ? args[0]
				: "http://localhost:4434/buecherservice";
		int anzahlBuecher = (args.length > 1) ? Integer.parseInt(args[1]) : 10;
		test("TestWsClient", url, 1000000000L, anzahlBuecher, false);
	}

	public static int test(String testName, String url, long startIsbn,
			int anzahlBuecher, boolean trace) throws Exception {
		// Zugriff auf den Webservice vorbereiten:
		BuecherServiceIntf buecherService;
		if (url.equalsIgnoreCase("direkt")) {
			buecherService = new BuecherServiceImpl();
		} else {
			System.out.println(testName + ": " + url);
			Service service = null;
			int timeoutSekunden = 20;
			while (service == null) {
				try {
					service = Service.create(new URL(url + "?wsdl"), new QName(
							"http://ws.example.lab1.vislab.iwi.hska.de/",
							"BuecherServiceImplService"));
				} catch (WebServiceException ex) {
					if (timeoutSekunden-- <= 0)
						throw ex;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
			buecherService = service.getPort(BuecherServiceIntf.class);
		}

		// Anlage von Buechern:
		if (trace)
			System.out.println(testName + ": Starte Anlage von "
					+ anzahlBuecher + " Buechern");
		long startZeit = System.nanoTime();
		for (int i = 0; i < anzahlBuecher; i++) {
			BuchDO bu = new BuchDO();
			bu.setIsbn(new Long(startIsbn + i));
			bu.setTitel("Buch " + i);
			bu.setPreis(new Double(i));
			buecherService.createBuch(bu);
		}
		String s1 = "\nAnlage von " + anzahlBuecher + " Buechern dauert: "
				+ ermittleDauer(startZeit);

		// Auslesen von Buechern in einzelnen Lesevorgaengen:
		if (trace)
			System.out.println(testName + ": Starte einzelnes Auslesen");
		startZeit = System.nanoTime();
		for (int i = 0; i < anzahlBuecher; i++) {
			Long isbn = new Long(startIsbn
					+ (new Random()).nextInt(anzahlBuecher));
			BuecherTO buTO = buecherService.getBuchByIsbn(isbn);
			if (buTO == null || buTO.getResults() == null
					|| buTO.getResults().size() != 1) {
				throw new RuntimeException(
						"Fehler beim Auslesen des Buches mit der ISBN " + isbn);
			}
		}
		String s2 = "\nEinzelnes Auslesen von " + anzahlBuecher
				+ " Buechern dauert: " + ermittleDauer(startZeit);

		// Auslesen aller Buecher in einem Lesevorgang:
		if (trace)
			System.out.println(testName + ": Starte gemeinsames Auslesen");
		startZeit = System.nanoTime();
		BuecherTO buTO = buecherService.findeBuecher(new BuchDO());
		String s3 = "\nGemeinsames Auslesen von " + buTO.getResults().size()
				+ " Buechern dauert: " + ermittleDauer(startZeit);
		String s0 = "";
		if (buTO.getResults().size() < 30)
			s0 = zeigeErgebnis(buTO);

		System.out.println(testName + ": " + s0 + s1 + s2 + s3 + "\n");
		return buTO.getResults().size();
	}

	static String zeigeErgebnis(BuecherTO buTO) {
		StringBuffer sb = new StringBuffer();
		sb.append("\n" + buTO.getMessage() + "\n");
		for (BuchDO bu : buTO.getResults())
			sb.append("    Buch (ISBN=" + bu.getIsbn() + ", Titel="
					+ bu.getTitel() + ", Preis=" + bu.getPreis() + ")\n");
		sb.append("    Returncode " + buTO.getReturncode() + "\n");
		return sb.toString();
	}

	static String ermittleDauer(long startZeitNanoSek) {
		long dauerMs = (System.nanoTime() - startZeitNanoSek) / 1000 / 1000;
		if (dauerMs < 1000)
			return "" + dauerMs + " ms";
		return (new DecimalFormat("#,##0.00")).format(dauerMs / 1000.) + " s";
	}
}