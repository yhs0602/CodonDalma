package com.kyunggi.proteindalma;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Codon
{
	int index;
	char [] bases=new char[3];
	public Codon()
	{
		reset();
	}

	public synchronized void reset()
	{
		index = 0;
	}
	public synchronized boolean isDone()
	{
		return index==3;
	}
	public synchronized void AddBase(char base)
	{
		if(index==0)
		{
			bases[0]=base;
			index++;
		}else if(index==1)
		{
			bases[1]=base;
			index++;
		}else if(index==2)
		{
			bases[2]=base;
			index++;
		}
	}
	
	public synchronized Protein getProtein()
	{
		if(index==3)
		{
			index=0;
			switch(bases[0])
			{
				case 'U':
				{
					switch(bases[1])
					{
						case 'U':
							switch(bases[2])
							{
								case 'U':
								case 'C':
									return Protein.Phe;
								default:
									return Protein.Leu;
							}
						case 'C':
							return Protein.Ser;
						case 'A':
							switch(bases[2])
							{
								case 'U':
								case 'C':
									return Protein.Tyr;
								default:
								return Protein.End;
							}
						case 'G':
							switch(bases[2])
							{
								case 'U':
								case 'C':
									return Protein.Cys;
								case 'A':
									return Protein.End;
								case 'G':
									return Protein.Trp;
							}
							
					}
				}
				break;
				case 'C':
					{
						switch(bases[1])
						{
							case 'U':
								return Protein.Leu;
							case 'C':
								return Protein.Pro;
							case 'A':
								switch(bases[2])
								{
									case 'U':
									case 'C':
										return Protein.His;
									default:
										return Protein.Gln;
								}
							case 'G':
								return Protein.Arg;

						}
					}
					break;
				case 'A':
					{
						switch(bases[1])
						{
							case 'U':
								switch(bases[2])
								{
									case 'U':
									case 'C':
									case 'A':
										return Protein.Ile;
									default:
										return Protein.Met;
								}
							case 'C':
								return Protein.Thr;
							case 'A':
								switch(bases[2])
								{
									case 'U':
									case 'C':
										return Protein.Asn;
									default:
										return Protein.Lys;
								}
							case 'G':
								switch(bases[2])
								{
									case 'U':
									case 'C':
										return Protein.Ser;
									case 'A':
									case 'G':
										return Protein.Arg;
								}

						}
					}
					break;
				case 'G':
					{
						switch(bases[1])
						{
							case 'U':
								return Protein.Val;
							case 'C':
								return Protein.Ala;
							case 'A':
								switch(bases[2])
								{
									case 'U':
									case 'C':
										return Protein.Asp;
									default:
										return Protein.Glu;
								}
							case 'G':
								return Protein.Gly;

						}
					}
			}
		}
		throw new RuntimeException();
	}
	public enum Protein{
		Phe,
		Leu,
		Ile,
		Met,
		Val,
		Ser,
		Pro,
		Thr,
		Ala,
		Tyr,
		End,
		His,
		Gln,
		Asn,
		Lys,
		Asp,
		Glu,
		Cys,
		Trp,
		Arg,
		Gly;

		public String getFull()
		{	
			switch(this)
			{
				case Phe: return "Phenylalanine";
				case Leu: return "Leucine";
				case Ile: return "Isoeucine";
				case Met: return "Methionine";
				case Val: return "Valine";
				case Ser: return "Serine";
				case Pro: return "Proline";
				case Thr: return "Threonine";
				case Ala: return "Alanin";
				case Tyr: return "Thyrosin";
				case End: return "Stop";
				case His: return "Histidine";
				case Gln: return "Glutamin";
				case Asn: return "Asparagine";
				case Lys: return "Lysine";
				case Asp: return "Aspartic acid";
				case Glu: return "Glutamic acid";
				case Cys: return "Cysteine";
				case Trp: return "Tryptophan";
				case Arg: return "Arginine";
				case Gly: return "Glycine";
			}
			return name();
		}
		public String getKor()
		{
			// TODO: Implement this method
			switch(this)
			{
				case Phe: return "페닐알라닌";
				case Leu: return "류신";
				case Ile: return "아이소류신";
				case Met: return "메싸이오닌";
				case Val: return "발린";
				case Ser: return "세린";
				case Pro: return "프롤린";
				case Thr: return "트레오닌";
				case Ala: return "알라닌";
				case Tyr: return "타이로신";
				case End: return "종결";
				case His: return "히스티딘";
				case Gln: return "글루타민";
				case Asn: return "아스파라진";
				case Lys: return "라이신";
				case Asp: return "아스파트산";
				case Glu: return "글루탐산";
				case Cys: return "시스테인";
				case Trp: return "트립토판";
				case Arg: return "아르지닌";
				case Gly: return "글라이신";
			}
			return name();
		};
		
		public String getCodon()
		{
			switch(this)
			{
				case Phe: return "UUU, UUC";
				case Leu: return "UUA, UUG, CUU, CUC, CUA, CUG";
				case Ile: return "AUU, AUC AUA";
				case Met: return "AUG";
				case Val: return "GUU, GUC, GUA, GUG";
				case Ser: return "UCU, UCC, UCA, UCG, AGU, AGC";
				case Pro: return "CCU, CCC, CCA, CCG";
				case Thr: return "ACU, ACC, ACA, ACG";
				case Ala: return "GCU, GCC, GCA, GCG";
				case Tyr: return "UAU, UAC";
				case End: return "UAA, UGA, UAG";
				case His: return "CAU, CAC";
				case Gln: return "CAA, CAG";
				case Asn: return "AAU, AAC";
				case Lys: return "AAA, AAG";
				case Asp: return "GAU, GAC";
				case Glu: return "GAA, GAG";
				case Cys: return "UGU, UGC";
				case Trp: return "UGG";
				case Arg: return "CGU, CGC, CGA, CGG, AGA, AGG";
				case Gly: return "GGU, GGC, GGA, GGG";
			}
			return "What?";
		}
		private static final List<Protein> VALUES =
		Collections.unmodifiableList(Arrays.asList(values()));
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		public static Protein randomProtein()  {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}
	};
}
