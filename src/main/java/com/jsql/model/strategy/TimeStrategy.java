/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 *
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 *******************************************************************************/
package com.jsql.model.strategy;

import org.apache.log4j.Logger;

import com.jsql.model.MediatorModel;
import com.jsql.model.accessible.bean.Request;
import com.jsql.model.exception.PreparationException;
import com.jsql.model.exception.StoppableException;
import com.jsql.model.strategy.blind.ConcreteTimeInjection;
import com.jsql.model.suspendable.AbstractSuspendable;

/**
 * Injection strategy using time attack.
 */
public class TimeStrategy extends AbstractStrategy {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(TimeStrategy.class);

    /**
     * Injection method using time attack.
     */
    private ConcreteTimeInjection timeInjection;
    
    @Override
    public void checkApplicability() throws PreparationException {
        LOGGER.trace("Time based test...");
        
        this.timeInjection = new ConcreteTimeInjection();
        
        this.isApplicable = this.timeInjection.isInjectable();
        
        if (this.isApplicable) {
            allow();
        } else {
            unallow();
        }
    }
    
    @Override
    public void allow() {
        Request request = new Request();
        request.setMessage("MarkTimebasedVulnerable");
        MediatorModel.model().interact(request);
    }

    @Override
    public void unallow() {
        Request request = new Request();
        request.setMessage("MarkTimebasedInvulnerable");
        MediatorModel.model().interact(request);
    }

    @Override
    public String inject(String sqlQuery, String startPosition, AbstractSuspendable stoppable) throws StoppableException {
        return this.timeInjection.inject(
            MediatorModel.model().currentVendor.getValue().getSqlTime(sqlQuery, startPosition),
            stoppable
        );
    }

    @Override
    public void activateStrategy() {
        LOGGER.info("Using timebased injection...");
        MediatorModel.model().setStrategy(Strategy.TIME);
        
        Request request = new Request();
        request.setMessage("MessageBinary");
        request.setParameters(timeInjection.getInfoMessage());
        MediatorModel.model().interact(request);
        
        Request request2 = new Request();
        request2.setMessage("MarkTimebasedStrategy");
        MediatorModel.model().interact(request2);
    }
    
    @Override
    public String getPerformanceLength() {
        return "65565";
    }
    
    @Override
    public String getName() {
        return "Time";
    }
}
