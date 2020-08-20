package de.hska.iwi.vislab.lab1.example.ws;

import java.util.List;

import javax.jws.WebService;

import de.hska.iwi.vislab.lab1.example.dao.BuecherCrudDAO;


/** Dienstimplementierung */
@WebService(endpointInterface = "de.hska.iwi.vislab.lab1.example.ws.BuecherServiceIntf")
public class BuecherServiceImpl implements BuecherServiceIntf {
	public static final Integer RET_CODE_OK = new Integer(0);
	public static final Integer RET_CODE_ERROR = new Integer(1);
	private BuecherCrudDAO dao = BuecherCrudDAO.getInstance();

	@Override
	public BuecherTO createBuch(BuchDO bu) throws DuplicateCreateException {
		bu = dao.createBuch(bu);
		return erzeugeBuecherTO("Buch hinzugefuegt (mit ISBN " + bu.getIsbn()
				+ ")", bu);
	}

	@Override
	public BuecherTO getBuchByIsbn(Long isbn) {
		return erzeugeBuecherTO("Buch mit ISBN " + isbn, dao
				.getBuchByIsbn(isbn));
	}

	@Override
	public BuecherTO findeBuecher(BuchDO bu) {
		BuecherTO buecherTO = new BuecherTO();
		List<BuchDO> buecherListe = dao.findeBuecher(bu.getIsbn(), bu
				.getTitel());
		if (buecherListe == null || buecherListe.size() == 0)
			return fehlerBuecherTO("Keine passenden Buecher gefunden.");
		if (bu.getIsbn() == null && isEmpty(bu.getTitel())) {
			buecherTO.setMessage(buecherListe.size() + " Buecher");
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(buecherListe.size() + " Ergebnis(se) fuer");
			if (bu.getIsbn() != null)
				sb.append(" ISBN = " + bu.getIsbn());
			if (!isEmpty(bu.getTitel()))
				sb.append(" Titel = " + bu.getTitel());
			buecherTO.setMessage(sb.toString());
		}
		buecherTO.getResults().addAll(buecherListe);
		buecherTO.setReturncode(RET_CODE_OK);
		return buecherTO;
	}

	private static BuecherTO erzeugeBuecherTO(String msg, BuchDO buchDO) {
		if (buchDO == null)
			return fehlerBuecherTO("Fehler: Buchobjekt fehlt (" + msg + ").");
		BuecherTO buecherTO = new BuecherTO();
		buecherTO.getResults().add(buchDO);
		buecherTO.setMessage(msg);
		buecherTO.setReturncode(RET_CODE_OK);
		return buecherTO;
	}

	private static BuecherTO fehlerBuecherTO(String msg) {
		BuecherTO buecherTO = new BuecherTO();
		buecherTO.setMessage(msg);
		buecherTO.setReturncode(RET_CODE_ERROR);
		return buecherTO;
	}

	private static boolean isEmpty(String s) {
		return s == null || s.trim().length() <= 0;
	}
}