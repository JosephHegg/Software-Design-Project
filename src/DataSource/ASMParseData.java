package DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

public class ASMParseData {
	public List<ClassNode> classNodes = new ArrayList<>();
	public Map<String, ClassNode> classNodeMap = new HashMap<>();
	public Set<String> classNamesToParse;
	public ASM asm;
	public int recursiveDepth = 1;
	
	public ASMParseData(ASM asm, Set<String> classNames) {
		this.asm = asm;
		this.classNamesToParse = classNames;
	}
	
	public ClassNode getClassNode(String className) throws IOException {
		return this.asm.getClassNode(className);
	}
	
	public String replaceSlashes(String className) {
		return className.replace('/', '.');
	}
}
