package com.grahamlea.glissmetrics.aspects.spring;

import com.grahamlea.glissmetrics.aspects.ReturnValueCountingAspect;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpelExpressionReturnValueCountingAspect extends ReturnValueCountingAspect {

    private Expression countingPredicate;

    @Override
    protected final boolean shouldBeCounted(Object returnValue) {
        return countingPredicate.getValue(returnValue, Boolean.class);
    }

    public void setCountingPredicateSpelExpression(String expression) {
        this.countingPredicate = new SpelExpressionParser().parseExpression(expression);
    }
}
