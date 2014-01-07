package vue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import objet.Item;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import personnage.Personnage;
import util.PathManager;
import vue.groupItems.GroupArmes;
import vue.groupItems.GroupArmures;
import vue.groupItems.GroupClasses;
import vue.groupItems.GroupPotions;
import vue.groupPerso.GroupApercuPerso;
import vue.groupPerso.GroupCaracteristiquesPerso;
import chargementDynamique.ChargementDynamique;
import chargementDynamique.ListenerChargementDyn;
import chargementDynamique.WatchDir;

public class InterfaceRPG implements Observer {

	private static Display display = new Display();;
	private ImageData cursor_Image = new ImageData(PathManager.cursorImg);
	private List listeClasses;
	private LinkedList<ChargementDynamique> classes;
	private LinkedList<ChargementDynamique> items;
	private List listeItems;

	public int getSizeListeClasses() {
		return sizeListeClasses;
	}

	public void setSizeListeClasses(int sizeListeClasses) {
		this.sizeListeClasses = sizeListeClasses;
	}

	protected int sizeListeClasses;
	private GroupClasses gClasses;
	private GroupArmes gArmes;
	private GroupArmures gArmures;
	private GroupPotions gPotions;
	private ListenerChargementDyn listenerCD;
	private Thread threadCD;
	private GroupCaracteristiquesPerso gCarac;
	private GroupApercuPerso gAppercu;
	private Personnage perso;

	public InterfaceRPG() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException {

		Shell fenetre = createFrame();

		// cr�ation du perso de base
		perso = Personnage.getInstance();

		GridData gridData = getGridData();

		Path dir = Paths.get("./Plugin");
		WatchDir watchDirectories = new WatchDir(dir, false);
		listenerCD = ListenerChargementDyn.getInstance();

		threadCD = new Thread(watchDirectories);
		threadCD.start();

		items = listenerCD.getPluginItem();
		classes = listenerCD.getPluginClasse();

		// r�cup�ration de la taille de la liste
		setSizeListeClasses(classes.size());

		// **** cr�ation des groupes ****

		// 1 > liste des classes
		gClasses = new GroupClasses(fenetre, classes, gridData);
		listenerCD.addObserver(gClasses);

		// 2 > saisie nom du perso + apper�u perso
		gAppercu = new GroupApercuPerso(fenetre, gridData);

		// 4 > armes
		gArmes = new GroupArmes(fenetre, items, gridData);

		// 5 > armures
		gArmures = new GroupArmures(fenetre, items, gridData);

		// 6 > potions
		gPotions = new GroupPotions(fenetre, items, gridData);

		// 3 > caract�ristiques
		gCarac = new GroupCaracteristiquesPerso(fenetre, gridData, perso,
				gClasses, gArmures, gArmes);

		gClasses.addObserver(gCarac);
		gArmes.addObserver(gCarac);
		gArmures.addObserver(gCarac);

		// FIN GROUPES

		// curseur
		Cursor cursor1 = new Cursor(display, cursor_Image, 1, 1);
		fenetre.setCursor(cursor1);

		// bg fenetre
		Image bg_Image = new Image(display, PathManager.bgImg);
		fenetre.setBackgroundImage(bg_Image);

		// bouton lancer jeu
		Button button = new Button(fenetre, SWT.NONE);
		button.setText("Lancer");
		button.addListener(SWT.Selection, getListener());

		centrerSurEcran(display, fenetre);

		fenetre.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				// stopper le thread
			}
		});

		fenetre.open();

		while (!fenetre.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}

	public GroupClasses getgClasses() {
		return gClasses;
	}

	public void setgClasses(GroupClasses gClasses) {
		this.gClasses = gClasses;
	}

	public GroupArmes getgArmes() {
		return gArmes;
	}

	public void setgArmes(GroupArmes gArmes) {
		this.gArmes = gArmes;
	}

	public GroupArmures getgArmures() {
		return gArmures;
	}

	public void setgArmures(GroupArmures gArmures) {
		this.gArmures = gArmures;
	}

	public GroupPotions getgPotions() {
		return gPotions;
	}

	public void setgPotions(GroupPotions gPotions) {
		this.gPotions = gPotions;
	}

	public GroupCaracteristiquesPerso getgCarac() {
		return gCarac;
	}

	public void setgCarac(GroupCaracteristiquesPerso gCarac) {
		this.gCarac = gCarac;
	}

	private Listener getListener() {
		return new Listener() {
			@Override
			public void handleEvent(Event arg0) {

				Item item = new Item(
						listenerCD.getClassForNamePluginItem(gArmes
								.getValSelection()),
						listenerCD.getClassForNamePluginItem(gArmures
								.getValSelection()),
						listenerCD.getClassForNamePluginItem(gPotions
								.getValSelection()));

				perso.setClassPerso(listenerCD
						.getClassForNamePluginClasse(gClasses.getValSelection()));

				perso.setNom(gAppercu.getNomPerso());
				perso.setItem(item);

				// � changer
				threadCD.stop();
				
			}

		};
	}

	private GridData getGridData() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.widthHint = 200;
		return gridData;
	}

	private Shell createFrame() {
		Shell fenetre = new Shell(display, SWT.CLOSE | SWT.MIN);
		fenetre.setSize(1024, 700);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 20;
		layout.marginTop = 20;
		layout.verticalSpacing = 25;
		layout.horizontalSpacing = 25;
		layout.makeColumnsEqualWidth = true;

		fenetre.setLayout(layout);
		return fenetre;
	}

	public static void centrerSurEcran(Display display, Shell shell) {
		Rectangle rect = display.getClientArea();
		Point size = shell.getSize();
		int x = (rect.width - size.x) / 2;
		int y = (rect.height - size.y) / 2;
		shell.setLocation(new Point(x, y));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (classes.size() != sizeListeClasses)
					listeClasses.add(classes.getLast().getNameClasse());
				else
					listeItems.add(items.getLast().getNameItem());
			}
		});
	}
}