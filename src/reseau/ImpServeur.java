package reseau;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class ImpServeur extends UnicastRemoteObject implements InterfaceServeur
{
	
	private static final long serialVersionUID = 1L;
	
	private Table table;
	private boolean partieCommencer;
	private int nbTour;
	

	protected ImpServeur(int petiteBlinde) throws RemoteException 
	{
		table = new Table(petiteBlinde);
		partieCommencer = false;
		nbTour = 1;
	}
	
	/**
     * envoiInformationsJoueur appel�e apr�s connexion du joueur avec le serveur
     * @param une liste d'objet de taille 3 : pseudo, solde, ip, interfaceClient
     * @return UUID du joueur, ex : return (long)(Math.random()*1000000);
     * @throws RemoteException
     */
	
	public long envoiInformationsJoueur(Object[] obj)
	{
		System.out.println("R�cuperation pseudo.");
		String pseudo = (String)obj[0];
		System.out.println("R�cuperation solde.");
		int solde = (Integer)obj[1];
		System.out.println("R�cuperation adresse.");
		InetAddress ip = (InetAddress)obj[2];
		System.out.println("R�cuperation interface.");
		InterfaceClient interfaceJoueur = (InterfaceClient)obj[3];
		
		System.out.println("R�cuperation r�ussit !!!");
		
		int lower = 0;
		int higher = 1000;

		int UID = (int)(Math.random() * (higher-lower)) + lower;
		
		System.out.println("UUID initialis�.");
		
		switch (table.getNbJoueur())
		{
			/** Aucun joueur dans la partie **/
			case(0):
			{
				table.setJoueur(new Joueur(UID,pseudo,"attente",true,0,solde,ip,interfaceJoueur));
				System.out.println("Un nouveau joueur a �t� ajouter dans la partie. C'est le dealer.\nSont UID est "+UID);
			}
			break;
			/** Aucune place de libre **/
			case(10):
			{
				table.setAttente(new Joueur(UID,pseudo,"spectateur",false,0,solde,ip,interfaceJoueur));
				System.out.println("Un nouveau joueur a �t� ajouter dans la file d'attente.\nSont UID est "+UID);
			}
			break;
			/** Place disponible **/
			default:
			{
				if (partieCommencer) 
				{
					table.setAttente(new Joueur(UID,pseudo,"spectateur",false,0,solde,ip,interfaceJoueur));
					System.out.println("Un nouveau joueur a �t� ajouter dans la file d'attente.\nSont UID est "+UID);
				}
				else 
				{
					table.setJoueur(new Joueur(UID,pseudo,"attente",false,0,solde,ip,interfaceJoueur));
					System.out.println("Un nouveau joueur a �t� ajouter a la partie.\nSont UID est "+UID);
				}
			}
		}
		
		if (table.getNbJoueur()>1) this.startGame();
		this.transmettreAction();
		
		return UID;
	}
	
    
    
    
	
    /*
     * relancer appel�e par les joueurs se tenant apr�s la grosse blinde
     * @param UUID du joueur
     * @param somme � miser
     * @return le solde actuel du joueur (convertis en jeton chez le client)
     * @throws RemoteException
     */
    
    public void relancer(long uuid, long somme)
	{
		Joueur joueur = table.getJoueur(uuid);
		
		if (joueur.getSolde()-somme>=0)
		{
			joueur.setSolde(joueur.getSolde()-(int)somme);
			joueur.setDerniereMise((int)somme);
			table.setPot(table.getPot()+(int)somme);
			joueur.setStatut("attente");
		}
		else
		{
			if (joueur.getSolde()>0)
			{
				table.setPot(table.getPot()+joueur.getSolde());
				joueur.setSolde(0);
				joueur.setStatut("attente");
			}
			else
			{
				joueur.setStatut("coucher");
			}
		}
		
		if (joueur.isDealer()) this.nouveauTour();
		this.chercheJoueurSuivant(joueur,"jouer");
		this.transmettreAction();
	}
	
    /*
     * suivre appel�e par les joueurs se tenant apr�s la grosse blinde
     * @param UUID du joueur
     * @return le solde actuel du joueur (convertis en jeton chez le client)
     * @throws RemoteException
     */
    public void suivre(long uuid)
	{
		Joueur joueur = table.getJoueur(uuid);
		int derniereMise = table.getDerniereMise();
		
		if (joueur.getSolde()-table.getDerniereMise()>=0)
		{
			joueur.setSolde(joueur.getSolde()-derniereMise);
			joueur.setDerniereMise(derniereMise);
			table.setPot(table.getPot()+derniereMise);
			joueur.setStatut("attente");
		}
		else
		{
			if (joueur.getSolde()>0)
			{
				table.setPot(table.getPot()+joueur.getSolde());
				joueur.setSolde(0);
				joueur.setStatut("attente");
			}
			else
			{
				joueur.setStatut("coucher");
			}
		}
		if (joueur.isDealer()) this.nouveauTour();
		
		if (table.getNbJoueur()>=2) 
		{
			this.chercheJoueurSuivant(joueur,"jouer");
			
		}
		else
		{
			this.partieCommencer = false;
		}
		
		this.transmettreAction();
	}
	
    
    
    /**
     * seCoucher appel�e si un joueur d�cide de se coucher
     * @param UUID du joueur
     * @return true si cela a �t� valid�e, false si erreur
     * @throws RemoteException
     */
    
    public void seCoucher(long uuid)
	{
		Joueur joueur = table.getJoueur(uuid);
		joueur.setStatut("couche");
		
		if (joueur.isDealer()) this.nouveauTour();
		this.chercheJoueurSuivant(joueur,"jouer");
		
		this.transmettreAction();
	}
    
    /**
     * faireTapis appel�e si un joueur a un bon jeu et veut tour miser
     * @param UUID du joueur
     * @param somme � miser
     * @return void
     * @throws RemoteException
     */
    
    public void faireTapis(long uuid)
    {
    	Joueur joueur = table.getJoueur(uuid);
    	
    	if (joueur.getSolde()>table.getDerniereMise())
    	{
    		table.setDerniereMise(joueur.getSolde());
    		joueur.setSolde(0);
    		joueur.setStatut("attente");	
    	}
    	else 
    	if (joueur.getSolde()>0)
    	{
    		table.setPot(table.getPot()+joueur.getSolde());
			joueur.setSolde(0);
			joueur.setStatut("attente");
    	}
    	
    	if (joueur.isDealer()) this.nouveauTour();
		this.chercheJoueurSuivant(joueur,"jouer");
		this.transmettreAction();
    }
	
 
	/**
	 * Permet au dealer de lancer la partie
	 * @param UID
	 */
	
	private void startGame()
	{
		partieCommencer = true;
		nbTour = 1;
			
		Joueur[] listJoueur = table.getListJoueur();
		Joueur joueur = listJoueur[0];
		joueur.setStatut("petiteBlinde");
	}
	
	 /**
     * recupererMesPetitesBlind appel�e pour demander au dealer de r�cup�rer
     * les petites blindes et d�cr�menter son solde
     * @param UUID du joueur
     * @return le solde actuel du joueur (convertis en jeton chez le client)
     * @throws RemoteException
     */
	
    public void recupererMesPetitesBlind(long uuid)
	{
		Joueur joueur = table.getJoueur(uuid);
		int petiteBlinde = table.getPetiteBlinde();
		
		if (joueur.getSolde()-petiteBlinde>=0)
		{
			table.setPot(table.getPot()+petiteBlinde);
			joueur.setSolde(joueur.getSolde()-petiteBlinde);
			joueur.setStatut("attente");
			
			this.chercheJoueurSuivant(joueur,"petiteBlinde");
		}
	}
	
	/**
     * recupererMesGrossesBlind appel�e pour demander au dealer de r�cup�rer
     * les grosses blindes et d�cr�menter son solde
     * @param UUID du joueur
     * @return le solde actuel du joueur (convertis en jeton chez le client)
     * @throws RemoteException
     */
    
    public void recupererMesGrossesBlind(long uuid)
	{
		Joueur joueur = table.getJoueur(uuid);
		int grosseBlinde = table.getPetiteBlinde()*2;
		
		if (joueur.getSolde()-grosseBlinde>=0)
		{
			table.setPot(table.getPot()+grosseBlinde);
			joueur.setSolde(joueur.getSolde()-grosseBlinde);
			joueur.setStatut("attente");
			
			if (joueur.isDealer()) this.nouveauTour();
			this.chercheJoueurSuivant(joueur,"jouer");
		}
		else
		{
			table.transfertJoueur(uuid);
		}
		
		if (nbTour==1) this.donnerCartes();
	}
	
   
    /**
     * Transmet les cartes de jeu aux joueurs
     * @return void
     */
    
	private void donnerCartes() 
	{
		Joueur[] listJoueur = table.getListJoueur();
		
		for(Joueur j : listJoueur)
		{
			if (j.isPresent())
			{
				InterfaceClient interC = j.getInterfaceClient();
				String carte1 = table.getNewCarte();
				String carte2 = table.getNewCarte();
				j.setCarte(carte1, carte2);	
				try 
				{
					interC.donnerCarte(j.getCarte());
				} catch (RemoteException e) {e.printStackTrace();}
			}
		}
	}

	/**
	 * Permet de tirer le flop
	 */
	
	private void setFlop()
	{
		String carte1 = table.getNewCarte();
		String carte2 = table.getNewCarte();
		String carte3 = table.getNewCarte();
		
		table.setListCarteTable(carte1);
		table.setListCarteTable(carte2);
		table.setListCarteTable(carte3);
	}
	
	/**
	 * Permet de tirer le turn
	 */
	
	private void setTurn()
	{
		table.setListCarteTable(table.getNewCarte());
	}
	
	/**
	 * Permet de tirer le revers
	 */
	
	private void setRevers()
	{
		table.setListCarteTable(table.getNewCarte());
	}
	
	
	/**
	 * Permet de trouver quel est le prochain joueur � jouer
	 * @param joueur
	 */
	
	private void chercheJoueurSuivant(Joueur joueur,String statut)
	{
		boolean trouver = false;
		while(trouver!=true)
		{
			joueur = table.getJoueurSuivant(joueur.getUID());
				
			if (joueur.getStatut()=="attente") 
			{
				joueur.setStatut(statut);
				trouver = true;
			}
				
			if (joueur.isDealer() && joueur.getStatut() == "coucher")
			{
				
				if(this.nouveauTour())
				{
					joueur = table.getJoueurSuivant(joueur.getUID());
					joueur.setStatut(statut);
				}
				
				trouver = true;
			}
		}
	}
	/**
     * demanderListeJoueur sera appel�e � chaque changement effectu� sur la
     * liste des joueurs.
     * @param UUID du joueur
     * @return une liste de tableaux d'object de 6 colonnes :
     *                  pseudo          (String)
     *                  solde           (long)
     *                  mise            (long)
     *                  statut          (String)
     * (* Normalement le statut indique s'il peut jouer ou pas *)
     *                  num�ro          (int)   
	 * 
     * @throws RemoteException
     */
    
    private List<Object[]> demanderListeJoueur()
    {
    	List<Object[]> lParticipant = new LinkedList<Object[]>();
    	
    	Joueur[] listJoueur = table.getListJoueur();
    	Joueur[] listAttente = table.getListJoueur();
    	
    	for(Joueur j : listJoueur)
    	{
    		if(j.isPresent())
    		{
    			Object[] joueur = new Object[7];
    		
    			joueur[0] = j.getPseudo();
    			joueur[1] = j.getSolde();
    			joueur[2] = j.getDerniereMise();
    			joueur[3] = j.getStatut();
    			joueur[4] = j.getPositionTable();
    		}
    	}
    	
    	for(Joueur j : listAttente)
    	{
    		if(j.isPresent())
    		{
    			Object[] joueur = new Object[7];
    		
    			joueur[0] = j.getPseudo();
    			joueur[1] = j.getSolde();
    			joueur[2] = j.getDerniereMise();
    			joueur[3] = j.getStatut();
    			joueur[4] = j.getPositionTable();
    		}
    	}
    	
    	return lParticipant;
    }
    
	/**
	 * Permet de transmettre une action faite d'un joueur
	 * � toutes les personnes.
	 */
	
	private void transmettreAction()
	{
		Joueur[] listJoueur = table.getListJoueur();
		Joueur[] listAttente = table.getListAttente();
		
		
		List<Object[]> actionATransmettre = demanderListeJoueur();
		
		try 
		{
			for (Joueur j : listJoueur)
			{
				if(j.isPresent())
				{
					j.getInterfaceClient().miseAJourTable(actionATransmettre, table.getPot());
				}
			}
			
			for (Joueur j : listAttente)
			{
				if(j!=null )
				{
					if (j.isPresent()) j.getInterfaceClient().miseAJourTable(actionATransmettre, table.getPot());
				}
			}
		} catch (RemoteException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Fait passer au tour suivant
	 */
	
	private boolean nouveauTour()
	{
		boolean resultat = true;
		switch (nbTour)
		{
			case(1):
			{
				setFlop();
			}
			break;
			case(2):
			{
				setTurn();
			}
			break;
			case(3):
			{
				setRevers();
			}
			break;
			case(4):
			{
				resultat = false;
				System.out.println("Fin de partie");
			}
			break;
		}
		nbTour++;
		return resultat;
	}
	
}

