package Domain;


public class ProtectedComparator implements AccessModifierComparator {

	@Override
	public boolean compare(String accessMod) {
		return accessMod.equals("protected") || accessMod.equals("public");
	
	}
	
	
}

	
