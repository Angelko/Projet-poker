package reseau;

import java.rmi.RemoteException;
import java.util.List;

public class ImpClient implements InterfaceClient {

	Table table;
	Joueur joueur;
	
	public ImpClient(Table tble, Joueur pj){
		table = tble;
		joueur = pj;
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


	public void miseAJourTable(List<Object[]> listParticipant, long pot)
	{
	
	}

	public void voirCartesATable(Object[] listCarte)
	{

	}

}
