package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class EnemySlotPanel {

	public JPanel slotPanel;

	public JLabel enemyIdLabel;
	public JComboBox<String> enemyIdComboBox;
	public JCheckBox enabledCheckBox;
	public JCheckBox notVisibleCheckBox;
	public JCheckBox notTargetableCheckBox;
	public JCheckBox notLoadedCheckBox;

	public JLabel positionLabel;
	public JLabel levelLabel;
	public JLabel xLabel;
	public JLabel yLabel;
	public JLabel zLabel;
	public JSpinner levelSpinner;
	public JSpinner xSpinner;
	public JSpinner ySpinner;
	public JSpinner zSpinner;

	public JButton copyBtn, pasteBtn;
	public JCheckBox selectedChkBox;

	public EnemySlotPanel(MainWindow main, int slotIndex, List<String> enemyNames) {
		slotPanel = new JPanel();
		slotPanel.setSize(338, 231);
		slotPanel.setLayout(null);
		slotPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Enemy Slot " + slotIndex, TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));

		enemyIdLabel = new JLabel("Enemy ID:");
		enemyIdLabel.setBounds(10, 21, 65, 14);
		slotPanel.add(enemyIdLabel);

		enemyIdComboBox = new JComboBox<>();
		enemyIdComboBox.setName("ID Slot " + slotIndex);
		for (String s : enemyNames) enemyIdComboBox.addItem(s);
		enemyIdComboBox.setBounds(85, 17, 243, 22);
		slotPanel.add(enemyIdComboBox);

		levelLabel = new JLabel("Level:");
		levelLabel.setBounds(10, 48, 65, 14);
		slotPanel.add(levelLabel);

		levelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		levelSpinner.setName("Level Slot " + slotIndex);
		levelSpinner.setEditor(new JSpinner.NumberEditor(levelSpinner, "#"));
		levelSpinner.setBounds(85, 45, 43, 20);
		slotPanel.add(levelSpinner);

		enabledCheckBox = new JCheckBox("Enabled");
		enabledCheckBox.setName("Enabled Slot " + slotIndex);
		enabledCheckBox.setBounds(10, 75, 97, 23);
		slotPanel.add(enabledCheckBox);

		notTargetableCheckBox = new JCheckBox("Not Targetable");
		notTargetableCheckBox.setName("Not Targetable Slot " + slotIndex);
		notTargetableCheckBox.setBounds(10, 101, 120, 23);
		slotPanel.add(notTargetableCheckBox);

		notVisibleCheckBox = new JCheckBox("Not Visible");
		notVisibleCheckBox.setName("Not Visible Slot " + slotIndex);
		notVisibleCheckBox.setBounds(10, 127, 97, 23);
		slotPanel.add(notVisibleCheckBox);

		notLoadedCheckBox = new JCheckBox("Not Loaded");
		notLoadedCheckBox.setName("Not Loaded Slot " + slotIndex);
		notLoadedCheckBox.setBounds(10, 153, 97, 23);
		slotPanel.add(notLoadedCheckBox);

		positionLabel = new JLabel("Position:");
		positionLabel.setBounds(143, 79, 60, 14);
		slotPanel.add(positionLabel);

		xLabel = new JLabel("x:");
		xLabel.setBounds(146, 105, 46, 14);
		slotPanel.add(xLabel);

		yLabel = new JLabel("y:");
		yLabel.setBounds(146, 131, 46, 14);
		slotPanel.add(yLabel);

		zLabel = new JLabel("z:");
		zLabel.setBounds(146, 157, 46, 14);
		slotPanel.add(zLabel);

		xSpinner = new JSpinner(new SpinnerNumberModel(0, -32768, 32767, 1));
		xSpinner.setName("x Slot " + slotIndex);
		xSpinner.setEditor(new JSpinner.NumberEditor(xSpinner, "#"));
		xSpinner.setBounds(251, 102, 77, 20);
		slotPanel.add(xSpinner);

		ySpinner = new JSpinner(new SpinnerNumberModel(0, -32768, 32767, 1));
		ySpinner.setName("y Slot " + slotIndex);
		ySpinner.setEditor(new JSpinner.NumberEditor(ySpinner, "#"));
		ySpinner.setBounds(251, 128, 77, 20);
		slotPanel.add(ySpinner);

		zSpinner = new JSpinner(new SpinnerNumberModel(0, -32768, 32767, 1));
		zSpinner.setName("z Slot " + slotIndex);
		zSpinner.setEditor(new JSpinner.NumberEditor(zSpinner, "#"));
		zSpinner.setBounds(251, 154, 77, 20);
		slotPanel.add(zSpinner);

		pasteBtn = new JButton("P");
		pasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EnemyData enemy = main.getCopiedEnemy();
				main.loadEnemy(slotIndex, enemy);
			}
		});
		pasteBtn.setMargin(new Insets(0, 0, 0, 0));
		pasteBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		pasteBtn.setEnabled(false);
		pasteBtn.setBounds(308, 200, 20, 20);
		slotPanel.add(pasteBtn);

		copyBtn = new JButton("C");
		copyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.setCopiedEnemy(getEnemyData());
				System.out.println(main.getCopiedEnemy());
			}
		});
		copyBtn.setMargin(new Insets(0, 0, 0, 0));
		copyBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		copyBtn.setEnabled(false);
		copyBtn.setBounds(285, 200, 20, 20);
		slotPanel.add(copyBtn);

		selectedChkBox = new JCheckBox("Enabled");
		selectedChkBox.setName(" ");
		selectedChkBox.setBounds(10, 200, 20, 23);
		slotPanel.add(selectedChkBox);
	}

	public void setAllEnabled(boolean enabled) {
		enemyIdLabel.setEnabled(enabled);
		enemyIdComboBox.setEnabled(enabled);
		enabledCheckBox.setEnabled(enabled);
		notTargetableCheckBox.setEnabled(enabled);
		notVisibleCheckBox.setEnabled(enabled);
		notLoadedCheckBox.setEnabled(enabled);
		positionLabel.setEnabled(enabled);
		levelLabel.setEnabled(enabled);
		xLabel.setEnabled(enabled);
		yLabel.setEnabled(enabled);
		zLabel.setEnabled(enabled);
		levelSpinner.setEnabled(enabled);
		xSpinner.setEnabled(enabled);
		ySpinner.setEnabled(enabled);
		zSpinner.setEnabled(enabled);
		copyBtn.setEnabled(enabled);
		pasteBtn.setEnabled(enabled);
		selectedChkBox.setEnabled(enabled);
	}

	public EnemyData getEnemyData() {
		return new EnemyData(getEnemyId(), getLevel(), isEnabledFlag(), isNotTargetableFlag(), isNotVisibleFlag(), isNotLoadedFlag(), getPosition());
	}

	public void setLevel(int level) {
		this.levelSpinner.setValue(level);
	}

	public void setEnemyId(int index) {
		enemyIdComboBox.setSelectedIndex(index);
	}

	public void setEnabledFlag(boolean value) {
		enabledCheckBox.setSelected(value);
	}

	public void setNotTargetableFlag(boolean value) {
		notTargetableCheckBox.setSelected(value);
	}

	public void setNotVisibleFlag(boolean value) {
		notVisibleCheckBox.setSelected(value);
	}

	public void setNotLoadedFlag(boolean value) {
		notLoadedCheckBox.setSelected(value);
	}

	public void setPosition(short x, short y, short z) {
		xSpinner.setValue(x);
		ySpinner.setValue(y);
		zSpinner.setValue(z);
	}

	public int getLevel() {
		return (int) levelSpinner.getValue();
	}

	public int getEnemyId() {
		return enemyIdComboBox.getSelectedIndex();
	}

	public boolean isEnabledFlag() {
		return enabledCheckBox.isSelected();
	}

	public boolean isNotTargetableFlag() {
		return notTargetableCheckBox.isSelected();
	}

	public boolean isNotVisibleFlag() {
		return notVisibleCheckBox.isSelected();
	}

	public boolean isNotLoadedFlag() {
		return notLoadedCheckBox.isSelected();
	}

	public Position getPosition() {
		short x = ((Number) xSpinner.getValue()).shortValue();
		short y = ((Number) ySpinner.getValue()).shortValue();
		short z = ((Number) zSpinner.getValue()).shortValue();
		return new Position(x, y, z);
	}

	public boolean isSelected() {
		return selectedChkBox.isSelected();
	}

}
