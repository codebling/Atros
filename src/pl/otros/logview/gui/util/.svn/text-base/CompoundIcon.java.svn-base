package pl.otros.logview.gui.util;

import java.awt.Component;
import java.awt.Graphics;
import java.util.List;

import javax.swing.Icon;

public class CompoundIcon implements Icon {

  private List<Icon> icons;
  private int width;
  private int height;

  public CompoundIcon(List<Icon> icons) {
    super();
    this.icons = icons;
    for (Icon icon : icons) {
      if (icon == null) {
        continue;
      }
      width += icon.getIconWidth();
      height = Math.max(height, icon.getIconHeight());
    }
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    int xPos = x;
    for (Icon icon : icons) {
      icon.paintIcon(c, g, xPos, y);
      xPos += icon.getIconWidth();
    }
  }

  @Override
  public int getIconWidth() {
    return width;
  }

  @Override
  public int getIconHeight() {
    return height;
  }

}
