package de.hska.iwi.vislab.lab1.example.ws;

/**
 * Buch-Domain-Objekt
 */
public class BuchDO {
	private Long isbn;
	private String titel;
	private Double preis;

	public Long getIsbn() {
		return isbn;
	}

	public String getTitel() {
		return titel;
	}

	public Double getPreis() {
		return preis;
	}

	public void setIsbn(Long isbn) {
		this.isbn = isbn;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public void setPreis(Double preis) {
		this.preis = preis;
	}
}