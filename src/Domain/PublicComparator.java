package Domain;

public class PublicComparator implements AccessModifierComparator {

	@Override
	public boolean compare(String accessMod) {
		return accessMod.equals("public");
	}

}
