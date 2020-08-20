package de.hska.iwi.vislab.lab1.example.ws;

import javax.jws.*;

/** Dienst-Interface */
@WebService
public interface BuecherServiceIntf {
	BuecherTO createBuch(@WebParam(name = "buch") BuchDO buch) throws Exception;

	BuecherTO getBuchByIsbn(@WebParam(name = "isbn") Long isbn);

	BuecherTO findeBuecher(@WebParam(name = "buch") BuchDO buch);
}