package snytng.astah.plugin.mindplus;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.MindmapEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IMindMapDiagram;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramEditorSelectionEvent;
import com.change_vision.jude.api.inf.view.IDiagramEditorSelectionListener;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;
import com.change_vision.jude.api.inf.view.IEntitySelectionEvent;
import com.change_vision.jude.api.inf.view.IEntitySelectionListener;

public class View
extends
JPanel
implements
IPluginExtraTabView,
ProjectEventListener,
IEntitySelectionListener,
IDiagramEditorSelectionListener
{
	/**
	 * logger
	 */
	static final Logger logger = Logger.getLogger(View.class.getName());
	static {
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.CONFIG);
		logger.addHandler(consoleHandler);
		logger.setUseParentHandlers(false);
	}

	/**
	 * プロパティファイルの配置場所
	 */
	private static final String VIEW_PROPERTIES = "snytng.astah.plugin.mindplus.view";

	/**
	 * リソースバンドル
	 */
	private static final ResourceBundle VIEW_BUNDLE = ResourceBundle.getBundle(VIEW_PROPERTIES, Locale.getDefault());

	private String title = "<Mind+>";
	private String description = "<This plugin edits a mindmap diagram.>";

	private static final long serialVersionUID = 1L;
	private transient ProjectAccessor projectAccessor = null;
	private transient IDiagramViewManager diagramViewManager = null;

	public View() {
		try {
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			diagramViewManager = projectAccessor.getViewManager().getDiagramViewManager();
		} catch (Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		initProperties();

		initComponents();
	}

	private void initProperties() {
		try {
			title = VIEW_BUNDLE.getString("pluginExtraTabView.title");
			description = VIEW_BUNDLE.getString("pluginExtraTabView.description");
		}catch(Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private void initComponents() {
		// レイアウトの設定
		setLayout(new GridLayout(1,1));
		add(createButtonsPane());
		deactivateButtons();
	}

	private void addDiagramListeners(){
		diagramViewManager.addDiagramEditorSelectionListener(this);
		diagramViewManager.addEntitySelectionListener(this);
	}

	private void removeDiagramListeners(){
		diagramViewManager.removeDiagramEditorSelectionListener(this);
		diagramViewManager.removeEntitySelectionListener(this);
	}

	// 削除ボタン
	JButton deleteMaruButton      = new JButton("。");
	JButton deleteCRButton        = new JButton("改行(S)");
	JButton deleteMaruAndCRButton = new JButton("。と改行");
	JButton deleteSpaceButton     = new JButton("前後空白(Z)");
	JTextField deleteRegexText    = new JTextField(5);
	JButton deleteRegexButton     = new JButton("正規表現");

	// 分割ボタン
	JButton splitMaruButton       = new JButton("。(L)");
	JButton splitCRButton         = new JButton("改行(J)");
	JButton splitMaruAndCRButton  = new JButton("。と改行");
	JButton splitBLButton         = new JButton("空行(K)");
	JTextField splitRegexText     = new JTextField(5);
	JButton splitRegexButton      = new JButton("正規表現");

	// 置換ボタン
	JButton replaceMaru2CRButton  = new JButton("。 ⇒ 改行(C)");

	// 統合ボタン
	JButton mergeButton   = new JButton("マージ(M)");
	JButton reverseButton = new JButton("逆順(B)");
	JButton rotateButton = new JButton("回転(N)");

	// 雲ボタン
	JButton cloudButton = new JButton();
	JButton skyButton   = new JButton();

	// オープンボタン
	JButton openCloseButton = new JButton();
	JButton openAllButton = new JButton();
	JButton closeAllButton = new JButton();
	JButton openButton = new JButton();
	JButton closeButton = new JButton();

	// 改行ボタン
	JButton returnMaruButton = new JButton("。(C)");
	JButton returnTenButton = new JButton("、");

	// 移動ボタン
	JButton upNodeButton = new JButton("上↑");
	JButton downNodeButton = new JButton("下↓");
	JButton rightNodeButton = new JButton("右→");
	JButton leftNodeButton = new JButton("左←");

	// 追加ボタン
	JButton addDateButton = new JButton("日付(U)");
	JButton addTimeButton = new JButton("時間(I)");

	// セパレーター
	@SuppressWarnings("serial")
	private JSeparator getSeparator(){
		return new JSeparator(SwingConstants.VERTICAL){
			@Override public Dimension getPreferredSize() {
				return new Dimension(1, 16);
			}
			@Override public Dimension getMaximumSize() {
				return this.getPreferredSize();
			}
		};
	}

	private void activateButtons(){
		setButtonsEnabled(true);
	}
	private void deactivateButtons(){
		setButtonsEnabled(false);
	}
	private void setButtonsEnabled(boolean b){
		deleteMaruButton.setEnabled(b);
		deleteCRButton.setEnabled(b);
		deleteMaruAndCRButton.setEnabled(b);
		deleteSpaceButton.setEnabled(b);
		deleteRegexText.setEnabled(b);
		deleteRegexButton.setEnabled(b);
		splitMaruButton.setEnabled(b);
		splitCRButton.setEnabled(b);
		splitMaruAndCRButton.setEnabled(b);
		splitBLButton.setEnabled(b);
		splitRegexText.setEnabled(b);
		splitRegexButton.setEnabled(b);
		replaceMaru2CRButton.setEnabled(b);

		mergeButton.setEnabled(b);
		reverseButton.setEnabled(b);
		rotateButton.setEnabled(b);

		cloudButton.setEnabled(b);
		skyButton.setEnabled(b);
		openCloseButton.setEnabled(b);
		openAllButton.setEnabled(b);
		closeAllButton.setEnabled(b);
		openButton.setEnabled(b);
		closeButton.setEnabled(b);
		returnMaruButton.setEnabled(b);
		returnTenButton.setEnabled(b);
		upNodeButton.setEnabled(b);
		downNodeButton.setEnabled(b);
		rightNodeButton.setEnabled(b);
		leftNodeButton.setEnabled(b);
		addDateButton.setEnabled(b);
		addTimeButton.setEnabled(b);
	}

	private Container createButtonsPane() {
		//	button mnemonic
		openCloseButton.setMnemonic(KeyEvent.VK_SLASH);
		openAllButton.setMnemonic(KeyEvent.VK_END);
		closeAllButton.setMnemonic(KeyEvent.VK_HOME);
		openButton.setMnemonic(KeyEvent.VK_PAGE_DOWN);
		closeButton.setMnemonic(KeyEvent.VK_PAGE_UP);

		cloudButton.setMnemonic(KeyEvent.VK_COMMA);
		skyButton.setMnemonic(KeyEvent.VK_PERIOD);

		mergeButton.setMnemonic(KeyEvent.VK_M);

		splitMaruButton.setMnemonic(KeyEvent.VK_L);
		splitCRButton.setMnemonic(KeyEvent.VK_J);
		splitBLButton.setMnemonic(KeyEvent.VK_K);

		deleteCRButton.setMnemonic(KeyEvent.VK_S);
		deleteSpaceButton.setMnemonic(KeyEvent.VK_Z);

		returnMaruButton.setMnemonic(KeyEvent.VK_C);

		reverseButton.setMnemonic(KeyEvent.VK_B);
		rotateButton.setMnemonic(KeyEvent.VK_N);

		addDateButton.setMnemonic(KeyEvent.VK_U);
		addTimeButton.setMnemonic(KeyEvent.VK_I);


		upNodeButton.setMnemonic(KeyEvent.VK_UP);
		downNodeButton.setMnemonic(KeyEvent.VK_DOWN);
		rightNodeButton.setMnemonic(KeyEvent.VK_RIGHT);
		leftNodeButton.setMnemonic(KeyEvent.VK_LEFT);

		// button listeners
		setButtonIcon(openCloseButton, "/", "/snytng/astah/plugin/mindplus/images/pm.png");
		openCloseButton.addActionListener(e -> openCloseNode(this::openCloseNode));

		setButtonIcon(openAllButton, "/snytng/astah/plugin/mindplus/images/pp.png");
		openAllButton.addActionListener(e -> openCloseNode(this::openAllNode));

		setButtonIcon(closeAllButton, "/snytng/astah/plugin/mindplus/images/mm.png");
		closeAllButton.addActionListener(e -> openCloseNode(this::closeAllNode));

		setButtonIcon(openButton, "/snytng/astah/plugin/mindplus/images/p.png");
		openButton.addActionListener(e -> openCloseNode(this::openNode));

		setButtonIcon(closeButton, "/snytng/astah/plugin/mindplus/images/m.png");
		closeButton.addActionListener(e -> openCloseNode(this::closeNode));

		setButtonIcon(cloudButton, ",", "/snytng/astah/plugin/mindplus/images/cloud.png");
		cloudButton.addActionListener(e -> cloudNode(true) );

		setButtonIcon(skyButton, ".", "/snytng/astah/plugin/mindplus/images/nocloud.png");
		skyButton.addActionListener(e -> cloudNode(false) );

		deleteMaruButton.addActionListener(e -> replaceString(TextConverter::deleteMaru));
		deleteCRButton.addActionListener(e -> replaceString(TextConverter::deleteCR));
		deleteMaruAndCRButton.addActionListener(e -> replaceString(TextConverter::deleteMaruAndCR) );
		deleteSpaceButton.addActionListener(e -> replaceString(TextConverter::deleteSpace));
		deleteRegexButton.addActionListener(e -> replaceString(input -> TextConverter.deleteRegex(input, deleteRegexText.getText())) );

		mergeButton.addActionListener(e -> mergeNodes() );

		reverseButton.addActionListener(e -> reverseNodes() );
		rotateButton.addActionListener(e -> rotateNodes() );

		splitMaruButton.addActionListener(e -> splitNodes(TextConverter::splitMaru) );
		splitCRButton.addActionListener(e -> splitNodes(TextConverter::splitCR) );
		splitBLButton.addActionListener(e -> splitNodes(TextConverter::splitBL) );
		splitMaruAndCRButton.addActionListener(e -> splitNodes(TextConverter::splitMaruAndCR) );
		splitRegexButton.addActionListener(e -> splitNodes(input -> TextConverter.splitRegex(input, splitRegexText.getText())) );

		replaceMaru2CRButton.addActionListener(e -> replaceString(TextConverter::replaceMaru2CR));

		returnMaruButton.addActionListener(e -> replaceString(TextConverter::replaceMaru2MaruCR) );
		returnTenButton.addActionListener(e -> replaceString(TextConverter::replaceTen2TenCR) );

		upNodeButton.addActionListener(e -> moveNode(MoveDirection.UP) );
		downNodeButton.addActionListener(e -> moveNode(MoveDirection.DOWN) );
		rightNodeButton.addActionListener(e -> moveNode(MoveDirection.RIGHT) );
		leftNodeButton.addActionListener(e -> moveNode(MoveDirection.LEFT) );

		addDateButton.addActionListener(e -> addNodes(new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime())) );
		addTimeButton.addActionListener(e -> addNodes(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())) );


		// オープン パネル
		JPanel openPanel = new JPanel();
		openPanel.setLayout(new BoxLayout(openPanel, BoxLayout.X_AXIS));
		openPanel.setAlignmentX(LEFT_ALIGNMENT);
		openPanel.add(new JLabel("開閉："));
		// オープン・クローズ
		openPanel.add(openCloseButton);
		openPanel.add(openAllButton);
		openPanel.add(closeAllButton);
		openPanel.add(openButton);
		openPanel.add(closeButton);
		// 雲
		openPanel.add(getSeparator());// セパレーター
		openPanel.add(new JLabel("雲："));
		openPanel.add(cloudButton);
		openPanel.add(skyButton);

		// 編集パネル
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
		editPanel.setAlignmentX(LEFT_ALIGNMENT);
		// マージ
		editPanel.add(new JLabel("マージ："));
		editPanel.add(mergeButton);
		// 分割
		editPanel.add(getSeparator());// セパレーター
		editPanel.add(new JLabel("分割："));
		editPanel.add(splitMaruButton);
		editPanel.add(splitCRButton);
		//editPanel.add(splitMaruAndCRButton);
		editPanel.add(splitBLButton);
		// 正規表現
		/*
		editPanel.add(getSeparator());// セパレーター
		editPanel.add(splitRegexText);
		editPanel.add(splitRegexButton);
		*/
		// 削除
		editPanel.add(getSeparator());// セパレーター
		editPanel.add(new JLabel("削除："));
		//editPanel.add(deleteMaruButton);
		editPanel.add(deleteCRButton);
		//editPanel.add(deleteMaruAndCRButton);
		editPanel.add(deleteSpaceButton);
		/*
		editPanel.add(getSeparator());// セパレーター
		editPanel.add(deleteRegexText);
		editPanel.add(deleteRegexButton);
		*/
		// 置換
		/*
		editPanel.add(getSeparator());// セパレーター
		editPanel.add(new JLabel("置換："));
		editPanel.add(replaceMaru2CRButton);
		*/

		// 改行
		editPanel.add(getSeparator());// セパレーター
		editPanel.add(new JLabel("改行："));
		editPanel.add(returnMaruButton);
		//returnPanel.add(returnTenButton);
		/**
		// 移動
		editPanel.add(getSeparator());// セパレーター
		editPanel.add(upNodeButton);
		editPanel.add(downNodeButton);
		editPanel.add(rightNodeButton);
		editPanel.add(leftNodeButton);
		*/

		// 入れ替えパネル
		JPanel orderPanel = new JPanel();
		orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.X_AXIS));
		orderPanel.setAlignmentX(LEFT_ALIGNMENT);
		// 入れ替え
		orderPanel.add(getSeparator());// セパレーター
		orderPanel.add(new JLabel("順序："));
		orderPanel.add(reverseButton);
		orderPanel.add(rotateButton);
		// 追加
		orderPanel.add(getSeparator());// セパレーター
		orderPanel.add(new JLabel("追加："));
		orderPanel.add(addDateButton);
		orderPanel.add(addTimeButton);

		// パネル配置
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(openPanel);
		panel.add(editPanel);
		panel.add(orderPanel);

		return panel;
	}

	private void setButtonIcon(JButton button, String imageFile) {
		try {
			Image pmOrigImage = new ImageIcon(View.class.getResource(imageFile)).getImage();
			Image pmImage = pmOrigImage.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
			Icon pmIcon = new ImageIcon(pmImage);
			button.setIcon(pmIcon);
		}catch(Exception e){
			logger.warning(e.getMessage());
		}
	}

	private void setButtonIcon(JButton button, String label, String imageFile) {
		setButtonIcon(button, imageFile);
		button.setText(label);
	}

	private IMindMapDiagram getCurrentDiagram(){
		// 今選択している図のタイプを取得する
		IDiagram diagram = diagramViewManager.getCurrentDiagram();

		// マインドマップを探索
		if(diagram instanceof IMindMapDiagram){
			return (IMindMapDiagram)diagram;
		} else {
			logger.log(Level.WARNING, "current diagram is not MindMap Diagram.");
			return null;
		}
	}

	private void replaceString(UnaryOperator<String> f ){
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}

		try {

			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			for(INodePresentation np : nps){
				String input = np.getLabel();
				String output = f.apply(input);
				np.setLabel(output);
			}

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}


	private void splitNodes(UnaryOperator<String> func) {
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}


		try {

			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			for(INodePresentation np : nps){

				String input = np.getLabel();
				String output = func.apply(input);
				String[] lines = output.split(System.lineSeparator());
				long effectiveLineNumber = Arrays.stream(lines).filter(l -> ! l.equals("")).count();
				logger.log(Level.INFO, () -> "# of lines=" + lines.length + ", # of effective lines = " + effectiveLineNumber);

				// 分割する文字が存在したら分割する
				if(effectiveLineNumber > 0){

					// 追加するnpのインデックスを取得する
					int index = 0;
					INodePresentation[] pps = np.getParent().getChildren();
					for(int i = 0; i < pps.length; i++){
						if(pps[i] == np){
							index = i;
						}
					}

					// 行ごと追加（下から）
					for(int i = lines.length - 1; i >= 0; i--){
						String line = lines[i];
						if(line.equals("")){
							continue;
						}
						mme.createTopic(np.getParent(), line, index+1);
					}

					// npの子ノードを先頭ノードへ移動して、np削除する
					for(INodePresentation cp : np.getChildren()){
						mme.moveTo(cp, np.getParent().getChildren()[index+1]);
					}
					mme.deletePresentation(np);
				}
			}

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}

	}

	private void mergeNodes() {
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}

		// 最初のノードと兄弟のノードだけにして、上下の位置の降順に並べなおす
		INodePresentation parent = nps.get(0).getParent();
		INodePresentation[] children = parent.getChildren();

		List<INodePresentation> xps =
				Arrays.stream(children)
				.filter(nps::contains)
				.collect(Collectors.toList());


		if(xps.size() <= 1){
			logger.log(Level.WARNING, "neednot merge INodePresentation (0 or 1)");
			return;
		}


		// 追加するnpのインデックスを取得する
		int index = 0;
		INodePresentation xps0 = xps.get(0);
		INodePresentation[] pps = xps0.getParent().getChildren();
		for(int i = 0; i < pps.length; i++){
			if(pps[i] == xps0){
				index = i;
			}
		}

		try {
			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			// 選択したノードの文字列を改行でつなぐ
			String baseLabel = xps.stream()
					.map(INodePresentation::getLabel)
					.collect(Collectors.joining(System.lineSeparator()));

			// マージする対象のノードを新規に作成
			INodePresentation base = mme.createTopic(xps0.getParent(), baseLabel, index);


			// マージ対象のノードの子ノードをbaseに移動して、選択したノードを削除する
			for(INodePresentation p : xps){
				for(INodePresentation cp : p.getChildren()){
					mme.moveTo(cp, base);
				}
				mme.deletePresentation(p);
			}

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}

	}

	private void reverseNodes() {
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}

		// 最初のノードと兄弟のノードだけにして、上下の位置の降順に並べなおす
		INodePresentation parent = nps.get(0).getParent();
		INodePresentation[] children = parent.getChildren();

		List<INodePresentation> xps =
				Arrays.stream(children)
				.filter(nps::contains)
				.collect(Collectors.toList());


		if(xps.size() <= 1){
			logger.log(Level.WARNING, "neednot merge INodePresentation (0 or 1)");
			return;
		}


		// npのインデックスを取得する
		INodePresentation[] pps = xps.get(0).getParent().getChildren();
		List<Integer> newIndicies = new ArrayList<>();
		for(INodePresentation p : xps){
			int index = 0;
			for(int i = 0; i < pps.length; i++){
				if(pps[i] == p){
					index = i;
					break;
				}
			}
			newIndicies.add(index);
		}

		// newIndiciesを逆順にする
		Collections.reverse(newIndicies);

		// 新しい順序に並び替える
		try {
			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			// 入れ替え対象のノードをindexへ追加していく
			for(int i = 0; i < xps.size(); i++){
				INodePresentation p = xps.get(i);
				int index = newIndicies.get(i);
				mme.moveTo(p, parent, index);
			}

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}

	}

	private void rotateNodes() {
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}

		// 最初のノードと兄弟のノードだけにして、上下の位置の降順に並べなおす
		INodePresentation parent = nps.get(0).getParent();
		INodePresentation[] children = parent.getChildren();

		List<INodePresentation> xps =
				Arrays.stream(children)
				.filter(nps::contains)
				.collect(Collectors.toList());


		if(xps.size() <= 1){
			logger.log(Level.WARNING, "neednot merge INodePresentation (0 or 1)");
			return;
		}


		// npのインデックスを取得する
		INodePresentation[] pps = xps.get(0).getParent().getChildren();
		List<Integer> newIndicies = new ArrayList<>();
		for(INodePresentation p : xps){
			int index = 0;
			for(int i = 0; i < pps.length; i++){
				if(pps[i] == p){
					index = i;
					break;
				}
			}
			newIndicies.add(index);
		}

		// newIndiciesを一つローテートする
		Collections.rotate(newIndicies, -1);


		// 新しい順序に並び替える
		try {
			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			// 入れ替え対象のノードをindexへ追加していく
			for(int i = 0; i < xps.size(); i++){
				INodePresentation p = xps.get(i);
				int index = newIndicies.get(i);
				mme.moveTo(p, parent, index);
			}

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}

	}

	private enum MoveDirection {UP, DOWN, LEFT, RIGHT}
	private void moveNode(MoveDirection md) {
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}

		if(nps.size() > 1){
			logger.log(Level.WARNING, "not one INodePresentation selected");
			return;
		}

		// 上下左右に移動する
		if(md == MoveDirection.UP){
			// 兄弟ノードの上に移動する。最初だったら最終ノードに移動する。
			INodePresentation parent = nps.get(0).getParent();
			INodePresentation[] children = parent.getChildren();

			for(int i = 0; i < children.length; i++){
				INodePresentation np = children[i];
				if(np == nps.get(0)){
					if(i == 0){
						selectAndFocusPresentation(children[children.length - 1]);
					} else {
						selectAndFocusPresentation(children[i-1]);
					}
					break;
				}
			}

		} else if(md == MoveDirection.DOWN){
			// 兄弟ノードの下に移動する。最後だったら最初のノードに移動する。
			INodePresentation parent = nps.get(0).getParent();
			INodePresentation[] children = parent.getChildren();

			for(int i = children.length - 1; i >= 0; i--){
				INodePresentation np = children[i];
				if(np == nps.get(0)){
					if(i == children.length - 1){
						selectAndFocusPresentation(children[0]);
					} else {
						selectAndFocusPresentation(children[i+1]);
					}
					break;
				}
			}

		} else if(md == MoveDirection.LEFT){
			// 親ノードに移動する
			INodePresentation parent = nps.get(0).getParent();
			if(parent != null){
				selectAndFocusPresentation(parent);
			}

		} else if(md == MoveDirection.RIGHT){
			// 子ノードの最初に移動する
			INodePresentation[] children = nps.get(0).getChildren();
			if(children.length > 0){
				selectAndFocusPresentation(children[0]);
			} else {
				logger.log(Level.INFO, "no child INodePresentations");

			}

		}
	}

	private void selectAndFocusPresentation(IPresentation p){
		diagramViewManager.select(p);
		diagramViewManager.showInDiagramEditor(p);
	}

	private void openCloseNode(Consumer<INodePresentation> c){
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		INodePresentation np = null;
		for(IPresentation p : ps){
			if(p instanceof INodePresentation){
				np = (INodePresentation)p;
				break;
			}
		}

		if(np == null){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}

		try {

			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			c.accept(np);

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}

	}

	private void addNodes(String str) {
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}


		IPresentation topic = null;

		try {

			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			for(INodePresentation np : nps){
				topic = mme.createTopic(np, str);
			}

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}

		diagramViewManager.select(topic);

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


	private void openCloseNode(INodePresentation np){
		if(isSubTopicVisible(np)){
			setSubTopicInvisible(np);
		} else {
			setSubTopicVisible(np);
		}
	}

	private void openAllNode(INodePresentation np) {
		setSubTopicVisible(np);
		for(INodePresentation p : np.getChildren()){
			this.openAllNode(p);
		}
	}

	private void closeAllNode(INodePresentation np) {
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

	private void openNode(INodePresentation np) {

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

	private void closeNode(INodePresentation np) {

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

	private void cloudNode(boolean visibility){
		// マインドマップを取得、なければ終了
		IMindMapDiagram mmDiagram = getCurrentDiagram();
		if(mmDiagram == null){
			return;
		}

		IPresentation[] ps = diagramViewManager.getSelectedPresentations();
		if(ps.length == 0){
			logger.log(Level.WARNING, "no node selected");
			return;
		}

		List<INodePresentation> nps =
				Arrays.stream(ps)
				.filter(INodePresentation.class::isInstance)
				.filter(p -> Objects.nonNull(p.getLabel()) && ! p.getLabel().isEmpty()) // トピックノード選択（雲ノードを排除）
				.map(INodePresentation.class::cast)
				.collect(Collectors.toList());

		if(nps.isEmpty()){
			logger.log(Level.WARNING, "no INodePresentation selected");
			return;
		}

		try {
			MindmapEditor mme = projectAccessor.getDiagramEditorFactory().getMindmapEditor();
			mme.setDiagram(mmDiagram);

			TransactionManager.beginTransaction();

			for(INodePresentation np : nps){
				mme.setBoundaryVisibility(np, visibility);
			}

			TransactionManager.endTransaction();

		} catch (Exception e) {
			TransactionManager.abortTransaction();
			logger.log(Level.WARNING, e.getMessage(), e);
		}

	}


	/**
	 * プロジェクトが変更されたら表示を更新する
	 */
	@Override
	public void projectChanged(ProjectEvent e) {
		updateDiagramView();
	}
	@Override
	public void projectClosed(ProjectEvent e) {
		// Do nothing when project is closed
	}

	@Override
	public void projectOpened(ProjectEvent e) {
		// Do nothing when project is opened
	}

	/**
	 * 図の選択が変更されたら表示を更新する
	 */
	@Override
	public void diagramSelectionChanged(IDiagramEditorSelectionEvent e) {
		updateDiagramView();
	}

	/**
	 * 要素の選択が変更されたら表示を更新する
	 */
	@Override
	public void entitySelectionChanged(IEntitySelectionEvent e) {
		updateDiagramView();
	}

	/**
	 * 表示を更新する
	 */
	private void updateDiagramView() {
		try {

			deactivateButtons();

			// 今選択している図がマインドマップだと更新する
			IMindMapDiagram mmDiagram = getCurrentDiagram();
			if(mmDiagram == null){
				return;
			}

			// 選択項目を図を読み上げる
			IPresentation[] ps = diagramViewManager.getSelectedPresentations();
			INodePresentation np = null;
			for(IPresentation p : ps){
				if(p instanceof INodePresentation){
					INodePresentation cnp = (INodePresentation)p;
					String cmessage = cnp.getLabel();
					if(Objects.nonNull(cmessage) && ! cmessage.isEmpty()){
						np = cnp;
						break;
					}
				}
			}

			if(np == null){
				logger.log(Level.WARNING, "no selected node presentation with message");
				return;
			}


			logger.log(Level.INFO, "update diagram view.");

			/*
			logger.log(Level.INFO, "np properties");
			@SuppressWarnings("unchecked")
			HashMap<String, String> props = np.getProperties();
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String, String> entry : props.entrySet()){
				sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
			}
			logger.log(Level.INFO, sb::toString);
			 */

			activateButtons();

		}catch(Exception e){
			logger.log(Level.WARNING, e.getMessage(), e);
		}
	}

	// IPluginExtraTabView
	@Override
	public void addSelectionListener(ISelectionListener listener) {
		// Do nothing
	}

	@Override
	public void activated() {
		// リスナーへの登録
		addDiagramListeners();
	}

	@Override
	public void deactivated() {
		// リスナーへの削除
		removeDiagramListeners();
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getTitle() {
		return title;
	}

}
