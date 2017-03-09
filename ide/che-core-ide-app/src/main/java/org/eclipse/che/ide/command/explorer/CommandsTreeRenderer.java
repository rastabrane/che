/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.command.explorer;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

import org.eclipse.che.ide.api.data.tree.Node;
import org.eclipse.che.ide.command.CommandResources;
import org.eclipse.che.ide.command.explorer.CommandsExplorerView.ActionDelegate;
import org.eclipse.che.ide.command.node.CommandFileNode;
import org.eclipse.che.ide.command.node.CommandGoalNode;
import org.eclipse.che.ide.ui.smartTree.Tree;
import org.eclipse.che.ide.ui.smartTree.TreeStyles;
import org.eclipse.che.ide.ui.smartTree.presentation.DefaultPresentationRenderer;
import org.vectomatic.dom.svg.ui.SVGResource;

import static com.google.gwt.user.client.Event.ONCLICK;

/**
 * Renderer for the commands tree.
 *
 * @author Artem Zatsarynnyi
 */
class CommandsTreeRenderer extends DefaultPresentationRenderer<Node> {

    private final CommandResources resources;

    private ActionDelegate delegate;

    CommandsTreeRenderer(TreeStyles treeStyles, CommandResources resources, ActionDelegate delegate) {
        super(treeStyles);

        this.resources = resources;
        this.delegate = delegate;
    }

    /** Sets the delegate that will handle events from the rendered DOM elements. */
    void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Element getPresentableTextContainer(Element content) {
        final Element presentableTextContainer = super.getPresentableTextContainer(content);
        presentableTextContainer.addClassName(resources.commandsExplorerCss().commandNodeText());

        return presentableTextContainer;
    }

    @Override
    public Element render(Node node, String domID, Tree.Joint joint, int depth) {
        final Element element = super.render(node, domID, joint, depth);
        final Element nodeContainerElement = element.getFirstChildElement();

        if (node instanceof CommandFileNode) {
            renderCommandGoalNode((CommandFileNode)node, nodeContainerElement);
        } else if (node instanceof CommandGoalNode) {
            renderCommandGoalNode(nodeContainerElement);
        }

        return element;
    }

    private void renderCommandGoalNode(CommandFileNode node, Element nodeContainerElement) {
        nodeContainerElement.addClassName(resources.commandsExplorerCss().commandNode());

        final Element removeCommandButton = createButton(resources.removeCommand());
        Event.setEventListener(removeCommandButton, event -> {
            if (ONCLICK == event.getTypeInt()) {
                event.stopPropagation();
                delegate.onCommandRemove(node.getData());
            }
        });

        final Element duplicateCommandButton = createButton(resources.duplicateCommand());
        Event.setEventListener(duplicateCommandButton, event -> {
            if (ONCLICK == event.getTypeInt()) {
                event.stopPropagation();
                delegate.onCommandDuplicate(node.getData());
            }
        });

        final Element buttonsPanel = Document.get().createSpanElement();
        buttonsPanel.setClassName(resources.commandsExplorerCss().commandNodeButtonsPanel());
        buttonsPanel.appendChild(removeCommandButton);
        buttonsPanel.appendChild(duplicateCommandButton);

        nodeContainerElement.appendChild(buttonsPanel);
    }

    private void renderCommandGoalNode(Element nodeContainerElement) {
        nodeContainerElement.addClassName(resources.commandsExplorerCss().commandGoalNode());

        final Element addCommandButton = createButton(resources.addCommand());

        Event.setEventListener(addCommandButton, event -> {
            if (ONCLICK == event.getTypeInt()) {
                event.stopPropagation();
                delegate.onCommandAdd(addCommandButton.getAbsoluteLeft(), addCommandButton.getAbsoluteTop());
            }
        });

        nodeContainerElement.appendChild(addCommandButton);
    }

    private Element createButton(SVGResource icon) {
        final Element button = Document.get().createSpanElement();
        button.appendChild(icon.getSvg().getElement());

        Event.sinkEvents(button, ONCLICK);

        return button;
    }
}
