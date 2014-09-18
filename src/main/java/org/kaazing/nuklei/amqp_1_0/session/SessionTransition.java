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

/*
 * See AMQP 1.0 specification, section 2.4.7 "Connection State Diagram"
 */
public enum SessionTransition {
    RECEIVED_BEGIN,
    SENT_BEGIN,
    RECEIVED_FLOW,
    SENT_FLOW,
    RECEIVED_DISPOSITION,
    SENT_DISPOSITION,
    RECEIVED_END,
    SENT_END,
    ERROR
}