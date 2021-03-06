package graphique.pseudo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jeux.Poker;

@SuppressWarnings("serial")
/**
 * Fenetre qui demande le pseudo
 *@author Fabien
 *@version 0.1
 **/
public class JPseudo extends JFrame implements ActionListener {
	// Label informant l'utilisateur
	private JLabel info = new JLabel("Quel pseudo choisissez vous ?");
	
	// Label o� l'utilisateur rentre son pseudo
	private String texte = "Entrez votre pseudo ici.";
	private JTextField pseudo = new JTextField(texte);
	
	// Bouton de validation du pseudo
	private JButton btn_valider = new JButton("Valider");
	
	// Classe appelante
	private Poker poker;
	
	/**
	 * Affichage de la fen�tre et v�rification du pseudo
	 **/
	public JPseudo (Poker poker) {
		this.poker = poker;
		this.setTitle("Choix du pseudo");
		this.setResizable(false);
		this.setBounds(100, 100, 400, 150);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		this.setLayout(layout);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pseudo.selectAll();
		
		// Ajout des composants de la fen�tre
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.insets = new Insets(10, 10, 10, 10);
		layout.addLayoutComponent(info, constraints);
		constraints.gridx = 3;
		layout.addLayoutComponent(pseudo, constraints);
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		layout.addLayoutComponent(btn_valider, constraints);
		this.add(info);
		this.add(pseudo);
		this.add(btn_valider);
		btn_valider.addActionListener(this);
	}
	
	// Affichage de la fen�tre
	public void setVisible () {
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String pseudo = this.pseudo.getText();
		System.out.println(pseudo);
		if ((!pseudo.equalsIgnoreCase(texte))&&(!pseudo.equalsIgnoreCase(""))) {
			this.setVisible(false);
			poker.lancementPartie(pseudo);
		}
	}
}
