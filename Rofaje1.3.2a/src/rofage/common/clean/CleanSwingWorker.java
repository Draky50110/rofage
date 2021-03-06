package rofage.common.clean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import rofage.common.Engine;
import rofage.common.SerializationHelper;
import rofage.common.files.FileToolkit;
import rofage.common.object.Configuration;
import rofage.common.object.Game;
import rofage.common.object.GlobalConfiguration;
import rofage.common.swingworker.StoppableSwingWorker;
import rofage.ihm.Messages;
import de.schlichtherle.io.ArchiveException;
import de.schlichtherle.io.File;

public class CleanSwingWorker extends StoppableSwingWorker<Integer, String> {
	
	private Configuration selConf;
	private TreeMap<Integer, Game> gameCollection; // <releaseNb, game>
	private List<Game> listArchivesToCleanUp;
	private JProgressBar jProgressBar;
	private JTextArea jTextArea;
	
	public CleanSwingWorker (Engine engine, 
			JProgressBar jProgressBar,
			JTextArea jTextArea,
			List<Game> listArchivesToCleanUp) {
		this.engine = engine;
		this.stopAction = false;
		this.selConf = engine.getGlobalConf().getSelectedConf();
		this.gameCollection = engine.getGameDB().getGameCollections().get(selConf.getConfName());
		this.listArchivesToCleanUp = listArchivesToCleanUp;
		this.jProgressBar = jProgressBar;
		this.jTextArea = jTextArea;
		
		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if("progress".equals(evt.getPropertyName())) { //$NON-NLS-1$
					getJProgressBar().setValue((Integer) evt.getNewValue());
				}
            }
		});
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		// Now we scan folders
		// We prevent additional run of this SW
		engine.getCleanWindow().getJButton().setEnabled(false);
		engine.getCleanWindow().getButtonStop().setEnabled(true);
		setProgress(0);
		publish(Messages.getString("CleanSwingWorker.1")); //$NON-NLS-1$
		
		// if the list is not defined, it means we perform a full clean up
		// for this configuration
		// TODO fix import 
		if (listArchivesToCleanUp==null) {
			generateCleanList(gameCollection.values()); // We perform a full clean up
		} else {
			generateCleanList(listArchivesToCleanUp);
		}
		
		publish (listArchivesToCleanUp.size()+Messages.getString("CleanSwingWorker.2")); //$NON-NLS-1$
		
		cleanUpArchivesInList();
		
		setProgress(100);
		publish (Messages.getString("CleanSwingWorker.3")); //$NON-NLS-1$
		
		// We save the results
		SerializationHelper.saveGameDB(engine.getGameDB());
		engine.getCleanWindow().getJButton().setEnabled(true);
		engine.getCleanWindow().getButtonStop().setEnabled(false);
		return 0;
	}
	
	@Override
    protected void process(List<String> strings) {
        /* Affichage des publications reçues dans le textarea. */
        for(String s : strings)
        	jTextArea.append(s + '\n');
    }
	
	/**
	 * Generates the list of archives to be cleaned up from the colGames
	 *
	 */
	private void generateCleanList (Collection<Game> colGames) {
		listArchivesToCleanUp = new ArrayList<Game>();
		Iterator<Game> iterGames = colGames.iterator();
		while (iterGames.hasNext() && !stopAction) {
			Game game = iterGames.next();
			if (game.isGotRom() 
					&& GlobalConfiguration.allowedCompressedExtensions.contains(FileToolkit.getFileExtension(game.getContainerPath()).toLowerCase())) { //$NON-NLS-1$
				// We look into each archive to see if there are more than one file
				File zipFile = new File(game.getContainerPath());
				if (zipFile.exists() && zipFile.list().length>1) {
					listArchivesToCleanUp.add(game);
				}
			}
		}
	}
	
	/**
	 * Cleans up the archive in the list
	 * It removes any other files than the rom file
	 *
	 */
	private void cleanUpArchivesInList () {
		int nbFilesRenamed = 0;
		int totalFiles = listArchivesToCleanUp.size();
		
		Iterator<Game> iterGames = listArchivesToCleanUp.iterator();
		while (iterGames.hasNext() && !stopAction) {
			Game game = iterGames.next();
			File gameFile = new File (game.getContainerPath());
			
			// We check whether the file still exists !
			if (!gameFile.exists()) {
				publish (Messages.getString("CleanSwingWorker.5")+gameFile.getAbsolutePath()+Messages.getString("CleanSwingWorker.6")); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				File[] tabFiles = (File[])gameFile.listFiles();
				for (int i=0; i<tabFiles.length; i++) {
					File curFile = tabFiles[i];
					// If the file isn't a rom we remove it
					if (!selConf.getAllowedExtensions().contains(FileToolkit.getFileExtension(curFile.getName()).toLowerCase())) {
						curFile.delete();
					}
				}
				
				try {
					File.umount();
				} catch (ArchiveException e) {
					e.printStackTrace();
				}
				publish (gameFile.getName()+Messages.getString("CleanSwingWorker.9")); //$NON-NLS-1$
				
				nbFilesRenamed++;
				setProgress(nbFilesRenamed*100/totalFiles);
			}
		}
	}
	
	public Engine getEngine() {
		return engine;
	}

	public JProgressBar getJProgressBar() {
		return jProgressBar;
	}

}
