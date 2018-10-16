/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package com.infy.pmd.bestpractices;
import com.infy.bpe.core.CodeAnalyser;
import com.infy.bpe.core.ToolingOperations;
import com.infy.utility.BPEnforcerConstants;
import com.infy.utility.CategoryConstants;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.GLOBAL;

import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidGlobalModifierCustomRule extends AbstractApexRule {

    public AvoidGlobalModifierCustomRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Style");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        return checkForGlobal(node, data);
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        return checkForGlobal(node, data);
    }

    private Object checkForGlobal(ApexNode<?> node, Object data) {
        ASTModifierNode modifierNode = node.getFirstChildOfType(ASTModifierNode.class);

        if (modifierNode != null && modifierNode.getNode().getModifiers().has(GLOBAL)) {
        	//System.out.println("**********AvoidGlobalModifierCustomRule**********");
        	
        	//System.out.println(node.getNode().getDefiningType().getApexName());
        	//System.out.println(node.getBeginLine());
        	//System.out.println (node.getEndLine());
        	//System.out.println(getPriority());
        	CodeAnalyser.addBestPracticesRule(ToolingOperations.setClassReportDtls(node.getNode().getDefiningType().getApexName(), CategoryConstants.AVOID_GLOBAL_MODIFIER, "PMD:Global classes may be avoided  ",node.getBeginLine(),BPEnforcerConstants.LOW, BPEnforcerConstants.AVOID_GLOBAL,"MEDIUM",BPEnforcerConstants.BEST_PRACTICES));
        	
            //addViolation(data, node);
        }

        return data;
    }
}
