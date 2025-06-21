package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class UnknownsPanel {

	public JPanel panel;

	public JLabel unknown1Lbl, unknown2Lbl, unknown3Lbl, unknown4Lbl;
	public JTextField unknown0TxFld, unknown1TxFld, unknown2TxFld, unknown3TxFld;
	public JButton copyBtn, pasteBtn;

	public UnknownsPanel(MainWindow main) {
		panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Unknowns - !! EDIT WITH CAUTION !!", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		panel.setBounds(10, 614, 541, 133);
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

		unknown0TxFld = new JTextField(47);
		unknown0TxFld.setName("Unknown 1");
		unknown0TxFld.setBounds(145, 18, 385, 22);
		unknown0TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown0TxFld);

		unknown1TxFld = new JTextField(47);
		unknown1TxFld.setName("Unknown 2");
		unknown1TxFld.setBounds(145, 44, 385, 22);
		unknown1TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown1TxFld);

		unknown2TxFld = new JTextField(47);
		unknown2TxFld.setName("Unknown 3");
		unknown2TxFld.setBounds(145, 70, 385, 22);
		unknown2TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown2TxFld);

		unknown3TxFld = new JTextField(31);
		unknown3TxFld.setName("Unknown 4");
		unknown3TxFld.setBounds(145, 96, 192, 22);
		unknown3TxFld.setFont(new Font("Monospaced", Font.PLAIN, 14));
		panel.add(unknown3TxFld);

		setUpHexFields();

		pasteBtn = new JButton("P");
		pasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				main.loadUnknowns(main.getCopiedUnknowns());
			}
		});
		pasteBtn.setMargin(new Insets(0, 0, 0, 0));
		pasteBtn.setFont(new Font("Tahoma", Font.PLAIN, 9));
		pasteBtn.setEnabled(false);
		pasteBtn.setBounds(511, 102, 20, 20);
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
		copyBtn.setBounds(488, 102, 20, 20);
		panel.add(copyBtn);
	}

	private void setUpHexFields() {
		unknown0TxFld.setDocument(new PlainDocument());
		((PlainDocument) unknown0TxFld.getDocument()).setDocumentFilter(new HexByteFilter(16, unknown0TxFld));

		unknown1TxFld.setDocument(new PlainDocument());
		((PlainDocument) unknown1TxFld.getDocument()).setDocumentFilter(new HexByteFilter(16, unknown1TxFld));

		unknown2TxFld.setDocument(new PlainDocument());
		((PlainDocument) unknown2TxFld.getDocument()).setDocumentFilter(new HexByteFilter(16, unknown2TxFld));

		unknown3TxFld.setDocument(new PlainDocument());
		((PlainDocument) unknown3TxFld.getDocument()).setDocumentFilter(new HexByteFilter(8, unknown3TxFld));

		unknown0TxFld.addKeyListener(new HexKeyAdapter(unknown0TxFld));
		unknown1TxFld.addKeyListener(new HexKeyAdapter(unknown1TxFld));
		unknown2TxFld.addKeyListener(new HexKeyAdapter(unknown2TxFld));
		unknown3TxFld.addKeyListener(new HexKeyAdapter(unknown3TxFld));
	}

	public void setAllEnabled(boolean enabled) {
		unknown1Lbl.setEnabled(enabled);
		unknown2Lbl.setEnabled(enabled);
		unknown3Lbl.setEnabled(enabled);
		unknown4Lbl.setEnabled(enabled);
		unknown0TxFld.setEnabled(enabled);
		unknown1TxFld.setEnabled(enabled);
		unknown2TxFld.setEnabled(enabled);
		unknown3TxFld.setEnabled(enabled);
		copyBtn.setEnabled(enabled);
		pasteBtn.setEnabled(enabled);
	}

	public void setUnknown0(String hex) {
		unknown0TxFld.setText(hex);
	}

	public void setUnknown1(String hex) {
		unknown1TxFld.setText(hex);
	}

	public void setUnknown2(String hex) {
		unknown2TxFld.setText(hex);
	}

	public void setUnknown3(String hex) {
		unknown3TxFld.setText(hex);
	}

	public String getUnknowns() {
		return unknown0TxFld.getText();
	}

	static class HexKeyAdapter extends KeyAdapter {

		private final JTextField textField;

		public HexKeyAdapter(JTextField textField) {
			this.textField = textField;
			InputMap inputMap = textField.getInputMap();
			inputMap.put(KeyStroke.getKeyStroke("DELETE"), "none");
			inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "none");
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int caret = textField.getCaretPosition();
			String text = textField.getText();

			switch (e.getKeyCode()) {
			case KeyEvent.VK_BACK_SPACE:
				if (caret == 0) {
					e.consume();
					break;
				}

				if (text.charAt(caret - 1) == ' ') {
					if (caret - 2 >= 0) {
						replaceChar(textField, caret - 2, '0');
						SwingUtilities.invokeLater(() -> textField.setCaretPosition(caret - 2));
					}
				} else {
					replaceChar(textField, caret - 1, '0');
					SwingUtilities.invokeLater(() -> textField.setCaretPosition(caret - 1));
				}
				e.consume(); // Block default Swing behavior
				break;

			case KeyEvent.VK_DELETE:
				if (caret >= text.length()) {
					e.consume();
					break;
				}

				if (text.charAt(caret) == ' ') {
					if (caret + 1 < text.length()) {
						replaceChar(textField, caret + 1, '0');
						SwingUtilities.invokeLater(() -> textField.setCaretPosition(Math.min(caret + 2, text.length())));
					}
				} else {
					replaceChar(textField, caret, '0');
					SwingUtilities.invokeLater(() -> textField.setCaretPosition(Math.min(caret + 1, text.length())));
				}
				e.consume(); // Block default Swing behavior
				break;

			case KeyEvent.VK_LEFT:
				if (caret > 0 && text.charAt(caret - 1) == ' ') {
					SwingUtilities.invokeLater(() -> textField.setCaretPosition(caret - 1));
					e.consume();
				}
				break;

			case KeyEvent.VK_RIGHT:
				if (caret < text.length() && text.charAt(caret) == ' ') {
					SwingUtilities.invokeLater(() -> textField.setCaretPosition(caret + 1));
					e.consume();
				}
				break;
			}
		}

		private void replaceChar(JTextField field, int pos, char replacement) {
			try {
				Document doc = field.getDocument();
				if (doc instanceof AbstractDocument) {
					((AbstractDocument) doc).replace(pos, 1, String.valueOf(replacement), null);
				}
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
	}

	static class HexByteFilter extends DocumentFilter {

		private final JTextField textField;
		private final int bytes, maxLength;

		public HexByteFilter(int bytes, JTextField textField) {
			this.bytes = bytes;
			this.textField = textField;
			this.maxLength = bytes * 2 + bytes - 1;
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			if (string == null) return;
			replace(fb, offset, 0, string, attr);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			if (text == null || text.isEmpty()) return;

			Document doc = fb.getDocument();
			String original = doc.getText(0, doc.getLength());

			// Fast path: full replace and input is already valid
			if (offset == 0 && length == doc.getLength() && isValidFormattedHex(text)) {
				fb.replace(offset, length, text.toUpperCase(), attrs);
				return;
			}

			StringBuilder raw = new StringBuilder(original.replaceAll("[^0-9A-Fa-f]", ""));
			int rawOffset = getRawOffset(original, offset);

			String cleanInput = text.replaceAll("[^0-9A-Fa-f]", "").toUpperCase();
			if (cleanInput.isEmpty()) return;

			for (int i = 0; i < cleanInput.length(); i++) {
				if (rawOffset + i < raw.length()) {
					raw.setCharAt(rawOffset + i, cleanInput.charAt(i));
				}
			}

			String formatted = formatHexInput(raw.toString());
			if (formatted.length() > maxLength) return;

			int newCaretRaw = rawOffset + cleanInput.length();
			int newCaretFormatted = mapRawIndexToFormattedCaret(newCaretRaw);

			fb.replace(0, doc.getLength(), formatted, attrs);

			SwingUtilities.invokeLater(() -> textField.setCaretPosition(Math.min(newCaretFormatted, formatted.length())));
		}

		private boolean isValidFormattedHex(String input) {
			// Valid if it matches "XX XX XX ..." pattern for the given number of bytes
			String[] parts = input.trim().split(" ");
			if (parts.length != bytes) return false;
			for (String part : parts) {
				if (!part.matches("[0-9A-Fa-f]{2}")) return false;
			}
			return true;
		}

		private int getRawOffset(String formatted, int offset) {
			int rawOffset = 0;
			for (int i = 0; i < offset && i < formatted.length(); i++) {
				if (Character.isLetterOrDigit(formatted.charAt(i))) {
					rawOffset++;
				}
			}
			return rawOffset;
		}

		private int mapRawIndexToFormattedCaret(int rawIndex) {
			int spaces = rawIndex / 2;
			return rawIndex + spaces;
		}

		private String formatHexInput(String input) {
			input = input.replaceAll("[^0-9A-Fa-f]", ""); // Strip non-hex
			input = input.toUpperCase();

			if (input.length() > bytes * 2) return null; // A byte is 2 chars

			StringBuilder result = new StringBuilder();
			for (int i = 0; i < input.length(); i += 2) {
				if (i > 0) result.append(" ");
				result.append(input.charAt(i));
				if (i + 1 < input.length()) result.append(input.charAt(i + 1));
			}
			return result.toString();
		}

	}

}
