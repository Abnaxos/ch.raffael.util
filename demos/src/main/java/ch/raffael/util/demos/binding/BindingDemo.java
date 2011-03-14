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

package ch.raffael.util.demos.binding;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.components.feedback.FeedbackPanel;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BindingDemo {

    public static void main(String[] args) throws Exception {
        SwingUtil.setupMetalLookAndFeel();
        final BindingDemoView view = new BindingDemoView();
        final Person person = new Person();
        person.setFirstName("Raffael");
        person.setLastName("Herzog");
        JFrame frame = new JFrame("Binding Demo");
        FeedbackPanel content = new FeedbackPanel();
        content.setBorder(SwingUtil.spacingBorder());
        content.setContent(view.getComponent());
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        //frame.getGlassPane().setVisible(true);
        //frame.getGlassPane().addMouseMotionListener(new MouseMotionListener() {
        //    @Override
        //    public void mouseDragged(MouseEvent e) {
        //    }
        //    @Override
        //    public void mouseMoved(MouseEvent e) {
        //        System.out.println("Mouse: " + e.getPoint());
        //    }
        //});
        Thread.sleep(2000);
        SwingUtil.invokeInEventQueue(new Runnable() {
            @Override
            public void run() {
                view.setPerson(person);
            }
        });
    }

}
