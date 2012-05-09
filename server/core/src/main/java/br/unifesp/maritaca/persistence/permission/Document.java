package br.unifesp.maritaca.persistence.permission;

public enum Document {
	FORM(0),
	ANSWER(1);
	
	private Integer idDocument;
	
	private Document(Integer idDocument) {
		this.idDocument = idDocument;
	}

	public Integer getId() {
		return idDocument;
	}

	public void setIdDocument(Integer idDocument) {
		this.idDocument = idDocument;
	}
}