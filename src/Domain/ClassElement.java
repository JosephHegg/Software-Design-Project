package Domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.parser.Element;

import org.objectweb.asm.tree.ClassNode;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

import DataSource.ASM;
import Presentation.ClassGenerator;

public class ClassElement extends DiagramElement {
	public String accessModifier;
	public List<String> keywords;
	public List<FieldElement> fields = new ArrayList<>();
	public List<MethodElement> methods = new ArrayList<>();

	public List<InterfaceElement> interfaces = new ArrayList<>();
	public ClassElement superClass;
	public Map<DiagramElement, String> dependencies = new HashMap<>();
	public Map<DiagramElement, String> associations = new HashMap<>();

	public ClassElement(Appearance appearance, String name, String accessModifier, List<String> keywords) {
		super(appearance, name);
		this.accessModifier = accessModifier;
		this.keywords = keywords;
	}

	// Dummy class for dependencies
	public ClassElement(Appearance appearance, String name) {
		super(appearance, name);
	}

	public void setInterfacesList(List<InterfaceElement> interfaces) {
		this.interfaces = interfaces;
	}

	public void setSuperClass(ClassElement superClass) {
		this.superClass = superClass;
	}

	public void setDependencies(Map<DiagramElement,String> dependencies) {
		this.dependencies = dependencies;
	}
	
	public void setAssociations(Map<DiagramElement,String> associations) {
		this.associations = associations;
	}

	public void setFields(List<FieldElement> fields) {
		this.fields = fields;
	}
	
	public boolean hasDependencyTo(String otherClassName) {
		for(DiagramElement d : dependencies.keySet())
			if(d.name.equals(otherClassName))
				return true;
		return false;
	}
	
	public boolean hasAssociation(String otherClassName) {
		for(DiagramElement a : associations.keySet())
			if(a.name.equals(otherClassName))
				return true;
		return false;
	}

	public void setMethods(List<MethodElement> methods) {
		this.methods = methods;
	}
	
	public boolean hasDrawnAssociationTo(String otherClassName) {
		return ((ClassGenerator)this.generator).hasDrawnAssociationTo(otherClassName);
	}
	
	public boolean hasDrawnDependencyTo(String otherClassName) {
		return ((ClassGenerator)this.generator).hasDrawnDependencyTo(otherClassName);
	}
	
	public boolean hasMultipleAssociationsTo(String otherClassName) {
		
		for(DiagramElement a : this.associations.keySet()){
			if(a.name.equals(otherClassName))
				return this.associations.get(a).equals("\"*\"");
		}
		return false;
		
		
	}
	
	public boolean hasMultipleDependenciesTo(String otherClassName) {
		for(DiagramElement d : this.dependencies.keySet()){
			if(d.name.equals(otherClassName))
				return this.dependencies.get(d).equals("\"*\"");
		}
		
		return false;
	}
	
	public void addRelationAnnotation(RelationAnnotation annotation) {
		((ClassGenerator) this.generator).addRelationAnnotation(annotation);
	}
	
	public void addAnnotation(String annotation) {
		((ClassGenerator) this.generator).addAnnotation(annotation);
	}

	
}
