package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Presentation.ClassGenerator;
import Presentation.InterfaceGenerator;

public class InterfaceElement extends DiagramElement{

	public List<String> keywords;
	public String accessModifier;
	public List<MethodElement> methods = new ArrayList<>();
	public List<InterfaceElement> extendedInterfaces = new ArrayList<>();
	public Map<DiagramElement, String> dependencies = new HashMap<>();
	public ClassElement superClass = null;

	public InterfaceElement(Appearance appearance, String name, String accMod, List<String> keywords) {
		super(appearance, name);
		this.accessModifier = accMod;
		this.keywords = keywords;
	}

	public void setInterfaces(List<InterfaceElement> extInterfaces) {
		this.extendedInterfaces = extInterfaces;
	}
	
	public void setSuperClass(ClassElement ce){
		this.superClass = ce;
	}
	
	public void setMethods(List<MethodElement> methods) {
		this.methods = methods;
	}
	
	public void setDependencies(Map<DiagramElement, String> dependencies) {
		this.dependencies = dependencies;
	}
	
	public boolean hasDependencyTo(String otherClassName) {
		for(DiagramElement d : dependencies.keySet())
			if(d.name.equals(otherClassName))
				return true;
		
		return false;
	}
	
	
	public void generateChildren(StringBuilder sb, Diagram diagram) {
		for(DiagramElement method : this.methods)
			method.generate(sb, diagram);
	}
	
	public boolean hasDrawnDependencyTo(String otherClassName) {
		return ((InterfaceGenerator)this.generator).hasDrawnDependencyTo(otherClassName);
	}
	
	public boolean hasMultipleDependenciesTo(String otherClassName){
		for(DiagramElement d : this.dependencies.keySet()){
			if(d.name.equals(otherClassName))
				return this.dependencies.get(d).equals("\"*\"");
		}
		return false;
	}
	
	public void addRelationAnnotation(RelationAnnotation ra) {
		((InterfaceGenerator) this.generator).addRelationAnnotation(ra);
	}
	
	public void addAnnotation(String annotation) {
		((InterfaceGenerator) this.generator).addAnnotation(annotation);
	}
	
}
