/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.bestpractices;
import java.util.List;

import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidLogicInTriggerCustomRule extends AbstractApexRule {

    public AvoidLogicInTriggerCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Style");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 200);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
    	
        List<ASTBlockStatement> blockStatements = node.findDescendantsOfType(ASTBlockStatement.class);

        if (!blockStatements.isEmpty()) {
        	System.out.println("**********AvoidLogicInTriggerCustomRule**********");
        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.LOGIC_IN_TRIGGER, "PMD:Logic in Trigger should be avoided for better flexibility",node.getBeginLine(),BPEnforcerConstants.MEDIUM, BPEnforcerConstants.LOGIC_TRIGGER,"MEDIUM",BPEnforcerConstants.BEST_PRACTICES));
           

        	//addViolation(data, node);
        }

        return data;
    }
}
