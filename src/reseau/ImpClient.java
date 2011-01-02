package reseau;

import graphique.carte.Carte;
import graphique.carte.JCarte;
import graphique.table.JTable;

import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ImpClient implements InterfaceClient, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImpClient(){
		
		
	}
	
	/*
     * recupererSesGains apr�s fin de partie
     * @param 
     * - un tableau d'objet de 5 colonnes:
     * 
     *        pseudo	(string)
     *        uuid		(long)
     *        carte1	(string)
     *        carte2	(string)
     *        gain		(long)
     * @return void
     * @throws RemoteException
     */

	public void setResultat(Object[] gagnant)
	{
		
	}

	/**
     * Met � jour les informations du joueur
     * @param  
     * 
     * - une liste de tableaux d'object de 7colonnes :
     *                  cartesDuJoueur  (String) // Intialement � "Masqu�es" et
     *                                              apr�s Abattage
     *                                              "valeur_couleur-valeur_couleur".
     *                  adresseIP		(String)
     *                  uuid			(long)
     *                  pseudo          (String)
     *                  solde           (long)
     *                  mise            (long)
     *                  statut          (String)
     *                  
     *    			(*  attente (* Attent de pouvoir jouer *)
	 *		 			spectateur (* mode spectateur *)
     *		 			couche (* s'est couch� *)
	 *		 			 petiteBlinde (* c'est lui la petite blinde *)
	 *		  			grosseBlinde (* c'est lui la grosse blinde *)
	 *		 			 jouer (* c'est � lui de jouer *)
	 *     			*)
     *                 
     *                  position          (int)   
     *                  
     * - la valeur du pot
     *                
	 * @return void
     * @throws RemoteException
     */
	public void miseAJourTable(List<Object[]> listParticipant, long pot)
	{
		for(Object[] obj : listParticipant){
			String carte = (String) obj[0];
			//r�cup val 1 c'est la carte 2
			//2 c'est 3
			StringTokenizer st = new StringTokenizer(carte, "-");
			List<String> cartes = new ArrayList<String>();
			
			while ( st.hasMoreTokens() ) {
			    cartes.add(st.nextToken());
			}
			// la liste cartes contient deux string val1_coul1 et val2_coul2
			
			List<String> splitCartes = new ArrayList<String>();
			for(String cart : cartes){
				StringTokenizer stt = new StringTokenizer(cart, "_");
				while ( stt.hasMoreTokens() ) {
					splitCartes.add(stt.nextToken());
				}
			}
			// On a une liste de string avec les valeurs dans l'ordre
			
			List<Integer> convertToInt = new ArrayList<Integer>();
			for(String spli : splitCartes){
				convertToInt.add(Integer.parseInt(spli));
			}
			
			Carte carte1 = new Carte(convertToInt.get(1), convertToInt.get(0));
			Carte carte2 = new Carte(convertToInt.get(3), convertToInt.get(2));
			
			JTable jtable = Global.getJTable();
			
			int position = (Integer) obj[6];
			jtable.ajoutCartesJoueur(position, carte1, carte2);
			//ajouter les autres mise � jour
			
			int solde = (Integer) obj[4];
			
			
			int mise  = (Integer) obj[5];
			jtable.miser(position, mise);
			
			String statut = (String) obj[6];
			
			
			int le_pot = (int) pot;
			jtable.ajouterPot(le_pot);
			
			
		}
	}

	 /**
     * voirCartesATable sera appel�e apr�s un tour de mise
     * @param 
     * 
     * - liste d'objet 
     * 3 cartes pour le flop, 4(3+1) pour le turn et 5(4+1) pour le river
     * pour tout autre appel, �a restera 5.
     * 
     * @return void
     * @throws RemoteException
     */
	public void voirCartesATable(Object[] listCarte)
	{
		int taille = listCarte.length;
		
		for(int i=0; i<taille;i++){
			Global.getJTable().ajoutCarte((Carte) listCarte[i], i);
		}
	}

}
