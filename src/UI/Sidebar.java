package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Navy navigation sidebar shared by the CEO and user pages:
 * logo and workspace name at the top, navigation items, and a logout button at the bottom.
 */
public class Sidebar extends JPanel {

    private final Map<String, JPanel> items = new LinkedHashMap<>();
    private String selected;

    /**
     * @param workspace caption under the brand name, e.g. "CEO workspace"
     * @param names     navigation items, in order; the first is selected initially
     * @param onSelect  called with the item name when an item is clicked
     * @param onLogout  called when the logout button is clicked
     */
    public Sidebar(String workspace, String[] names, Consumer<String> onSelect, Runnable onLogout) {
        super(new BorderLayout());
        setBackground(Theme.NAVY);
        setPreferredSize(new Dimension(230, 0));

        add(brandBlock(workspace), BorderLayout.NORTH);

        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        for (String name : names) {
            JPanel item = navItem(name, onSelect);
            items.put(name, item);
            nav.add(item);
            nav.add(Box.createVerticalStrut(4));
        }
        add(nav, BorderLayout.CENTER);

        JButton logout = Theme.button("Log out", Theme.ButtonStyle.ACCENT);
        logout.addActionListener(e -> onLogout.run());
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 16, 20, 16));
        bottom.add(logout, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        if (names.length > 0) {
            select(names[0]);
        }
    }

    /**
     * Highlights the given item without triggering its action.
     */
    public void select(String name) {
        selected = name;
        items.forEach((n, item) -> paintItem(item, n.equals(name) ? Theme.ROYAL : Theme.NAVY));
    }

    private JPanel brandBlock(String workspace) {
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.ROYAL),
                BorderFactory.createEmptyBorder(24, 24, 20, 24)));

        JLabel symbol = new JLabel(Theme.logoIcon(56));
        JLabel name = new JLabel("Vertex Library");
        name.setFont(Theme.heading(20));
        name.setForeground(Theme.WHITE);
        JLabel caption = new JLabel(workspace.toUpperCase());
        caption.setFont(Theme.strong(11));
        caption.setForeground(Theme.SKY);

        brand.add(symbol);
        brand.add(Box.createVerticalStrut(12));
        brand.add(name);
        brand.add(Box.createVerticalStrut(4));
        brand.add(caption);
        return brand;
    }

    private JPanel navItem(String name, Consumer<String> onSelect) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        item.setPreferredSize(new Dimension(200, 44));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(name);
        label.setFont(Theme.strong(14));
        label.setForeground(Theme.WHITE);
        item.add(label, BorderLayout.CENTER);
        paintItem(item, Theme.NAVY);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                select(name);
                onSelect.accept(name);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!name.equals(selected)) {
                    paintItem(item, new Color(0x13306A)); // navy lifted toward royal for hover
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                paintItem(item, name.equals(selected) ? Theme.ROYAL : Theme.NAVY);
            }
        });
        return item;
    }

    private static void paintItem(JPanel item, Color bg) {
        item.setBackground(bg);
        item.repaint();
    }
}
