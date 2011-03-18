/*
 * Copyright 2011 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.raffael.util.binding.validate;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Message {

    private Severity severity;
    private String message;
    private Object details;

    private Message(Severity severity, String message, Object details) {
        this.severity = severity;
        this.message = message;
        this.details = details;
    }

    public static Message error(String message) {
        return new Message(Severity.ERROR, ensureMessage(message), null);
    }

    public static Message error(String message, Object details) {
        return new Message(Severity.ERROR, ensureMessage(message), details);
    }

    public static Message warning(String message) {
        return new Message(Severity.WARNING, ensureMessage(message), null);
    }

    public static Message warning(String message, Object details) {
        return new Message(Severity.WARNING, ensureMessage(message), details);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("severity", severity)
                .add("message", message)
                .add("details", details)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        Message that = (Message)o;
        if ( details != null ? !details.equals(that.details) : that.details != null ) {
            return false;
        }
        if ( !message.equals(that.message) ) {
            return false;
        }
        return severity == that.severity;
    }

    @Override
    public int hashCode() {
        int result = severity.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + (details != null ? details.hashCode() : 0);
        return result;
    }

    public static String ensureMessage(String message) {
        if ( message == null ) {
            return "No message.";
        }
        message = message.trim();
        if ( message.isEmpty() ) {
            return "No message.";
        }
        else {
            return message;
        }
    }

    @NotNull
    public Severity getSeverity() {
        return severity;
    }

    @NotNull 
    public String getMessage() {
        return message;
    }

    public Object getDetails() {
        return details;
    }

    public static enum Severity {
        WARNING{
            @Override
            public Message message(String msg, Object details) {
                return warning(msg, details);
            }
        },
        ERROR {
            @Override
            public Message message(String msg, Object details) {
                return error(msg, details);
            }
        };

        public Message message(String msg) {
            return message(msg, null);
        }

        public abstract Message message(String msg, Object details);

    }

}
