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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.JTextComponent;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import ch.raffael.util.binding.Adapter;
import ch.raffael.util.binding.BeanPropertyBinding;
import ch.raffael.util.binding.BufferedBinding;
import ch.raffael.util.binding.PresentationModel;
import ch.raffael.util.binding.SimpleBinding;
import ch.raffael.util.binding.validate.validators.NotEmptyValidator;
import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.binding.EnabledAdapter;
import ch.raffael.util.swing.binding.TextComponentAdapter;
import ch.raffael.util.swing.binding.ValidationFeedbackManager;
import ch.raffael.util.swing.components.feedback.FeedbackPanel;
import ch.raffael.util.swing.components.feedback.IconTextFeedback;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BindingDemoView {

    private JPanel root;
    private JTextField firstName;
    private JTextField lastName;
    private JLabel nameDisplay;
    private JButton revertButton;
    private JButton applyButton;

    private final PropertyChangeListener viewUpdater = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( person.getValue() == null ) {
                nameDisplay.setText("<No Person>");
            }
            else {
                nameDisplay.setText(person.getValue().getLastName() + ", " + person.getValue().getFirstName());
            }
        }
    };
    private final PresentationModel model = new PresentationModel();
    private final SimpleBinding<Person> person = new SimpleBinding<Person>();

    public BindingDemoView() {
        person.addPropertyChangeListener(viewUpdater);
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.commitData();
            }
        });
        revertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.flushData();
            }
        });
        model.addAdapter(bind("firstName", firstName));
        TextComponentAdapter lastNameAdapter = bind("lastName", lastName);
        lastNameAdapter.setValidator(new NotEmptyValidator());
        model.addAdapter(lastNameAdapter);
        model.addAdapter(new EnabledAdapter(model.getValidBinding(), applyButton));
        //root.addAncestorListener(new AncestorListener() {
        //    @Override
        //    public void ancestorAdded(AncestorEvent event) {
        //        FeedbackPanel feedbackPanel = SwingUtil.findComponent(root, FeedbackPanel.class);
        //        if ( feedbackPanel != null ) {
        //            System.out.println("Adding feedback");
        //            root.removeAncestorListener(this);
        //            feedbackPanel.add(new IconTextFeedback(new ImageIcon(getClass().getResource("/ch/raffael/util/swing/binding/feedback-error.png")), "Test-Feedback", FeedbackPanel.Placement.BOTTOM_RIGHT), firstName);
        //
        //            feedbackPanel.revalidate();
        //        }
        //    }
        //
        //    @Override
        //    public void ancestorRemoved(AncestorEvent event) {
        //    }
        //
        //    @Override
        //    public void ancestorMoved(AncestorEvent event) {
        //    }
        //});
        model.addValidationListener(new ValidationFeedbackManager());
        model.flushData();
        model.validate();
    }

    public Person getPerson() {
        return person.getValue();
    }

    public void setPerson(Person bean) {
        if ( person.getValue() != null ) {
            person.removePropertyChangeListener(viewUpdater);
        }
        person.setValue(bean);
        if ( bean != null ) {
            bean.addPropertyChangeListener(viewUpdater);
        }
        model.flushData();
    }

    private TextComponentAdapter bind(String property, JTextComponent target) {
        BeanPropertyBinding<String, Person> propBinding = new BeanPropertyBinding<String, Person>(property, person);
        model.add(propBinding);
        BufferedBinding<String> buffer = new BufferedBinding<String>(propBinding);
        model.add(buffer);
        return new TextComponentAdapter().install(target, buffer);
    }

    public JComponent getComponent() {
        return root;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this
     * method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Edit"));
        final JLabel label1 = new JLabel();
        label1.setText("First Name");
        label1.setDisplayedMnemonic('F');
        label1.setDisplayedMnemonicIndex(0);
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Last Name");
        label2.setDisplayedMnemonic('L');
        label2.setDisplayedMnemonicIndex(0);
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        firstName = new JTextField();
        panel1.add(firstName, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lastName = new JTextField();
        panel1.add(lastName, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        revertButton = new JButton();
        revertButton.setText("Revert");
        revertButton.setMnemonic('R');
        revertButton.setDisplayedMnemonicIndex(0);
        panel2.add(revertButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        applyButton = new JButton();
        applyButton.setText("Apply");
        applyButton.setMnemonic('A');
        applyButton.setDisplayedMnemonicIndex(0);
        panel2.add(applyButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Current Value"));
        nameDisplay = new JLabel();
        nameDisplay.setText("Label");
        panel3.add(nameDisplay, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1.setLabelFor(firstName);
        label2.setLabelFor(lastName);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }
}
