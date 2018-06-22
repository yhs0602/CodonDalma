package com.kyunggi.proteindalma;

public class Dalma
{
	boolean alive;
	long spawntime;
	Codon.Protein protein;

	public Dalma(long spawntime, Codon.Protein protein)
	{
		this.spawntime = spawntime;
		this.protein = protein;
		alive=false;
	}
}
