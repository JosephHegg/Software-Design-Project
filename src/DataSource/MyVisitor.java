package DataSource;

import java.util.ArrayList;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

public class MyVisitor extends SignatureVisitor {

	public boolean isArray = false;
	public ArrayList<String> seenClasses = new ArrayList<>();
	
	public MyVisitor() {
		super(Opcodes.ASM4);
	}
	
	@Override
	public void visitClassType(String className) {
		seenClasses.add(className);
		super.visitClassType(className);
	}
}
