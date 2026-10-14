package ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Enterprise Design System for Quiz Management System.
 * Supports dynamic Light/Dark mode switching, modern typography, glass & card panels,
 * branded high-DPI window icons, and micro-interactions.
 */
public class UITheme {

    public static boolean isDarkMode = false;

    // Brand Colors
    public static final Color PRIMARY = new Color(37, 99, 235);         // Royal Blue 600
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);   // Blue 700
    public static final Color PRIMARY_DARK = new Color(30, 58, 138);    // Blue 900

    public static final Color SECONDARY = new Color(100, 116, 139);     // Slate 500
    public static final Color SECONDARY_HOVER = new Color(71, 85, 105);

    public static final Color SUCCESS = new Color(16, 185, 129);        // Emerald 500
    public static final Color SUCCESS_HOVER = new Color(5, 150, 105);
    public static final Color SUCCESS_LIGHT = new Color(236, 253, 245); // Emerald 50
    public static final Color SUCCESS_DARK = new Color(6, 95, 70);

    public static final Color DANGER = new Color(239, 68, 68);          // Rose 500
    public static final Color DANGER_HOVER = new Color(220, 38, 38);
    public static final Color DANGER_LIGHT = new Color(254, 242, 242);

    public static final Color WARNING = new Color(245, 158, 11);        // Amber 500
    public static final Color WARNING_LIGHT = new Color(254, 243, 199); // Amber 50
    public static final Color WARNING_DARK = new Color(146, 64, 14);

    public static final Color INFO = new Color(14, 165, 233);           // Sky 500
    public static final Color INFO_LIGHT = new Color(240, 249, 255);

    public static final Color PURPLE = new Color(139, 92, 246);         // Violet 500
    public static final Color PURPLE_LIGHT = new Color(245, 243, 255);

    // Adaptive Theme Surfaces & Backgrounds
    public static Color BG_APP = new Color(248, 250, 252);
    public static Color BG_CARD = Color.WHITE;
    public static Color BG_CARD_HOVER = new Color(241, 245, 249);
    public static Color BORDER = new Color(226, 232, 240);
    public static Color BORDER_STRONG = new Color(203, 213, 225);
    public static Color BORDER_FOCUS = new Color(147, 197, 253);
    public static Color PRIMARY_LIGHT = new Color(239, 246, 255);
    public static Color SECONDARY_LIGHT = new Color(241, 245, 249);

    // Adaptive Typography Colors
    public static Color TEXT_TITLE = new Color(15, 23, 42);
    public static Color TEXT_BODY = new Color(51, 65, 85);
    public static Color TEXT_MUTED = new Color(100, 116, 139);
    public static Color TEXT_LIGHT = new Color(148, 163, 184);

    // Modern Typography
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_TITLE_LARGE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_STAT_VALUE = new Font("Segoe UI", Font.BOLD, 26);

    /**
     * Initializes FlatLaf design tokens and font scaling.
     */
    public static void setupTheme() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("TableHeader.font", FONT_BOLD);

        applyColors(false);
        FlatLightLaf.setup();
    }

    /**
     * Toggles between Light and Dark mode across all active windows.
     */
    public static void toggleDarkMode(Window activeWindow) {
        setDarkMode(!isDarkMode, activeWindow);
    }

    public static void setDarkMode(boolean dark, Window activeWindow) {
        isDarkMode = dark;
        applyColors(dark);

        try {
            if (dark) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
                w.repaint();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void applyColors(boolean dark) {
        if (dark) {
            BG_APP = new Color(15, 23, 42);          // Slate 900
            BG_CARD = new Color(30, 41, 59);         // Slate 800
            BG_CARD_HOVER = new Color(51, 65, 85);   // Slate 700
            BORDER = new Color(51, 65, 85);          // Slate 700
            BORDER_STRONG = new Color(71, 85, 105);  // Slate 600
            BORDER_FOCUS = new Color(96, 165, 250);  // Blue 400
            PRIMARY_LIGHT = new Color(30, 58, 138);  // Blue 900
            SECONDARY_LIGHT = new Color(51, 65, 85); // Slate 700

            TEXT_TITLE = new Color(248, 250, 252);   // Slate 50
            TEXT_BODY = new Color(226, 232, 240);    // Slate 200
            TEXT_MUTED = new Color(148, 163, 184);   // Slate 400
            TEXT_LIGHT = new Color(100, 116, 139);   // Slate 500
        } else {
            BG_APP = new Color(248, 250, 252);       // Slate 50
            BG_CARD = Color.WHITE;
            BG_CARD_HOVER = new Color(241, 245, 249);// Slate 100
            BORDER = new Color(226, 232, 240);       // Slate 200
            BORDER_STRONG = new Color(203, 213, 225);// Slate 300
            BORDER_FOCUS = new Color(147, 197, 253); // Blue 300
            PRIMARY_LIGHT = new Color(239, 246, 255);// Blue 50
            SECONDARY_LIGHT = new Color(241, 245, 249);

            TEXT_TITLE = new Color(15, 23, 42);      // Slate 900
            TEXT_BODY = new Color(51, 65, 85);       // Slate 700
            TEXT_MUTED = new Color(100, 116, 139);   // Slate 500
            TEXT_LIGHT = new Color(148, 163, 184);   // Slate 400
        }
    }

    /**
     * Creates a reusable 🌙 / ☀️ Theme Toggle button.
     */
    public static JButton createThemeToggle(JFrame frame) {
        JButton btn = new JButton(isDarkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(isDarkMode ? new Color(51, 65, 85) : Color.WHITE);
        btn.setForeground(isDarkMode ? new Color(248, 250, 252) : TEXT_BODY);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(7, 14, 7, 14)
        ));
        btn.addActionListener(e -> {
            toggleDarkMode(frame);
            btn.setText(isDarkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
            btn.setBackground(isDarkMode ? new Color(51, 65, 85) : Color.WHITE);
            btn.setForeground(isDarkMode ? new Color(248, 250, 252) : TEXT_BODY);
        });
        return btn;
    }

    /**
     * Generates a sleek, high-DPI application brand icon.
     */
    public static Image createLogoIcon(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gradient rounded background
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(37, 99, 235),
            size, size, new Color(99, 102, 241)
        );
        g2.setPaint(gp);
        int arc = Math.max(4, size / 3);
        g2.fillRoundRect(1, 1, size - 2, size - 2, arc, arc);

        // Highlight stroke
        g2.setColor(new Color(255, 255, 255, 120));
        g2.drawRoundRect(1, 1, size - 3, size - 3, arc, arc);

        // Draw Academic 'Q' Monogram
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (size * 0.58)));
        FontMetrics fm = g2.getFontMetrics();
        String text = "Q";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent() - (int)(size * 0.04);
        g2.drawString(text, x, y);

        g2.dispose();
        return img;
    }

    /**
     * Applies brand icons to any JFrame.
     */
    public static void applyWindowIcon(JFrame frame) {
        List<Image> icons = new ArrayList<>();
        icons.add(createLogoIcon(16));
        icons.add(createLogoIcon(24));
        icons.add(createLogoIcon(32));
        icons.add(createLogoIcon(48));
        icons.add(createLogoIcon(64));
        icons.add(createLogoIcon(128));
        frame.setIconImages(icons);
    }

    /**
     * Creates a card panel that adapts seamlessly to light/dark mode.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            public Color getBackground() {
                return BG_CARD;
            }
        };
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 22, 18, 22)
        ));
        return panel;
    }

    /**
     * Creates a Gradient Banner Panel for headers.
     */
    public static JPanel createGradientHeader(String title, String subtitle, String badgeText) {
        JPanel panel = new JPanel(new BorderLayout(15, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint paint = new GradientPaint(
                    0, 0, new Color(30, 58, 138),
                    getWidth(), getHeight(), new Color(37, 99, 235)
                );
                g2.setPaint(paint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 22, 16, 22));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        if (badgeText != null && !badgeText.isEmpty()) {
            JLabel badge = new JLabel("  " + badgeText.toUpperCase() + "  ");
            badge.setFont(FONT_BADGE);
            badge.setForeground(Color.WHITE);
            badge.setOpaque(true);
            badge.setBackground(new Color(255, 255, 255, 60));
            badge.setBorder(new LineBorder(new Color(255, 255, 255, 100), 1, true));
            left.add(badge);
            left.add(Box.createVerticalStrut(6));
        }

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE_LARGE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SUBTITLE);
        lblSub.setForeground(new Color(224, 242, 254));

        left.add(lblTitle);
        left.add(Box.createVerticalStrut(2));
        left.add(lblSub);

        panel.add(left, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a pill chip badge (e.g. for role or status).
     */
    public static JLabel createChip(String text, Color bg, Color fg) {
        JLabel chip = new JLabel("  " + text + "  ");
        chip.setFont(FONT_BADGE);
        chip.setForeground(fg);
        chip.setBackground(bg);
        chip.setOpaque(true);
        chip.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 70), 1, true),
            new EmptyBorder(3, 8, 3, 8)
        ));
        return chip;
    }

    /**
     * Create a primary action button (Royal Blue with hover effect).
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(PRIMARY_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(PRIMARY);
            }
        });
        return btn;
    }

    /**
     * Create a secondary outline button.
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_BODY);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 16, 8, 16)
        ));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(SECONDARY_LIGHT);
                    btn.setForeground(TEXT_TITLE);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(BG_CARD);
                    btn.setForeground(TEXT_BODY);
                }
            }
        });
        return btn;
    }

    /**
     * Create a danger button (Red).
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(DANGER_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(DANGER);
            }
        });
        return btn;
    }

    /**
     * Create a success button (Emerald Green).
     */
    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(SUCCESS_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(SUCCESS);
            }
        });
        return btn;
    }

    /**
     * Create a modern KPI stat card with emoji circle and colored accent.
     */
    public static JPanel createStatCard(String title, String value, String subtitle, Color accentColor, String emoji) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override
            public Color getBackground() {
                return BG_CARD;
            }
        };
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)
            )
        ));

        // Emoji Circle Icon
        JLabel iconLbl = new JLabel(emoji, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLbl.setPreferredSize(new Dimension(46, 46));
        iconLbl.setOpaque(true);
        iconLbl.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
        iconLbl.setBorder(new LineBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80), 1, true));

        // Text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(FONT_BADGE);
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_STAT_VALUE);
        lblValue.setForeground(TEXT_TITLE);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(TEXT_MUTED);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblValue);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblSub);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Styles any JTable to modern, ultra-clean appearance.
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_BODY);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(PRIMARY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setForeground(TEXT_TITLE);
        header.setBackground(SECONDARY_LIGHT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 12, 0, 12));

        table.setDefaultRenderer(Object.class, leftRenderer);
    }

    /**
     * Create a score percentage pill badge.
     */
    public static JLabel createScoreBadge(double pct) {
        String text = Math.round(pct) + "%";
        if (pct >= 80.0) {
            return createChip(text + " • EXCELLENT", SUCCESS_LIGHT, SUCCESS_DARK);
        } else if (pct >= 50.0) {
            return createChip(text + " • PASSING", WARNING_LIGHT, WARNING_DARK);
        } else {
            return createChip(text + " • NEEDS STUDY", DANGER_LIGHT, DANGER);
        }
    }

    /**
     * Create a role pill badge.
     */
    public static JLabel createRoleBadge(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return createChip("ADMINISTRATOR", PURPLE_LIGHT, PURPLE);
        } else {
            return createChip("STUDENT", SUCCESS_LIGHT, SUCCESS);
        }
    }

    /**
     * Create a standard frame header banner.
     */
    public static JPanel createHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE_LARGE);
        lblTitle.setForeground(TEXT_TITLE);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SUBTITLE);
        lblSub.setForeground(TEXT_MUTED);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblSub, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Create a form field row with label and component.
     */
    public static JPanel createFormField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BOLD);
        label.setForeground(TEXT_TITLE);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Apply default styling to text inputs.
     */
    public static void styleInput(JTextField input) {
        input.setFont(FONT_BODY);
        input.setPreferredSize(new Dimension(input.getPreferredSize().width, 36));
        input.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
    }
}
