/*
 *  Copyright (C) 2010-2021 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author JPEXS
 * @param <E> Element to search
 */
public class SearchResultsDialog<E> extends AppDialog {

    private final JList<E> resultsList;

    private final DefaultListModel<E> model;
    private final boolean regExp;

    private final List<SearchListener<E>> listeners;

    private final JButton gotoButton = new JButton(translate("button.goto"));

    private final JButton closeButton = new JButton(translate("button.close"));

    private String text;
    private final boolean ignoreCase;

    public SearchResultsDialog(Window owner, String text, boolean ignoreCase, boolean regExp, List<SearchListener<E>> listeners) {
        super(owner);
        setTitle(translate("dialog.title").replace("%text%", text));
        this.text = text;
        Container cnt = getContentPane();
        model = new DefaultListModel<>();
        resultsList = new JList<>(model);
        this.regExp = regExp;
        this.listeners = listeners;

        gotoButton.addActionListener(this::gotoButtonActionPerformed);
        closeButton.addActionListener(this::closeButtonActionPerformed);

        JPanel paramsPanel = new JPanel();
        paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
        JLabel ignoreCaseLabel = new JLabel(AppDialog.translateForDialog("checkbox.ignorecase", SearchDialog.class) + ": " + (ignoreCase ? AppStrings.translate("yes") : AppStrings.translate("no")));
        JLabel regExpLabel = new JLabel(AppDialog.translateForDialog("checkbox.regexp", SearchDialog.class) + ": " + (regExp ? AppStrings.translate("yes") : AppStrings.translate("no")));
        paramsPanel.add(ignoreCaseLabel);
        paramsPanel.add(regExpLabel);


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(gotoButton);
        buttonsPanel.add(closeButton);
        resultsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gotoElement();
                }
            }
        });

        resultsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    gotoElement();
                }
            }
        });

        cnt.setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(resultsList);
        sp.setPreferredSize(new Dimension(300, 300));
        cnt.add(sp, BorderLayout.CENTER);
        cnt.add(buttonsPanel, BorderLayout.SOUTH);
        cnt.add(paramsPanel, BorderLayout.NORTH);
        pack();
        View.centerScreen(this);
        View.setWindowIcon(this);
        this.ignoreCase = ignoreCase;
    }

    public void setResults(List<E> results) {
        model.clear();
        for (E e : results) {
            model.addElement(e);
        }
    }

    private void gotoButtonActionPerformed(ActionEvent evt) {
        gotoElement();
    }

    private void closeButtonActionPerformed(ActionEvent evt) {
        setVisible(false);
    }

    private void gotoElement() {
        if (resultsList.getSelectedIndex() != -1) {
            for (SearchListener<E> listener : listeners) {
                listener.updateSearchPos(text, ignoreCase, regExp, resultsList.getSelectedValue());
            }
        }
    }
}
