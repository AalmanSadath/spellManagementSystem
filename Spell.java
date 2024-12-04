import java.util.*;

public class Spell {
	String name;
	ArrayList<Spell> reqs;
	ArrayList<Spell> isReqFor;
	boolean explicitlyLearned;
	
	public Spell(String n, boolean learn) {
		name=n;
		reqs=new ArrayList<Spell>();
		isReqFor=new ArrayList<Spell>();
		explicitlyLearned=learn;
	}

}

