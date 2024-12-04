import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class BuildSpellbook {
	
	public final Integer MAXCOMS = 1000;  // maximum number of specs
	ArrayList<Spell> allSpells;
	ArrayList<Spell> learned;
	

	public BuildSpellbook() {
		allSpells=new ArrayList<Spell>();
		learned=new ArrayList<Spell>();
	}
	
	
	public Vector<String> execNSpecs (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications,
        //       returning required output, one line per string in vector
		
		Vector<String> reply=new Vector<String>();
		for(int i=0;i<Math.min(specs.size(), N);i++) {
			String[] currCommand=specs.get(i).split(" ");
			if(currCommand[0].equals("PREREQ")) {
				prereq(specs.get(i));
				reply.add(specs.get(i));
			}
			else if(currCommand[0].equals("LEARN")) {
				Vector<String> subReply=learnSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("FORGET")) {
				Vector<String> subReply=forgetSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("ENUM")) {
				reply.add(specs.get(i));
				reply.addAll(listLearned());
			}
			else if(currCommand[0].equals("END")) {
				reply.add(specs.get(i));
				break;
			}
		}
		return reply;
	}
	
	
	
	public Vector<String> execNSpecswCheck (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles,
        //       returning required output, one line per string in vector
		
		Vector<String> reply=new Vector<String>();
		for(int i=0;i<Math.min(specs.size(), N);i++) {
			String[] currCommand=specs.get(i).split(" ");
			if(currCommand[0].equals("PREREQ")) {
				prereq(specs.get(i));
				reply.add(specs.get(i));
				if(hasCycle(currCommand[1], new ArrayList<String>(), new ArrayList<String>())) {
					reply.add("   Found cycle in prereqs");
					for(int j=i+1;j<Math.min(N, specs.size());j++){
						reply.add(specs.get(j));
					}
					break;
				}
			}
			else if(currCommand[0].equals("LEARN")) {
				Vector<String> subReply=learnSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("FORGET")) {
				Vector<String> subReply=forgetSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("ENUM")) {
				reply.add(specs.get(i));
				reply.addAll(listLearned());
			}
			else if(currCommand[0].equals("END")) {
				reply.add(specs.get(i));
				break;
			}
		}
		return reply;
	}
	
	public Vector<String> execNSpecswCheckRecLarge (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles and 
		//       recommending fix by removing largest cycle,
		//       returning required output, one line per string in vector

		Vector<String> reply=new Vector<String>();
		String currLargestPrereq=new String();
		int currLargestSize=0;
		for(int i=0;i<Math.min(specs.size(), N);i++) {
			String[] currCommand=specs.get(i).split(" ");
			if(currCommand[0].equals("PREREQ")) {
				prereq(specs.get(i));
				reply.add(specs.get(i));
				List<List<String>> cycles = findCycles(currCommand[1]);
				if(!cycles.isEmpty()) {
					List<String> largestCycle = findCycleToRemove(cycles,true);
					if (largestCycle != null) {
	                    reply.add("   Found cycle in prereqs");
	                    if(currLargestSize<largestCycle.size()) {
	                    	currLargestPrereq=specs.get(i);
	                    	currLargestSize=largestCycle.size();
	                    }
	                    reply.add("   Suggest forgetting "+currLargestPrereq);
					}
					if(!specs.get(i+1).split(" ")[0].equals("PREREQ")) {
						for(int j=i+1;j<Math.min(N, specs.size());j++){
							reply.add(specs.get(j));
						}
						break;
					}
				}
			}
			else if(currCommand[0].equals("LEARN")) {
				Vector<String> subReply=learnSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("FORGET")) {
				Vector<String> subReply=forgetSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("ENUM")) {
				reply.add(specs.get(i));
				reply.addAll(listLearned());
			}
			else if(currCommand[0].equals("END")) {
				reply.add(specs.get(i));
				break;
			}
		}
		return reply;
	}

	public Vector<String> execNSpecswCheckRecSmall (Vector<String> specs, Integer N) {
		// PRE: specs contains set of specifications read in by readSpecsFromFile()
		// POST: executed min(N, all) specifications, checking for cycles and 
		//       recommending fix by removing smallest cycle,
        //       returning required output, one line per string in vector

		Vector<String> reply=new Vector<String>();
		String currSmallestPrereq=new String();
		int currSmallestSize=Integer.MAX_VALUE;
		for(int i=0;i<Math.min(specs.size(), N);i++) {
			String[] currCommand=specs.get(i).split(" ");
			if(currCommand[0].equals("PREREQ")) {
				prereq(specs.get(i));
				reply.add(specs.get(i));
				List<List<String>> cycles = findCycles(currCommand[1]);
				if(!cycles.isEmpty()) {
					List<String> smallestCycle = findCycleToRemove(cycles,false);
					if (smallestCycle != null) {
	                    reply.add("   Found cycle in prereqs");
	                    if(currSmallestSize>smallestCycle.size()) {
	                    	currSmallestPrereq=specs.get(i);
	                    	currSmallestSize=smallestCycle.size();
	                    }
	                    reply.add("   Suggest forgetting "+currSmallestPrereq);
					}
					if(!specs.get(i+1).split(" ")[0].equals("PREREQ")) {
						for(int j=i+1;j<Math.min(N, specs.size());j++){
							reply.add(specs.get(j));
						}
						break;
					}
				}
			}
			else if(currCommand[0].equals("LEARN")) {
				Vector<String> subReply=learnSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("FORGET")) {
				Vector<String> subReply=forgetSpell(currCommand[1], null, true);
				reply.add(specs.get(i));
				if(!subReply.isEmpty()) {
					reply.addAll(subReply);
				}
			}
			else if(currCommand[0].equals("ENUM")) {
				reply.add(specs.get(i));
				reply.addAll(listLearned());
			}
			else if(currCommand[0].equals("END")) {
				reply.add(specs.get(i));
				break;
			}
		}
		return reply;
	}
	
	//HELPERS
	
	private List<List<String>> findCycles(String spellName) {
        List<List<String>> cycles = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();
        Set<String> inStack = new HashSet<>();
        getCycles(spellName, stack, visited, inStack, cycles);
        return cycles;
    }

	private boolean getCycles(String spellName, Stack<String> stack, Set<String> visited, Set<String> inStack, List<List<String>> cycles) {
        if (inStack.contains(spellName)) {
            List<String> cycle = new ArrayList<>(stack.subList(stack.indexOf(spellName), stack.size()));
            cycles.add(cycle);
            return true;
        }
        if (visited.contains(spellName)) {
            return false;
        }
        visited.add(spellName);
        inStack.add(spellName);
        stack.push(spellName);
        Spell currSpell = findSpellAll(spellName).get();
        for (Spell req : currSpell.reqs) {
        	getCycles(req.name, stack, visited, inStack, cycles);
        }
        inStack.remove(spellName);
        stack.pop();
        return false;
    }
	
	 private List<String> findCycleToRemove(List<List<String>> cycles, boolean largest) {
	        List<String> targetCycle = cycles.get(0);
	        for (List<String> cycle : cycles) {
	            if (largest && cycle.size() > targetCycle.size()) {
	                targetCycle = cycle;
	            } 
	            else if (!largest && cycle.size() < targetCycle.size()) {
	                targetCycle = cycle;
	            }
	        }
	        return targetCycle;
	    }
	
	private void prereq(String command){
		String[] currCommand=command.split(" ");
		Spell currSpell;
		if(!isSpell(currCommand[1])) {
			currSpell=new Spell(currCommand[1], false);
			allSpells.add(currSpell);
		}
		else {
			currSpell=findSpellAll(currCommand[1]).get();
		}
		int currSpellInd=findSpellIndex(currCommand[1]);
		for(int j=2;j<currCommand.length;j++) {
			Spell subSpell;
			if(!isSpell(currCommand[j])) {
				subSpell=new Spell(currCommand[j], false);
				allSpells.add(subSpell);
			}
			else {
				subSpell=findSpellAll(currCommand[j]).get();
			}
			if(!currSpell.reqs.contains(subSpell)) {	
				currSpell.reqs.add(subSpell);
			}
		}
		allSpells.set(currSpellInd, currSpell);
	}
	
	private Vector<String> forgetSpell(String spellName, Spell ogSpell, boolean explicit){
		Vector<String> reply=new Vector<String>();
		if(isSpell(spellName)) {
			Spell currSpell=findSpellAll(spellName).get();
			if(learned.contains(currSpell)) {
				if(explicit) {
					if(currSpell.isReqFor.isEmpty()) {	
						learned.remove(currSpell);
						reply.add("   Forgetting "+spellName);
						if(!currSpell.reqs.isEmpty()) {
							ArrayList<Spell> learnedReverse=new ArrayList<Spell>(learned);
							Collections.reverse(learnedReverse);
							for(Spell temp : learnedReverse) {
								if(currSpell.reqs.contains(temp)) {
									reply.addAll(forgetSpell(temp.name, currSpell, false));
								}
							}
						}
						currSpell.explicitlyLearned=false;
						allSpells.set(findSpellIndex(currSpell.name), currSpell);
					}
					else {
						reply.add("   "+spellName+" is still needed");
					}
				}
				else {
					if(currSpell.explicitlyLearned) {//cannot forget explicitly learned as implicit
						currSpell.isReqFor.remove(ogSpell);
					}
					else {
						currSpell.isReqFor.remove(ogSpell);
						if(currSpell.isReqFor.isEmpty()) {
							learned.remove(currSpell);
							reply.add("   Forgetting "+ spellName);
							allSpells.set(findSpellIndex(currSpell.name), currSpell);
							if(!currSpell.reqs.isEmpty()) {
								ArrayList<Spell> learnedReverse=new ArrayList<Spell>(learned);
								Collections.reverse(learnedReverse);
								for(Spell temp : learnedReverse) {
									if(currSpell.reqs.contains(temp)) {
										reply.addAll(forgetSpell(temp.name, currSpell, false));
									}
								}
							}
						}
					}
				}
			}
			else {
				reply.add("   "+ spellName+ " is not learned");
			}
		}
		else {
			reply.add("   "+ spellName+ " is not learned");
		}
		return reply;
	}
	
	private Vector<String> learnSpell(String spellName, Spell ogSpell, boolean explicit){
		Spell currSpell;
		Vector<String> reply=new Vector<String>();
		if(!isSpell(spellName)) {
			currSpell=new Spell(spellName,true);
			allSpells.add(currSpell);
			learned.add(currSpell);
			reply.add("   Learning "+spellName);
		}
		else {
			currSpell=findSpellAll(spellName).get();
			if(learned.contains(currSpell)) {
				if(explicit) {
					reply.add("   "+spellName+" is already learned");
				}
				else {
					currSpell.isReqFor.add(ogSpell);
					allSpells.set(findSpellIndex(currSpell.name), currSpell);
					learned.set(findSpellIndexLearned(currSpell.name), currSpell);
				}
			}
			else {
				if(!currSpell.reqs.isEmpty()) {
					for(Spell temp : currSpell.reqs) {
						Vector<String> subReply=learnSpell(temp.name, currSpell, false);
						if(!subReply.isEmpty()) {
							reply.addAll(subReply);
						}
					}
				}
				if(explicit) {
					currSpell.explicitlyLearned=true;
				}
				else {
					currSpell.isReqFor.add(ogSpell);
				}
				allSpells.set(findSpellIndex(currSpell.name), currSpell);
				learned.add(currSpell);
				reply.add("   Learning "+spellName);
			}
		}
		return reply;
	}
	
	private Vector<String> listLearned(){
		Vector<String> reply=new Vector<String>();
		for(Spell temp : learned) {
			reply.add("   "+temp.name);
		}
		return reply;
	}
	
	private boolean hasCycle(String spellName, ArrayList<String> visited, ArrayList<String> recursion) {
		if(recursion.contains(spellName)) {
			return true;
		}
		if(visited.contains(spellName)) {
			return false;
		}
		visited.add(spellName);
		recursion.add(spellName);
		Spell currSpell=findSpellAll(spellName).get();
		for(Spell temp : currSpell.reqs) {
			if(hasCycle(temp.name, visited, recursion)) {
				return true;
			}
		}
		recursion.remove(spellName);
		return false;
	}
	
	private boolean isSpell(String spellName) {
		if(allSpells.size()<=0) {
			return false;
		}
		for(Spell temp : allSpells) {
			if(temp.name.equals(spellName)) {
				return true;
			}
		}
		return false;
	}
	
	private Optional<Spell> findSpellAll(String name) {
		for(Spell temp : allSpells) {
			if(temp.name.equals(name)) {
				return Optional.of(temp);
			}
		}
		return Optional.empty();
	}
	
	private int findSpellIndex(String name) {
		for(int i=0;i<allSpells.size();i++) {
			if(allSpells.get(i).name.equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	private int findSpellIndexLearned(String name) {
		for(int i=0;i<learned.size();i++) {
			if(learned.get(i).name.equals(name)) {
				return i;
			}
		}
		return -1;
	}

	// Provided files below

	public Vector<String> readSpecsFromFile(String fInName) throws IOException {
		// PRE: -
		// POST: returns lines from input file as vector of string

		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<String> comList = new Vector<String>();
		
		while ((s = fIn.readLine()) != null) {
			comList.add(s);
		}
		fIn.close();
		
		return comList;
	}

	public Vector<String> readSolnFromFile(String fInName, Integer N) throws IOException {
		// PRE: -
		// POST: returns (up to) N lines from input file as a vector of N strings;
		//       only the specification lines are counted in this N, not responses

		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<String> out = new Vector<String>();
		Integer i = 0;

		while (((s = fIn.readLine()) != null) && (i <= N)) {
			if ((i != N) || s.startsWith("   ")) // responses to commands start with three spaces
				out.add(s);
			if (!s.startsWith("   "))  
				i += 1;
		}
		fIn.close();
		
		return out;
	}
	
	public Boolean compareExecWSoln (Vector<String> execd, Vector<String> soln) {
		// PRE: -
		// POST: Returns True if execd and soln string-match exactly, False otherwise

		if (execd.size() != soln.size()) {
			return Boolean.FALSE;
		}
		for (int i = 0; i < execd.size(); i++) {
			if (!execd.get(i).equals(soln.get(i))) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;

	}

	

	public static void main(String[] args) {
		

	}
}

