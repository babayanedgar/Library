package UI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Vertex Library visual theme: brand colors, fonts and component styles.
 * Values come from the Vertex Library brand book, so every screen looks the same.
 */
public final class Theme {

    // Brand palette
    public static final Color NAVY = new Color(0x071A42);      // primary: headings, sidebar, primary buttons
    public static final Color ROYAL = new Color(0x1E4FA3);     // secondary: links, active states, accents
    public static final Color SKY = new Color(0x85AEE0);       // accent on navy; never text on white
    public static final Color SKY_TINT = new Color(0xDAE7F6);  // sky at 30% on white: selected rows
    public static final Color INK = new Color(0x111827);       // body text
    public static final Color SLATE = new Color(0x5B6478);     // secondary text
    public static final Color MIST = new Color(0xE8EFF8);      // borders, dividers
    public static final Color FIELD_LINE = new Color(0xB8C4D6); // input borders: mist darkened so fields stay visible
    public static final Color PAPER = new Color(0xF7F8FB);     // page backgrounds
    public static final Color WHITE = Color.WHITE;             // cards, panels

    // Functional colors (match the book states in the app)
    public static final Color AVAILABLE = new Color(0x2E7D5B);
    public static final Color DUE_SOON = new Color(0x975A16);
    public static final Color OVERDUE = new Color(0xB42318);

    // Brand fonts are Libre Baskerville and DM Sans; fall back to fonts that ship with Windows
    private static final String SERIF = firstInstalled("Libre Baskerville", "Georgia", Font.SERIF);
    private static final String SANS = firstInstalled("DM Sans", "Segoe UI", Font.SANS_SERIF);

    public enum ButtonStyle { PRIMARY, SECONDARY, ACCENT, DANGER, QUIET }

    private Theme() {
    }

    public static Font heading(int size) {
        return new Font(SERIF, Font.BOLD, size);
    }

    public static Font body(int size) {
        return new Font(SANS, Font.PLAIN, size);
    }

    public static Font strong(int size) {
        return new Font(SANS, Font.BOLD, size);
    }

    /**
     * Sets app-wide Swing defaults. Call once, before any window is created.
     */
    public static void install() {
        put("Panel.background", PAPER);
        put("OptionPane.background", PAPER);
        put("Viewport.background", WHITE);
        put("ScrollPane.background", WHITE);
        put("Label.foreground", INK);
        put("CheckBox.foreground", INK);
        put("CheckBox.background", PAPER);
        put("TitledBorder.titleColor", NAVY);
        put("Button.background", WHITE);
        put("Button.foreground", NAVY);
        put("Button.select", MIST);
        put("Table.gridColor", MIST);
        UIManager.put("Button.gradient", null); // flat buttons instead of the Metal gradient
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));

        Font plain = body(14);
        for (String key : Arrays.asList("Label.font", "TextField.font", "PasswordField.font", "ComboBox.font",
                "CheckBox.font", "RadioButton.font", "Table.font", "List.font", "OptionPane.messageFont",
                "ToolTip.font", "TextArea.font")) {
            UIManager.put(key, new FontUIResource(plain));
        }
        for (String key : Arrays.asList("Button.font", "OptionPane.buttonFont", "TableHeader.font", "TitledBorder.font")) {
            UIManager.put(key, new FontUIResource(strong(13)));
        }
    }

    /**
     * Sets the Vertex Library badge as a window's title-bar and taskbar icon.
     */
    public static void applyIcon(Window window) {
        Image icon = loadImage("/logo-icon.png");
        if (icon == null) {
            return;
        }
        List<Image> sizes = new ArrayList<>();
        for (int size : new int[]{16, 24, 32, 48, 64, 128, 256}) {
            sizes.add(icon.getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }
        window.setIconImages(sizes);
    }

    /**
     * The full-color logo scaled to the given width, or an empty icon if the resource is missing.
     */
    public static ImageIcon logo(int width) {
        return scaled("/logo.png", width);
    }

    /**
     * The navy badge (logo without the wordmark), scaled to a square of the given size.
     */
    public static ImageIcon logoIcon(int size) {
        return scaled("/logo-icon.png", size);
    }

    /**
     * Green check used in success dialogs, drawn in code so it can never be missing.
     */
    public static Icon successIcon() {
        int size = 48;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(AVAILABLE);
        g.fillOval(0, 0, size, size);
        g.setColor(WHITE);
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawPolyline(new int[]{14, 21, 35}, new int[]{25, 32, 17}, 3);
        g.dispose();
        return new ImageIcon(img);
    }

    public static JButton button(String text, ButtonStyle style) {
        JButton button = new JButton(text);
        styleButton(button, style);
        return button;
    }

    public static void styleButton(JButton button, ButtonStyle style) {
        Color bg;
        Color fg;
        Color border;
        switch (style) {
            case SECONDARY -> { bg = WHITE; fg = NAVY; border = NAVY; }
            case ACCENT -> { bg = ROYAL; fg = WHITE; border = ROYAL; }
            case DANGER -> { bg = OVERDUE; fg = WHITE; border = OVERDUE; }
            case QUIET -> { bg = MIST; fg = NAVY; border = MIST; }
            default -> { bg = NAVY; fg = WHITE; border = NAVY; }
        }
        Color hover = style == ButtonStyle.SECONDARY || style == ButtonStyle.QUIET ? SKY_TINT : blend(bg, WHITE, 0.15f);

        button.setFont(strong(14));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(10, 22, 10, 22)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Replace any hover listener from an earlier call so restyling doesn't stack effects
        for (var l : button.getMouseListeners()) {
            if (l instanceof HoverListener) {
                button.removeMouseListener(l);
            }
        }
        button.addMouseListener(new HoverListener(button, bg, hover));
    }

    public static void styleField(JTextField field) {
        field.setFont(body(15));
        field.setForeground(INK);
        field.setBackground(WHITE);
        field.setOpaque(true);
        field.setCaretColor(NAVY);
        field.setBorder(fieldBorder(FIELD_LINE));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(fieldBorder(ROYAL));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(fieldBorder(FIELD_LINE));
            }
        });
    }

    /**
     * Small bold label placed above or beside a form field.
     */
    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        styleFieldLabel(label);
        return label;
    }

    public static void styleFieldLabel(JLabel label) {
        label.setFont(strong(13));
        label.setForeground(INK);
    }

    public static void styleCheckBox(AbstractButton box) {
        box.setFont(body(13));
        box.setForeground(SLATE);
        box.setOpaque(false);
        box.setFocusPainted(false);
    }

    /**
     * White page header with a serif title, an optional subtitle and a mist divider underneath.
     */
    public static JPanel pageHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, MIST),
                BorderFactory.createEmptyBorder(24, 32, 20, 32)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(heading(26));
        titleLabel.setForeground(NAVY);
        header.add(titleLabel);

        if (subtitle != null && !subtitle.isEmpty()) {
            header.add(Box.createVerticalStrut(6));
            JLabel sub = new JLabel(subtitle);
            sub.setFont(body(14));
            sub.setForeground(SLATE);
            header.add(sub);
        }
        return header;
    }

    /**
     * Horizontal bar of action buttons placed under a table.
     */
    public static JPanel actionBar(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        bar.setBackground(WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MIST),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        for (JButton b : buttons) {
            bar.add(b);
        }
        return bar;
    }

    /**
     * Dashboard card: a caption, a large serif value and a colored accent bar on the left.
     */
    public static JPanel statCard(String caption, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MIST, 1),
                        BorderFactory.createMatteBorder(0, 4, 0, 0, accent)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JLabel captionLabel = new JLabel(caption.toUpperCase());
        captionLabel.setFont(strong(12));
        captionLabel.setForeground(SLATE);
        valueLabel.setFont(heading(34));
        valueLabel.setForeground(NAVY);

        card.add(captionLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Styles a table: navy header, roomy striped rows, and colored text for book status values.
     */
    public static void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(34);
        table.setFont(body(14));
        table.setForeground(INK);
        table.setBackground(WHITE);
        table.setGridColor(MIST);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(SKY_TINT);
        table.setSelectionForeground(INK);
        table.setDefaultRenderer(Object.class, new BodyRenderer());
        table.setDefaultRenderer(Number.class, new BodyRenderer());

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setDefaultRenderer(new HeaderRenderer());
    }

    /**
     * Scroll pane for a table with a thin mist border instead of the default bevel.
     */
    public static JScrollPane tableScroll(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MIST, 1));
        scroll.getViewport().setBackground(WHITE);
        return scroll;
    }

    // ---- helpers ----

    private static Border fieldBorder(Color line) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(line, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    private static void put(String key, Color color) {
        UIManager.put(key, new ColorUIResource(color));
    }

    private static Color blend(Color a, Color b, float amount) {
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * amount),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * amount),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * amount));
    }

    private static String firstInstalled(String... families) {
        List<String> installed = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String family : families) {
            if (installed.contains(family)) {
                return family;
            }
        }
        return families[families.length - 1];
    }

    private static Image loadImage(String resource) {
        java.net.URL url = Theme.class.getResource(resource);
        return url == null ? null : new ImageIcon(url).getImage();
    }

    private static ImageIcon scaled(String resource, int width) {
        Image img = loadImage(resource);
        if (img == null) {
            return new ImageIcon();
        }
        int height = Math.round(width * img.getHeight(null) / (float) img.getWidth(null));
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private static final class HoverListener extends MouseAdapter {
        private final JButton button;
        private final Color normal;
        private final Color hover;

        HoverListener(JButton button, Color normal, Color hover) {
            this.button = button;
            this.normal = normal;
            this.hover = hover;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (button.isEnabled()) {
                button.setBackground(hover);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(normal);
        }
    }

    private static final class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setBackground(NAVY);
            setForeground(WHITE);
            setFont(strong(13));
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            return this;
        }
    }

    private static final class BodyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            setHorizontalAlignment(LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            setFont(body(14));
            if (!isSelected) {
                setBackground(row % 2 == 0 ? WHITE : PAPER);
            }

            String text = value == null ? "" : value.toString();
            Color statusColor = switch (text) {
                case "Available" -> AVAILABLE;
                case "Unavailable", "Borrowed" -> ROYAL;
                case "Overdue" -> OVERDUE;
                default -> null;
            };
            if (statusColor != null) {
                setText("●  " + text);
                setForeground(statusColor);
                setFont(strong(13));
            } else {
                setForeground(INK);
            }
            return this;
        }
    }
}
