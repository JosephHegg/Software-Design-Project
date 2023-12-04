package DataSource;

import java.io.IOException;

import org.objectweb.asm.tree.ClassNode;

public class SequenceParseData {

	ASM asm;
	public int recursiveDepth = 1;
	
	public SequenceParseData(ASM asm){
		this.asm = asm;
	}
	
	public ClassNode getClassNode(String className) throws IOException {
		return this.asm.getClassNode(className);
	}
	
	public String replaceSlashes(String className) {
		return className.replace('/', '.');
	}
}
