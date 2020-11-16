package snytng.astah.plugin.mindplus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.presentation.INodePresentation;

public class NodeVisibility {
	/**
	 * logger
	 */
	static final Logger logger = Logger.getLogger(NodeVisibility.class.getName());
	static {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.CONFIG);
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
	}

	public NodeVisibility() {
	}

	private static final String SUB_TOPIC_VISIBILITY = "sub_topic_visibility";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	private boolean isSubTopicVisible(INodePresentation np){
		return np.getProperty(SUB_TOPIC_VISIBILITY).equals(TRUE);
	}

	private boolean isSubTopicInvisible(INodePresentation np){
		return ! isSubTopicVisible(np);
	}

	private void setSubtopicvisibility(INodePresentation np, boolean npv) {
		try {
			np.setProperty(SUB_TOPIC_VISIBILITY, npv ? TRUE : FALSE);
		}catch(InvalidEditingException e){
			e.printStackTrace();
		}
	}

	private void setSubTopicVisible(INodePresentation np) {
		setSubtopicvisibility(np, true);
	}

	private void setSubTopicInvisible(INodePresentation np) {
		setSubtopicvisibility(np, false);
	}


	public void openCloseNode(INodePresentation np){
		if(isSubTopicVisible(np)){
			setSubTopicInvisible(np);
		} else {
			setSubTopicVisible(np);
		}
	}

	public void openAllNode(INodePresentation np) {
		setSubTopicVisible(np);
		for(INodePresentation p : np.getChildren()){
			this.openAllNode(p);
		}
	}

	public void closeAllNode(INodePresentation np) {
		setSubTopicInvisible(np);
		for(INodePresentation p : np.getChildren()){
			this.closeAllNode(p);
		}
	}

	private void setOpenNodeDepth(INodePresentation np, int depth) {
		logger.log(Level.INFO, "np open node=" + np.getLabel() + ", depth=" + depth);

		if(depth < 0){
			setSubTopicInvisible(np);
		}

		INodePresentation[] ns = new INodePresentation[]{np};

		for (int i = 0; i <= depth; i++) {
			if(ns.length == 0){
				break;
			}

			for(INodePresentation n : ns){
				setSubTopicVisible(n);
			}

			if(i == depth){
				for(INodePresentation n : ns){
					for(INodePresentation nc : n.getChildren()){
						setSubTopicInvisible(nc);
					}
				}
				break;
			}

			// next nodes
			List<INodePresentation> nlist = new ArrayList<>();
			for(INodePresentation n : ns){
				nlist.addAll(Arrays.asList(n.getChildren()));
			}
			ns = nlist.toArray(new INodePresentation[nlist.size()]);
		}

	}

	public void openNode(INodePresentation np) {

		int depth = 0;
		INodePresentation[] ns = new INodePresentation[]{np};

		DEPTH: while(true){

			// check open/close at n-th depth
			for(INodePresentation n : ns){
				if(isSubTopicInvisible(n)){
					break DEPTH;
				}
			}

			// next depth
			List<INodePresentation> nlist = new ArrayList<>();
			for(INodePresentation n : ns) { nlist.addAll(Arrays.asList(n.getChildren())); }
			ns = nlist.toArray(new INodePresentation[nlist.size()]);

			// check next depth exists
			if(ns.length == 0){
				break;
			} else {
				depth++;
			}
		}

		setOpenNodeDepth(np, depth);

	}

	public void closeNode(INodePresentation np) {

		int depth = 0;
		INodePresentation[] ns = new INodePresentation[]{np};

		DEPTH: while(true){

			// check open/close at n-th depth
			boolean visibleDepth = false;
			boolean invisibleDepth = false;
			for(INodePresentation n : ns){
				if(isSubTopicVisible(n)){
					visibleDepth = true;
				}
				if(isSubTopicInvisible(n)){
					invisibleDepth = true;
				}
			}

			if((! visibleDepth) && invisibleDepth) {
				depth--;
				if(depth < 0){
					depth = 0;
				}
				break DEPTH;
			}

			// next depth
			List<INodePresentation> nlist = new ArrayList<>();
			for(INodePresentation n : ns) { nlist.addAll(Arrays.asList(n.getChildren())); }
			ns = nlist.toArray(new INodePresentation[nlist.size()]);

			// check next depth exists
			if(ns.length == 0){
				depth--;
				if(depth < 0){
					depth = 0;
				}
				break DEPTH;
			} else {
				depth++;
			}
		}

		depth--;
		setOpenNodeDepth(np, depth);
	}


}
