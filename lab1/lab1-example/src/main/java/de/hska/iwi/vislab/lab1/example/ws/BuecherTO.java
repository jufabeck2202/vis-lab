package de.hska.iwi.vislab.lab1.example.ws;

import java.util.*;
import javax.xml.bind.annotation.*;

/** Rueckgabe-Transferobjekt */
@XmlRootElement
public class BuecherTO {
	@XmlElement(nillable = true)
	private List<BuchDO> results = new ArrayList<BuchDO>();
	private String message;
	private Integer returncode;

	public List<BuchDO> getResults() {
		return results;
	}

	public String getMessage() {
		return message;
	}

	public Integer getReturncode() {
		return returncode;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setReturncode(Integer returncode) {
		this.returncode = returncode;
	}
}