package vue.groupPerso;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import personnage.Personnage;
import util.PathManager;
import vue.groupItems.GroupArmes;
import vue.groupItems.GroupArmures;
import vue.groupItems.GroupClasses;

public class GroupCaracteristiquesPerso implements Observer {

	private Group thisGroup;

	private Label lClasse;
	private Label lArmure;
	private Label lArme;
	private GroupArmes gArmes;
	private GroupArmures gArmures;
	private GroupClasses gClasses;

	private Label force;

	private Label pdv;

	private Label def;

	public GroupCaracteristiquesPerso(Shell fenetre, GridData gridData,
			Personnage perso, GroupClasses gClasses, GroupArmures gArmures,
			GroupArmes gArmes) {

		this.gClasses = gClasses;
		this.gArmes = gArmes;
		this.gArmures = gArmures;

		thisGroup = new Group(fenetre, SWT.FLAT);
		thisGroup.setLayoutData(gridData);
		thisGroup.setText("Caractéristiques du personnage");
		thisGroup.setLayout(new GridLayout());
		thisGroup.setBackgroundImage(new Image(fenetre.getDisplay(),
				PathManager.bgGroup));

		force = new Label(thisGroup, SWT.CENTER);
		force.setText("Force :       ");
		pdv = new Label(thisGroup, SWT.CENTER);
		pdv.setText("Points de vie :      ");
		def = new Label(thisGroup, SWT.CENTER);
		def.setText("Defense :      ");

		lClasse = new Label(thisGroup, SWT.CENTER);
		lClasse.setText("Classe : ");

		lArme = new Label(thisGroup, SWT.CENTER);
		lArme.setText("Arme : ");

		lArmure = new Label(thisGroup, SWT.CENTER);
		lArmure.setText("Armure : ");
	}

	@Override
	public void update(Observable o, Object arg) {

		// maj des labels
		lClasse.setText("Classe : " + gClasses.getValSelection());
		lArme.setText("Arme : " + gArmes.getValSelection());
		lArmure.setText("Armure : " + gArmures.getValSelection());

		int[] carac = (int[]) arg;
		int pv = carac[0];
		int d = carac[1];
		int f = carac[2];

		pdv.setText(Integer.toString(pv));
		def.setText(Integer.toString(d));
		force.setText(Integer.toString(f));

		// rafraichissement des labels
		lClasse.pack();
		lArme.pack();
		lArmure.pack();
	}
}
