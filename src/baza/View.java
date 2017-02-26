package baza;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View {

	public View() {
	
	}

	public static void main(String[] args) {
		try {
			FemaleName s = new FemaleName("Marcin");
			Map<String, Double> names = s.getNameMap();
			System.out.println(names.get("Marci"));
			//przejedz();
		} catch (Exception e) {
			// TODO: handle exception
		}
		Scanner in = new Scanner(System.in);
		while(true){
			String z = in.next();
			FemaleName name = new FemaleName(z);
			System.out.println(name);
		}
	}
	static void przejedz() throws Exception{
		Set<String> retMap = new HashSet<String>();
		BazaConnection polaczenie = new BazaConnection();
		PreparedStatement stmnt = polaczenie.myConn.prepareStatement("select * from imionam");
		ResultSet rs = stmnt.executeQuery(); 
		while(rs.next()){
			retMap.add(rs.getString("name"));
		}
		for (String a: retMap){
			System.out.println(a +" is now "+new FemaleName(a));
		}
	}

}

class FemaleName{
	String mName;
	String fName;
	Set<Rank> setOfNames;
	FemaleName(String name){
		
		this.mName = name;
		try {
			Map<String, Double> names = this.getNameMap();
			setOfNames = new TreeSet<Rank>();
				
			for (String ab : names.keySet()){
				setOfNames.add(new Rank(ab, names.get(ab)));
			}
			if (this.setOfNames.size()==0){
				this.inventNames();
			}
			
			Iterator iter = setOfNames.iterator();
			Rank help = (Rank)iter.next();

			fName = help.imie;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Map<String, Double> getNameMap() throws Exception{
		Map<String, Double> retMap = new HashMap<String,Double>();
		BazaConnection polaczenie = new BazaConnection();
		PreparedStatement stmnt = polaczenie.myConn.prepareStatement("select * from imionak where name not in (select name from imionam) and name like ?");
		ResultSet rs; 
		HashSet<String> sylabus = this.getSylabus();

		for (String a : sylabus){
			if (a.length()==0) continue;
		
			stmnt.setString(1, "%"+a+"%");
			rs = stmnt.executeQuery();
	
			while(rs.next()){
				retMap.put(rs.getString("name"),rs.getDouble("nr"));
			}
			stmnt.setString(1, a +"%");
			rs = stmnt.executeQuery();
			while(rs.next()){
				retMap.put(rs.getString("name"),rs.getDouble("nr"));
			}
			stmnt.setString(1, "%"+a);
			rs = stmnt.executeQuery();
			while(rs.next()){
				retMap.put(rs.getString("name"),rs.getDouble("nr"));
			}
			stmnt.setString(1, this.mName.charAt(0)+"%"+a +"%");
			rs = stmnt.executeQuery();
			while(rs.next()){
				retMap.put(rs.getString("name"),rs.getDouble("nr"));
			}
			stmnt.setString(1, this.mName.charAt(0)+"%"+a);
			rs = stmnt.executeQuery();
			while(rs.next()){
				retMap.put(rs.getString("name"),rs.getDouble("nr"));
			}
			for (int i=0;i<this.mName.length();i++){
				for (int j=1;j<this.mName.length()-i;j++){
					String as = this.mName.substring(i,i+j);
					stmnt.setString(1,as);
					rs = stmnt.executeQuery();
					while(rs.next()){
						retMap.put(rs.getString("name"),rs.getDouble("nr"));
					}
				}
			}
			
		}
		for (String a : retMap.keySet()){
			Double z = retMap.get(a);
			z /= 100000;
			for (int i=0;i<this.mName.length();i++){
				
				try {
					if (a.charAt(i) == this.mName.charAt(i)) {
						z += 10*(1/(i+1));
					}
				} catch (Exception e) {
					break;
				}
				
			}
			for (String i: sylabus){
				if(a.toLowerCase().contains(i.toLowerCase())){
					z+= 5*i.length();
					//System.out.println(i + " " + a);
					if (a.length() >=3 && i.compareTo(a.substring(0, 3).toLowerCase()) == 0 && (this.mName.toLowerCase().charAt(0) != i.toLowerCase().charAt(0))){
						
						z+=10;
					}
				}
			}
			
			String b = a.toLowerCase();
			String c =this.mName.toLowerCase();
			int n=0;
			for (int i= 0; i<c.length();i++){
				for (int h= 0; h<b.length();h++){
					if (c.charAt(i) == b.charAt(h)){
						z+=3;
						n++;
						b = b.replaceFirst("["+b.charAt(h)+"]", "!");
						c = c.replaceFirst("["+c.charAt(i)+"]", "?");
						
						continue;
					}
				}
			}
			//if(n == 3) z = (z*n)/a.length();
			//else{
			//	z = (double) ((1/n)/a.length());
			//}
		
			retMap.put(a, z);
		}
		if (retMap.get(mName)!=null) retMap.remove(mName);
		polaczenie.myConn.close();
		return retMap;
	}
	HashSet<String> getSylabus(){
		HashSet<String> pats = new HashSet<>();
		Pattern b = Pattern.compile("[B-DF-HJ-NP-TV-Zb-df-hj-np-tv-z]+[AEIOUYaeiouy]+[b-df-hj-np-tv-z]+");
		Pattern b2 = Pattern.compile("[AEIOUYaeiouy]+[B-DF-HJ-NP-TV-Zb-df-hj-np-tv-z]+[AEIOUYaeiouy]+");
		Pattern b3 = Pattern.compile("[A-z]{0,3}");
		Pattern b4 = Pattern.compile("[A-z]{0,2}");
		addToSylabus(pats, b.matcher(this.mName));
		addToSylabus(pats, b2.matcher(this.mName));
		addToSylabus(pats, b3.matcher(this.mName));
		addToSylabus(pats, b4.matcher(this.mName));
		if(pats.contains(""))pats.remove("");
		return pats;
	}
	void addToSylabus(HashSet<String> pat, Matcher c){
		while(c.find()){
            pat.add(c.group().toLowerCase());
        }
	}
	public void showTop5(){
		if (this.setOfNames.size()<5){
			inventNames();
		}
		
		Iterator it = this.setOfNames.iterator();
		
		for (int i=0; i<5; i++){
			Rank helper = (Rank)it.next();
			System.out.println(helper.imie + " " + helper.rank + " " + helper.imie.hashCode());
		}
	}public void showTopx(int x){
		
		
		Iterator it = this.setOfNames.iterator();
		
		for (int i=0; i<x; i++){
			Rank helper = (Rank)it.next();
			System.out.println(helper.imie + " " + helper.rank + " " + helper.imie.hashCode());
		}
	}
	private void inventNames(){
		int sa = 0;
		HashSet<String> strings = new HashSet<>();
		while(this.setOfNames.size()<5){
			sa++;
		for (String i : this.getSylabus()){
			String a = i.charAt(i.length()-1)+"";
			Pattern nowy = Pattern.compile("[nms]+");
			Pattern nowy2 = Pattern.compile("[^aeiouy]+");
			Pattern nowy3 = Pattern.compile("[kbr]+");
			Pattern nowy4 = Pattern.compile("[aeio]+");
			Pattern nowy5 = Pattern.compile("[u]+");
			Pattern nowy6 = Pattern.compile("[y]+");
			//String[] rand = {"y","ina","ine","uine"};
			ArrayList<String> rand = new ArrayList<>();
			
			rand.add("y");
			rand.add("ine");
			rand.add("ina");
			rand.add("iana");
			rand.add("ilia");
			rand.add("ia");
			rand.add("a");
			strings.add(this.createRandomString(nowy2.matcher(a), i, rand));
			rand.add("elle");
			rand.add("ella");

			strings.add(this.createRandomString(nowy.matcher(a), i+i.charAt(i.length()-1),rand));
		
			rand.add("quine");
			rand.add("sia");
			rand.add("issa");
			strings.add(this.createRandomString(nowy3.matcher(a), i.substring(0,i.length()-1), rand));
			rand = new ArrayList<>();
			rand.add("nna");
			rand.add("my");
			rand.add("ny");
			rand.add("ry");
			rand.add("cy");
			rand.add("nne");
			rand.add("ria");
			rand.add("cia");
			rand.add("ndy");
			strings.add(this.createRandomString(nowy4.matcher(a), i, rand));
			rand = new ArrayList<>();
			rand.add("my");
			rand.add("cy");
			rand.add("cia");
			rand.add("sia");
			rand.add("lla");
			strings.add(this.createRandomString(nowy5.matcher(a), i, rand));
			rand = new ArrayList<>();
			rand.add("ria");
			rand.add("sia");
			rand.add("ndy");
			rand.add("a");
			strings.add(this.createRandomString(nowy6.matcher(a), i, rand));
			for (String ab : strings){
				if(ab != null){
					//Rank s = strings.get(z);
					//ab.rank = ab.rank -0.1;
					Double s = (double)ab.length();
					if (this.setOfNames.add(new Rank(ab,s ))){
						System.out.println("dodano");
					}
					
					
					
		
					
				}
			}
		}
		}
	}
	String createRandomString(Matcher maczer,String base, ArrayList<String> rand){
		String dodaj = null;
		if (maczer.find()){
			String doda;
			Random s = new Random();
			int randex = s.nextInt(rand.size());
			doda = base+rand.get(randex);
			String doda2 = doda.substring(1, doda.length());

			//System.out.println(doda + this.setOfNames.size());
			dodaj = Character.toUpperCase(doda.charAt(0)) + doda2;
			
		}
		return dodaj;
	}
	@Override
	public String toString(){
		return fName;
	}
    public class Rank implements Comparable<Rank>{
		public Double rank;
		public String imie;
		Rank(String a, Double n){
			this.imie = a;
			this.rank = n;
			//this.equals(this);
		
		
		}
		
		public boolean equals(Rank o){
			if (!(o instanceof Rank))return false;
			System.out.println("sfsf");
			Rank s = o;

			return imie.equals(s.imie);
		}
		@Override
		public int hashCode() {
			
		      return imie.hashCode();
		  }
		@Override
		public int compareTo(Rank o) {

			if (o.imie.equals(this.imie)){
				return 0;
			}else if (o.rank > this.rank){
				return 1;
			}else if(o.rank < this.rank){
				return -1;
			}else{
				return 1;
			}
		
		}
		
	}
	
}
