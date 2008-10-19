package rofage.ihm.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import rofage.common.Engine;
import rofage.ihm.Messages;
import rofage.ihm.actions.common.community.LogoutAction;
import rofage.ihm.actions.common.community.ShowLoginAction;

@SuppressWarnings("serial")
public class MenuCommunity extends JMenu {
	
	private Engine engine	= null;
	
	private JMenuItem mItemLogin 		= null;
	private JMenuItem mItemLogout	= null;
		
	public MenuCommunity (Engine engine) {
		this.engine = engine;
		setText(Messages.getString("Community.community"));
		
		add(getMItemLogin());
		add(getMItemLogout());
		
	}

	public JMenuItem getMItemLogin() {
		if (mItemLogin==null) {
			mItemLogin = new JMenuItem();
			mItemLogin.addActionListener(new ShowLoginAction(engine));
			mItemLogin.setText(Messages.getString("Community.login")); //$NON-NLS-1$
			mItemLogin.setVisible(true);
		}
		return mItemLogin;
	}
	
	public JMenuItem getMItemLogout() {
		if (mItemLogout==null) {
			mItemLogout = new JMenuItem();
			mItemLogout.addActionListener(new LogoutAction(engine));
			mItemLogout.setText(Messages.getString("Community.logout")); //$NON-NLS-1$
			mItemLogout.setVisible(true);
		}
		return mItemLogout;
	}
}