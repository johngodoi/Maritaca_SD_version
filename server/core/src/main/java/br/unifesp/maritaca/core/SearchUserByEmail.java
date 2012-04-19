package br.unifesp.maritaca.core;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class SearchUserByEmail {

	@Id
	private UUID key;
	
}