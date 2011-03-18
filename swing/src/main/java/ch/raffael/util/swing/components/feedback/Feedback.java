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

package ch.raffael.util.swing.components.feedback;

import java.awt.Component;
import java.awt.Point;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Feedback {

    FeedbackPanel.Placement getPlacement();

    void prepare(Component component, @NotNull FeedbackPanel.Placement placement);
    @NotNull
    Point translate(Component component, @NotNull Point reference, @NotNull FeedbackPanel.Placement placement);

    void attach(Component component);
    void detach(Component component);

}
