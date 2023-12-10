package mastermind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.util.ArrayList;
import javax.swing.border.LineBorder;

public class ViewGame extends JFrame implements MastermindObserver {
    GameController controller;
    JPanel mainPanel = new JPanel();
    JPanel tentativePanel = new JPanel();
    JPanel avaibleColors = new JPanel();
    private JLabel selectedPion;
    private ImageIcon draggedIcon;
    private boolean isDragging = false;


    private JLabel[] pions;
    private JLabel[] emptyCells;

    public ViewGame(GameController controller)
    {
        super("Mastermind");
        this.controller = controller;
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        mainPanel.setLayout(new GridLayout(8,1));


        this.add(mainPanel, BorderLayout.PAGE_START);//affichage des tentatives passées + indices
        add(tentativePanel, BorderLayout.CENTER);//tentative actuelle
        add(avaibleColors, BorderLayout.PAGE_END);//couleurs dispos
        setVisible(true);
    }

    public void init(int nbTentatives, int nbPionsCombinaison)
    {
        mainPanel.setLayout(new GridLayout(nbTentatives, 2));
        tentativePanel.setLayout(new GridLayout(1, nbPionsCombinaison + 1));
        //pack();
    }



    public void showAvaibleColors(Couleurs[] couleursDispo)
    {

        avaibleColors.setLayout(new GridLayout(1, couleursDispo.length));

        pions = new JLabel[couleursDispo.length];
        for (int i = 0; i < pions.length; i++) {
            pions[i] = new JLabel(new ImageIcon("assets/pions/" + couleursDispo[i] + ".png"));
            //pions[i].setTransferHandler(new TransferHandler("icon"));
            pions[i].addMouseListener(new PionMouseListener());
            pions[i].addMouseMotionListener(new PionsMouseMotionListener());
            pions[i].setPreferredSize(new Dimension(64,64));
            pions[i].putClientProperty("couleur", couleursDispo[i]);
            avaibleColors.add(pions[i]);
        }
    }

    public void startTentative(int nbPionsCombi)
    {
        emptyCells = new JLabel[nbPionsCombi];
        for (int i = 0; i < nbPionsCombi; i++) {
            emptyCells[i] = new JLabel();
            emptyCells[i].setPreferredSize(new Dimension(50, 50));
            emptyCells[i].setBorder(new LineBorder(Color.BLACK));
            //emptyCells[i].setTransferHandler(new TransferHandler("icon"));
            emptyCells[i].addMouseListener(new EmptyCellMouseListener());
            tentativePanel.add(emptyCells[i]);
        }

        JButton validerButton = new JButton("Valider");
        validerButton.addActionListener( actionEvent  -> {
            Couleurs[] tentative = new Couleurs[nbPionsCombi];

            JPanel archiveTentative = new JPanel();
            archiveTentative.setLayout(new GridLayout(1, nbPionsCombi));

            int i = 0;
            for (JLabel j: emptyCells)
            {
                ImageIcon originalIcon = (ImageIcon) j.getIcon();
                JLabel duplicatedLabel = new JLabel(originalIcon);
                archiveTentative.add(duplicatedLabel);
                tentative[i] = ((Couleurs)j.getClientProperty("couleur"));

                j.putClientProperty("couleur", null);
                j.setIcon(null);
                i++;
            }
            mainPanel.add(archiveTentative);

            //Le bouton permet d'appeller la fonction de verification dans le game controller
            try {
                controller.validerTentative(tentative);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        tentativePanel.add(validerButton);
    }


    public void updateIndice(Indice[] indices)
    {
        JPanel indicesPanel = new JPanel();
        for(int i = 0; i < 4; i++)
        {
            JLabel j = new JLabel();
            j.setOpaque(true);
            switch (indices[i])
            {
                case NOIR -> j.setBackground(Color.BLACK);
                case BLANC -> j.setBackground(Color.GRAY);
                case VIDE -> j.setBackground(Color.WHITE);
            }
            indicesPanel.add(j);
        }
        mainPanel.add(indicesPanel);
    }

    class PionMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println("mouse pressed");
            selectedPion = (JLabel)e.getSource();
            draggedIcon = (ImageIcon) selectedPion.getIcon();
        }
    }

    class EmptyCellMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            JLabel emptyCell = (JLabel) e.getSource();

            System.out.println(selectedPion == emptyCell);
            if (selectedPion != null) {
                ImageIcon icon = (ImageIcon) selectedPion.getIcon();
                emptyCell.setIcon(icon);
                emptyCell.putClientProperty("couleur", selectedPion.getClientProperty("couleur"));
                draggedIcon = null;
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    class PionsMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (draggedIcon != null) {
                setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                        draggedIcon.getImage(), new Point(0, 0), "Custom Cursor"));
            }
        }
    }
}
