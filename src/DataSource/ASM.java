package DataSource;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;


public class ASM {

	public ClassNode getClassNode(String className) throws IOException{
		ClassReader reader = new ClassReader(className);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode,  ClassReader.EXPAND_FRAMES);
		return classNode;
		
	}
}
