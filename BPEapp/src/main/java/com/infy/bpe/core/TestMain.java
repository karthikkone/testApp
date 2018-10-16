/*package com.infy.bpe.core;
*//**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 *//*



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.sourceforge.pmd.SourceCodeProcessor;

import com.sforce.soap.tooling.sobject.ApexClass;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;

public class TestMain {

    
    public static void main(String args[]) {
    	Node node;
		ApexClass c;
    	
    	System.out.println("mthod scnanning PMD");
    	
    	
    	MethodNamingConventionsCustomRule rule = new MethodNamingConventionsCustomRule();
    	
    	
		RuleContext ruleCtx = new RuleContext();
		LanguageVersion javaLanguageVersion = LanguageRegistry.getLanguage("Apex").getDefaultVersion();
		ParserOptions parserOptions = javaLanguageVersion.getLanguageVersionHandler().getDefaultParserOptions();
		Parser parser = javaLanguageVersion.getLanguageVersionHandler().getParser(parserOptions);
		
		String filePath = "D:\\TestClasses\\SObjectDataLoader.cls";
		File file = new File("D:\\TestClasses\\SObjectDataLoader.cls");
		

		 StringBuilder contentBuilder = new StringBuilder();
		    try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
		    {
		 
		        String sCurrentLine;
		        while ((sCurrentLine = br.readLine()) != null)
		        {
		            contentBuilder.append(sCurrentLine).append("\n");
		        }
		    }
		    catch (IOException e)
		    {
		        e.printStackTrace();
		    }
		    
	 String sourceCode= contentBuilder.toString(); 
	 

        Reader reader = new StringReader(sourceCode);

        node= parser.parse("SobjectDataLoader", reader);
       // node = parser.parse(apexComponent.getName(), new StringReader(apexComponent.getBody()));
		//rule.apply(Arrays.asList(node), ruleCtx);
		
        LanguageVersion languageVersion = ruleCtx.getLanguageVersion();
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
      //  Parser parser = PMD.parserFor(languageVersion, configuration);

        Node rootNode = parse(ruleCtx, reader, parser);
        resolveQualifiedNames(rootNode, languageVersionHandler);
        symbolFacade(rootNode, languageVersionHandler);
        Language language = languageVersion.getLanguage();
        usesDFA(languageVersion, rootNode, ruleSets, language);
        usesTypeResolution(languageVersion, rootNode, ruleSets, language);
        usesMultifile(rootNode, languageVersionHandler, ruleSets, language);

        List<Node> acus = Collections.singletonList(rootNode);
        ruleSets.apply(acus, ctx, language);

		//String s = "Apex Rules";
		
		
		
		
		
    }
    private static Node parse(RuleContext ctx, Reader sourceCode, Parser parser) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            Node rootNode = parser.parse(ctx.getSourceCodeFilename(), sourceCode);
            ctx.getReport().suppress(parser.getSuppressMap());
            return rootNode;
        }
    }
    
    private static void symbolFacade(Node rootNode, LanguageVersionHandler languageVersionHandler) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.SYMBOL_TABLE)) {
            languageVersionHandler.getSymbolFacade(configuration.getClassLoader()).start(rootNode);
        }
    }
    
    private static void resolveQualifiedNames(Node rootNode, LanguageVersionHandler handler) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.QUALIFIED_NAME_RESOLUTION)) {
            handler.getQualifiedNameResolutionFacade(configuration.getClassLoader()).start(rootNode);
        }
    }

	
    
}*/