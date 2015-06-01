package edu.course.city.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class DebugPhaseListener implements PhaseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugPhaseListener.class);

    @Override
    public void beforePhase(PhaseEvent event) {
        LOGGER.debug("<<< Before " + event.getPhaseId().getName());
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        LOGGER.debug(">>> After " + event.getPhaseId().getName());
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
