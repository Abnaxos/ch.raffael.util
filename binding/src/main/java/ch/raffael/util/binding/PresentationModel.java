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

package ch.raffael.util.binding;

import java.awt.Component;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.beans.ObservableSupport;
import ch.raffael.util.binding.validate.Message;
import ch.raffael.util.binding.validate.ValidationEvent;
import ch.raffael.util.binding.validate.ValidationListener;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PresentationModel {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    public static final String PROPERTY_VALID = "valid";
    public static final String PROPERTY_WARNING_COUNT = "warningCount";
    public static final String PROPERTY_ERROR_COUNT = "errorCount";

    private final ObservableSupport observableSupport = new ObservableSupport(this);
    private final Set<Adapter<?, ?>> adapters = new HashSet<Adapter<?, ?>>();
    private BufferGroup bufferGroup;

    private final SetMultimap<Object, Message> validationMessages = LinkedHashMultimap.create();
    private int warningCount = 0;
    private int errorCount = 0;

    private final ValidationListener validationEvents = new ValidationListener() {
        @Override
        public void validationChanged(ValidationEvent event) {
            validationMessages.replaceValues(event.getSubject(), event.getMessages());
            validationUpdate();
            if ( !validationListeners.isEmpty() ) {
                validationListeners.emitter().validationChanged(new ValidationEvent(this, event.getSubject(), event.getMessages(), event.getAddedMessages(), event.getRemovedMessages()));
            }
        }
    };
    private final EventEmitter<ValidationListener> validationListeners = EventEmitter.newEmitter(ValidationListener.class);

    private Binding<Integer> warningCountBinding = null;
    private Binding<Integer> errorCountBinding = null;
    private Binding<Boolean> validBinding = null;

    public PresentationModel() {
        bufferGroup = new BufferGroup();
    }


    public void addValidationListener(ValidationListener listener) {
        validationListeners.addListener(listener);
    }

    public void removeValidationMessageListener(ValidationListener listener) {
        validationListeners.addListener(listener);
    }

    @NotNull
    public BufferGroup getBufferGroup() {
        return bufferGroup;
    }

    public void setBufferGroup(@NotNull BufferGroup bufferGroup) {
        for ( Buffer buffer : this.bufferGroup ) {
            bufferGroup.add(buffer);
        }
        this.bufferGroup = bufferGroup;
    }

    @NotNull
    public <T> T add(@NotNull T member) {
        boolean added = false;
        if ( member instanceof Binding ) {
            added = true;
            //NOP for now
        }
        if ( member instanceof Buffer ) {
            added = true;
            bufferGroup.add((Buffer)member);
        }
        if ( member instanceof Adapter ) {
            added = true;
            if ( adapters.add((Adapter<?, ?>)member) ) {
                ((Adapter<?, ?>)member).addValidationListener(validationEvents);
            }
        }
        if ( !added ) {
            log.warn("{} not added to the presentation model: Unknown type", member);
        }
        if ( member instanceof PresentationModelMember ) {
            ((PresentationModelMember)member).presentationModel = this;
        }
        return member;
    }

    public boolean commitData() {
        validate();
        if ( isValid() ) {
            bufferGroup.commit();
            return true;
        }
        else {
            return false;
        }
    }

    public void flushData() {
        bufferGroup.flush();
    }

    public void validate() {
        for ( Adapter<?, ?> adapter : adapters ) {
            adapter.validate();
        }
    }

    public void scheduleInitialValidation(final Component component) {
        if ( !component.isShowing() ) {
            component.addHierarchyListener(new HierarchyListener() {
                @Override
                public void hierarchyChanged(HierarchyEvent e) {
                    if ( component.isShowing() ) {
                        validate();
                        component.removeHierarchyListener(this);
                    }
                }
            });
        }
        else {
            validate();
        }
    }

    public int getWarningCount() {
        return warningCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public boolean isValid() {
        return errorCount == 0;
    }

    public Binding<Integer> getWarningCountBinding() {
        if ( warningCountBinding == null ) {
            warningCountBinding = new SimpleBinding<Integer>(getWarningCount());
        }
        return warningCountBinding;
    }

    public Binding<Integer> getErrorCountBinding() {
        if ( errorCountBinding == null ) {
            errorCountBinding = new SimpleBinding<Integer>(getErrorCount());
        }
        return errorCountBinding;
    }

    public Binding<Boolean> getValidBinding() {
        if ( validBinding == null ) {
            validBinding = new SimpleBinding<Boolean>(isValid());
        }
        return validBinding;
    }

    public SetMultimap<Object, Message> getValidationMessages() {
        return Multimaps.unmodifiableSetMultimap(validationMessages);
    }

    private void validationUpdate() {
        int oldWarningCount = warningCount;
        int oldErrorCount = errorCount;
        warningCount = 0;
        errorCount = 0;
        for ( Message msg : validationMessages.values() ) {
            switch ( msg.getSeverity() ) {
                case WARNING:
                    warningCount++;
                    break;
                case ERROR:
                    errorCount++;
                    break;
            }
        }
        if ( warningCount != oldWarningCount ) {
            observableSupport.firePropertyChange(PROPERTY_WARNING_COUNT, oldWarningCount, warningCount);
            if ( warningCountBinding != null ) {
                warningCountBinding.setValue(warningCount);
            }
        }
        if ( errorCount != oldErrorCount ) {
            observableSupport.firePropertyChange(PROPERTY_ERROR_COUNT, oldErrorCount, errorCount);
            if ( errorCountBinding != null ) {
                errorCountBinding.setValue(errorCount);
            }
            if ( errorCount == 0 || oldErrorCount == 0 ) {
                boolean valid = (errorCount == 0);
                observableSupport.firePropertyChange(PROPERTY_VALID, !valid, valid);
                if ( validBinding != null ) {
                    validBinding.setValue(valid);
                }
            }
        }
    }

}
