package battle;

import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Random;

import personnage.Personnage;

public class BattleModel extends Observable {

	private Personnage perso;
	private Enemy enemy;
	private String text = new String();
	private int pdvPersoMax;
	private int utilisePotion = 0;
	private int utilisePoison = 0;

	/**
	 * @param perso
	 *            utilisation du personnage cr��e au pr�alable
	 * @param enemy
	 *            utilisation de l'ennemi cr�ee al�atoirement
	 */
	public BattleModel(Personnage perso, Enemy enemy) {
		this.perso = perso;
		this.enemy = enemy;
		this.pdvPersoMax = perso.getPointDeVie();
	}

	public void setVal(String text) { // mode PULL
		this.text = text;
		setChanged();
		notifyObservers();
	}

	public String getImagePerso() {
		return perso.getClassPerso().getIcoClasse();
	}

	public String getImageEnnemi() {
		return enemy.getImgEn();
	}

	/**
	 * m�thode qui va lancer le combat et activer l'Observable
	 */
	void lancerCombat() { // mode PULL == incrementValue()
		setChanged();
		notifyObservers();
	}

	public String getVal() {
		return this.text;
	}

	public Personnage getPerso() {
		return perso;
	}

	public void setPerso(Personnage perso) {
		this.perso = perso;
	}

	public Enemy getEnemy() {
		return enemy;
	}

	public void setEnemy(Enemy enemy) {
		this.enemy = enemy;
	}

	/**
	 * Combat strat�gique avec les diff�rents cas, qui vont venir s'instancier
	 * au cours du combat pour r�partir les elixirs ou poisons.
	 * 
	 * @param pdvMinPerso
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	void Combat(int pdvMinPerso) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		while (this.combatFinish(enemy, perso)) {
			int PourcentPerso = (int) ((this.getPerso().getPointDeVie() * 100.0f) / pdvPersoMax);

			if (PourcentPerso < pdvMinPerso
					&& perso.getItem().getPotion().getTypeItem() == "Potion"
					&& utilisePotion < 2
					&& !perso.getItem().getPotion().getNameItem()
							.contains("auto")) { // a definir par l'utilisateur
													// plus tard
				int soin = (int) perso
						.getItem()
						.getPotion()
						.getMethodForName("soigne")
						.invoke(perso.getItem().getPotion().getClassInstancie());
				perso.setPointDeVie(perso.getPointDeVie() + soin);
				utilisePotion++;
				text = "\n Le personnage utilise une potion et gagne : " + soin
						+ " il a donc : " + perso.getPointDeVie() + " pv";
				setChanged();
				notifyObservers();
			} else if (perso.getItem().getPotion().getNameItem()
					.contains("auto")
					&& perso.getItem().getPotion().getTypeItem() == "Potion"
					&& utilisePotion < 15) {
				int soin = (int) perso
						.getItem()
						.getPotion()
						.getMethodForName("soigne")
						.invoke(perso.getItem().getPotion().getClassInstancie());
				perso.setPointDeVie(perso.getPointDeVie() + soin);
				utilisePotion++;
				text = "\n Le personnage utilise une potion auto et gagne : "
						+ soin + " il a donc : " + perso.getPointDeVie()
						+ " pv";
				setChanged();
				notifyObservers();
			} else if (PourcentPerso < pdvMinPerso
					&& perso.getItem().getPotion().getTypeItem() == "Poison"
					&& utilisePoison < 2
					&& !perso.getItem().getPotion().getNameItem()
							.contains("auto")) {
				int degat = (int) perso
						.getItem()
						.getPotion()
						.getMethodForName("enlevePv")
						.invoke(perso.getItem().getPotion().getClassInstancie());
				enemy.setPdv(enemy.getPdv() + degat);
				utilisePoison++;
				text = "\n Le personnage utilise poison enl�ve : " + degat
						+ " il reste � l'ennemi : " + enemy.getPdv() + " pv";
				setChanged();
				notifyObservers();

			} else if (perso.getItem().getPotion().getNameItem()
					.contains("auto")
					&& perso.getItem().getPotion().getTypeItem() == "Poison"
					&& utilisePoison < 15) {
				int degat = (int) perso
						.getItem()
						.getPotion()
						.getMethodForName("enlevePv")
						.invoke(perso.getItem().getPotion().getClassInstancie());
				enemy.setPdv(enemy.getPdv() + degat);
				utilisePoison++;
				text = "\n Le personnage utilise poison auto enl�ve : " + degat
						+ " il reste � l'ennemi : " + enemy.getPdv() + " pv";
				setChanged();
				notifyObservers();
			}
			if (!enemy.isReculer()
					|| perso.getClassPerso().getNameClasse()
							.equalsIgnoreCase("Archer")) {
				enemy.setPdv(enemy.getPdv() - perso.getForceDeFrappe());
				int i = generateRand(0, 1);
				if (i == 1) {
					enemy.setReculer(true);
				} else {
					enemy.setReculer(false);
				}
			} else {
				enemy.setPdv(enemy.getPdv()
						- (int) (perso.getForceDeFrappe() * 0.5));
				text = "\n l'ennemi est loin, vous infligez 50% de d�gat en moins";
				int i = generateRand(0, 1);
				if (i == 1) {
					enemy.setReculer(true);
				} else {
					enemy.setReculer(false);
				}
			}
			perso.setPointDeVie(perso.getPointDeVie()
					- pdvEnleverPerso(enemy.getAttaque()));
		}
	}

	/**
	 * Cette m�thode d�finit si le combat se termine et d�termine le gagnant
	 * 
	 * @param enemy
	 *            oppos� dans le combat
	 * @param perso
	 *            personnage lanc� dans le combat
	 * @return un bool�en pour connaitre la fin d'un combat
	 */
	private boolean combatFinish(Enemy enemy, Personnage perso) {

		if (enemy.getPdv() > 0 && perso.getPointDeVie() > 0) {

			text += "\n Il reste � l'ennemi " + enemy.getPdv()
					+ " et au perso " + perso.getPointDeVie();
			setChanged();
			notifyObservers();
			return true;
		} else if (enemy.getPdv() <= 0 && perso.getPointDeVie() > 0) {
			text = "\n Il reste � l'ennemi " + 0 + " et au perso "
					+ perso.getPointDeVie();
			text += "\n Le perso " + perso.getNom() + " a gagn�";
			setChanged();
			notifyObservers();
			return false;
		} else if (enemy.getPdv() > 0 && perso.getPointDeVie() <= 0) {
			text = "\n Il reste a l'ennemi " + enemy.getPdv() + " et au perso "
					+ 0;
			text += "\n L'ennemi " + enemy.getNom() + " a gagn�";
			setChanged();
			notifyObservers();
			return false;
		} else if (enemy.getPdv() == 0 && perso.getPointDeVie() == 0) {
			text = "Match nul, les 2 joueurs sont KO !";
			setChanged();
			notifyObservers();
			return false;
		} else if (enemy.getPdv() <= 0 && perso.getPointDeVie() <= 0) {
			if (enemy.getPdv() < perso.getPointDeVie()) {
				text += "\n Le perso " + perso.getNom() + " a gagn�";
			} else {
				text += "\n L'ennemi " + enemy.getNom() + " a gagn�";
			}
			setChanged();
			notifyObservers();
			return false;
		}
		System.out.println("Erreur exception");
		return false;
	}

	/**
	 * M�thode qui va modifier la valeur de l'attaque d'un ennemi si la valeur
	 * de d�f d'un personnage est sup�rieur � la valeur d'attaque d'un ennemi.
	 * 
	 * @param attaque
	 * @return la nouvelle valeur
	 */
	private int pdvEnleverPerso(int attaque) {
		if (perso.getDefense() > attaque) {
			text += "\n L'ennemi " + enemy.getNom()
					+ " n'a pas assez de force pour vous battre";
			text += "\n L'ennemi "
					+ enemy.getNom()
					+ " prend une SuperAttaque qui lui fait gagner (+1) en attaque";
			enemy.setAttaque(enemy.getAttaque() + 1);
			return 0;
		} else {
			return attaque - perso.getDefense();
		}
	}

	String getText() {
		return text;
	}

	public void setText(String t) {
		text = t;
	}

	/**
	 * G�n�rateur de valeur al�atoire
	 * 
	 * @param min
	 *            valeur minimum
	 * @param max
	 *            valeur maximum
	 * @return valeur al�atoire entre min et max
	 */
	private int generateRand(int min, int max) {
		Random rand = new Random();
		int nombreAleatoire = rand.nextInt(max - min + 1) + min;
		return nombreAleatoire;
	}

}
