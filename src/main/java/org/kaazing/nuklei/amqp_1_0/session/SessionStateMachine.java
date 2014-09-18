/*
 * Copyright 2014 Kaazing Corporation, All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.nuklei.amqp_1_0.session;

import static java.util.EnumSet.allOf;

import org.kaazing.nuklei.amqp_1_0.codec.transport.Begin;
import org.kaazing.nuklei.amqp_1_0.codec.transport.Disposition;
import org.kaazing.nuklei.amqp_1_0.codec.transport.End;
import org.kaazing.nuklei.amqp_1_0.codec.transport.Flow;
import org.kaazing.nuklei.amqp_1_0.codec.transport.Frame;


/*
 * See AMQP 1.0 specification, section 2.5.5 "Session States"
 */
public final class SessionStateMachine {

    private final SessionHooks sessionHooks;
    
    public SessionStateMachine(SessionHooks sessionHooks) {
        this.sessionHooks = sessionHooks;
    }

    public void start(Session session) {
        session.state = SessionState.UNMAPPED;
        sessionHooks.whenInitialized.accept(session);
    }
    
    public void received(Session session, Frame frame, Begin begin) {
        transition(session, SessionTransition.RECEIVED_BEGIN);
        sessionHooks.whenBeginReceived.accept(session, frame, begin);
    }
    
    public void sent(Session session, Frame frame, Begin begin) {
        transition(session, SessionTransition.SENT_BEGIN);
        sessionHooks.whenBeginSent.accept(session, frame, begin);
    }
    
    public void received(Session session, Frame frame, Flow flow) {
        transition(session, SessionTransition.RECEIVED_FLOW);
        sessionHooks.whenFlowReceived.accept(session, frame, flow);
    }
    
    public void sent(Session session, Frame frame, Flow flow) {
        transition(session, SessionTransition.SENT_FLOW);
        sessionHooks.whenFlowSent.accept(session, frame, flow);
    }
    
    public void received(Session session, Frame frame, Disposition disposition) {
        transition(session, SessionTransition.RECEIVED_DISPOSITION);
        sessionHooks.whenDispositionReceived.accept(session, frame, disposition);
    }
    
    public void sent(Session session, Frame frame, Disposition disposition) {
        transition(session, SessionTransition.SENT_DISPOSITION);
        sessionHooks.whenDispositionSent.accept(session, frame, disposition);
    }
    
    public void received(Session session, Frame frame, End end) {
        transition(session, SessionTransition.RECEIVED_END);
        sessionHooks.whenEndReceived.accept(session, frame, end);
    }
    
    public void sent(Session session, Frame frame, End end) {
        transition(session, SessionTransition.SENT_END);
        sessionHooks.whenEndSent.accept(session, frame, end);
    }
    
    public void error(Session session) {
        transition(session, SessionTransition.ERROR);
        sessionHooks.whenError.accept(session);
    }

    private static void transition(Session session, SessionTransition transition) {
        session.state = STATE_MACHINE[session.state.ordinal()][transition.ordinal()];
    }
   
    private static final SessionState[][] STATE_MACHINE;
    
    static {
        int stateCount = SessionState.values().length;
        int transitionCount = SessionTransition.values().length;

        SessionState[][] stateMachine = new SessionState[stateCount][transitionCount];
        for (SessionState state : allOf(SessionState.class)) {
            for (SessionTransition transition : allOf(SessionTransition.class)) {
                // default transition to "end" state
                stateMachine[state.ordinal()][transition.ordinal()] = SessionState.UNMAPPED;
            }

            // default "error" transition to "discarding" state
            stateMachine[state.ordinal()][SessionTransition.ERROR.ordinal()] = SessionState.DISCARDING;
        }
        
        stateMachine[SessionState.UNMAPPED.ordinal()][SessionTransition.RECEIVED_BEGIN.ordinal()] = SessionState.BEGIN_RECEIVED;
        stateMachine[SessionState.UNMAPPED.ordinal()][SessionTransition.SENT_BEGIN.ordinal()] = SessionState.BEGIN_SENT;
        stateMachine[SessionState.BEGIN_RECEIVED.ordinal()][SessionTransition.SENT_BEGIN.ordinal()] = SessionState.MAPPED;
        stateMachine[SessionState.BEGIN_SENT.ordinal()][SessionTransition.RECEIVED_BEGIN.ordinal()] = SessionState.MAPPED;
        stateMachine[SessionState.MAPPED.ordinal()][SessionTransition.RECEIVED_END.ordinal()] = SessionState.END_RECEIVED;
        stateMachine[SessionState.MAPPED.ordinal()][SessionTransition.SENT_END.ordinal()] = SessionState.END_SENT;
        stateMachine[SessionState.MAPPED.ordinal()][SessionTransition.RECEIVED_FLOW.ordinal()] = SessionState.MAPPED;
        stateMachine[SessionState.MAPPED.ordinal()][SessionTransition.SENT_FLOW.ordinal()] = SessionState.MAPPED;
        stateMachine[SessionState.MAPPED.ordinal()][SessionTransition.RECEIVED_DISPOSITION.ordinal()] = SessionState.MAPPED;
        stateMachine[SessionState.MAPPED.ordinal()][SessionTransition.SENT_DISPOSITION.ordinal()] = SessionState.MAPPED;
        stateMachine[SessionState.END_RECEIVED.ordinal()][SessionTransition.SENT_END.ordinal()] = SessionState.UNMAPPED;
        stateMachine[SessionState.END_SENT.ordinal()][SessionTransition.RECEIVED_END.ordinal()] = SessionState.UNMAPPED;
        
        STATE_MACHINE = stateMachine;
    }

}