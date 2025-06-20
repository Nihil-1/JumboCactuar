package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class UnknownsPanel {

	public JPanel panel;

	public JLabel unknown1Lbl, unknown2Lbl, unknown3Lbl, unknown4Lbl;
	public JTextField unknown1TxFld, unknown2TxFld, unknown3TxFld, unknown4TxFld;
	public JButton copyBtn, pasteBtn;

	public UnknownsPanel(MainWindow main) {
		panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Unknowns - !! EDIT WITH CAUTION !!", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		panel.setBounds(10, 614, 539, 133);
		panel.setLayout(null);

		unknown1Lbl = new JLabel("Unknown 0 Bytes:");
		unknown1Lbl.setBounds(10, 21, 87, 14);
		panel.add(unknown1Lbl);

		unknown2Lbl = new JLabel("Unknown 1 Bytes:");
		unknown2Lbl.setBounds(10, 47, 87, 14);
		panel.add(unknown2Lbl);

		unknown3Lbl = new JLabel("Unknown 2 Bytes:");
		unknown3Lbl.setBounds(10, 73, 87, 14);
		panel.add(unknown3Lbl);

		unknown4Lbl = new JLabel("Unknown 3 Bytes:");
		unknown4Lbl.setBounds(10, 99, 87, 14);
		panel.add(unknown4Lbl);

		unknown1TxFld = new JTextField(47);
		unknown1TxFld.setName("Unknown 1");
		unknown1TxFld.setBounds(145, 18, 383, 22);
		unknown1TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown1TxFld);

		unknown2TxFld = new JTextField(47);
		unknown2TxFld.setName("Unknown 2");
		unknown2TxFld.setBounds(145, 44, 383, 22);
		unknown2TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown2TxFld);

		unknown3TxFld = new JTextField(47);
		unknown3TxFld.setName("Unknown 3");
		unknown3TxFld.setBounds(145, 70, 383, 22);
		unknown3TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown3TxFld);

		unknown4TxFld = new JTextField(31);
		unknown4TxFld.setName("Unknown 4");
		unknown4TxFld.setBounds(145, 96, 190, 22);
		unknown4TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown4TxFld);

		pasteBtn = new JButton("P");
		pasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.loadUnknowns(main.getCopiedUnknowns());
			}
		});
		pasteBtn.setMargin(new Insets(0, 0, 0, 0));
		pasteBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		pasteBtn.setEnabled(false);
		pasteBtn.setBounds(509, 102, 20, 20);
		panel.add(pasteBtn);

		copyBtn = new JButton("C");
		copyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.setCopiedUnknowns(main.getCurrentEncounter().getUnknowns());
			}
		});
		copyBtn.setMargin(new Insets(0, 0, 0, 0));
		copyBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		copyBtn.setEnabled(false);
		copyBtn.setBounds(486, 102, 20, 20);
		panel.add(copyBtn);
	}

	public void setAllEnabled(boolean enabled) {
		unknown1Lbl.setEnabled(enabled);
		unknown2Lbl.setEnabled(enabled);
		unknown3Lbl.setEnabled(enabled);
		unknown4Lbl.setEnabled(enabled);
		unknown1TxFld.setEnabled(enabled);
		unknown2TxFld.setEnabled(enabled);
		unknown3TxFld.setEnabled(enabled);
		unknown4TxFld.setEnabled(enabled);
		copyBtn.setEnabled(enabled);
		pasteBtn.setEnabled(enabled);
	}

	public void setUnknown1(String unk) {
		unknown1TxFld.setText(unk);
	}

	public void setUnknown2(String unk) {
		unknown2TxFld.setText(unk);
	}

	public void setUnknown3(String unk) {
		unknown3TxFld.setText(unk);
	}

	public void setUnknown4(String unk) {
		unknown4TxFld.setText(unk);
	}

	public String getUnknowns() {
		return unknown1TxFld.getText();
	}

}
