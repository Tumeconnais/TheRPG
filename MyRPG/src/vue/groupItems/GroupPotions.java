package vue.groupItems;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import annot.Item;
import annot.TypeItem;
import util.PathManager;
import chargementDynamique.ChargementDynamique;
import chargementDynamique.ListenerChargementDyn;

public class GroupPotions implements Observer {

	private Group thisGroup;
	private LinkedList<ChargementDynamique> potions;
	private List listeDesPotions;
	private String valSelection = "";

	public String getValSelection() {
		return valSelection;
	}

	public GroupPotions(Shell fenetre, LinkedList<ChargementDynamique> items,
			GridData gridData) {

		this.potions = items;
		thisGroup = new Group(fenetre, SWT.FLAT);
		thisGroup.setLayoutData(gridData);

		listeDesPotions = new List(thisGroup, SWT.BORDER);

		thisGroup.setText("Choisir une potion");
		thisGroup.setFont(new Font(fenetre.getDisplay(), "Arial", 12, SWT.BOLD));
		FillLayout fl = new FillLayout(SWT.VERTICAL);
		fl.marginHeight = 10;
		fl.marginWidth = 55;
		fl.spacing = 5;
		thisGroup.setLayout(fl);
		thisGroup.setBackgroundImage(new Image(fenetre.getDisplay(),
				PathManager.bgGroup));

		FillList();
		addListener();

		listeDesPotions.setBackgroundImage(new Image(fenetre.getDisplay(),
				PathManager.bgGroup));
		listeDesPotions.setFont(new Font(fenetre.getDisplay(), "Arial", 12,
				SWT.NONE));
		
		
			listeDesPotions.setEnabled(true);
	}
	

	private void addListener() {
		listeDesPotions.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent arg0) {
				super.mouseDown(arg0);
				valSelection = listeDesPotions.getItem(listeDesPotions
						.getSelectionIndex());
			}
		});
	}

	private void FillList() {
		for (ChargementDynamique potion : potions) {
			if (potion.getTypeItem() == "Poison" || potion.getTypeItem() == "Potion" )
				listeDesPotions.add(potion.getNameItem());
		}
		listeDesPotions.pack();
	}

	@Override
	public void update(final Observable arg0, Object arg1) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if(arg0 instanceof ListenerChargementDyn){
					if(potions.size()!=0){
					Item Item = (Item) potions.getLast().getClassCharged()
							.getAnnotations()[0];
					if(Item.type() ==TypeItem.Potion || Item.type() == TypeItem.Poison){
				listeDesPotions.add(potions.getLast().getNameItem());
				listeDesPotions.redraw();
				thisGroup.pack();
					}
					}
				}
			}
		});
	}

}
