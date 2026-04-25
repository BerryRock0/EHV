package EtherHack.utils;

import zombie.debug.LineDrawer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class Rendering {
    public static void drawText(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        TextManager.instance.DrawString(font, (double)x, (double)y, text, (double)r, (double)g, (double)b, (double)a);
    }

    public static void drawTextCenterWithShadow(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        Rendering.drawTextCenterWithShadow(text, font, x, y, r, g, b, a, 1.0f);
    }

    public static void drawTextCenterWithShadow(String text, UIFont font, float x, float y, float r, float g, float b, float a, float thickness) {
        TextManager.instance.DrawStringCentre(font, (double)(x + thickness), (double)(y + thickness), text, 0.0, 0.0, 0.0, (double)a);
        TextManager.instance.DrawStringCentre(font, (double)x, (double)y, text, (double)r, (double)g, (double)b, (double)a);
    }

    public static void drawTextCenter(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        TextManager.instance.DrawStringCentre(font, (double)x, (double)y, text, (double)r, (double)g, (double)b, (double)a);
    }

    public static void drawLine(int startX, int startY, int endX, int endY, float r, float g, float b, float a, int thickness) {
        LineDrawer.drawLine((float)startX, (float)startY, (float)endX, (float)endY, (float)r, (float)g, (float)b, (float)a, (int)thickness);
    }

    public static void drawCircle(float posX, float posY, float radius, int segments, float r, float g, float b) {
        LineDrawer.drawCircle((float)posX, (float)posY, (float)radius, (int)segments, (float)r, (float)g, (float)b);
    }

    public static void drawArc(float posX, float posY, float thickness, float radius, float startAngle, float endAngle, int segments, float r, float g, float b, float a) {
        LineDrawer.drawArc((float)posX, (float)posY, (float)thickness, (float)radius, (float)startAngle, (float)endAngle, (int)segments, (float)r, (float)g, (float)b, (float)a);
    }

    public static void drawRect(float posX, float posY, float width, float height, float r, float g, float b, float a, int thicknessBorder) {
        LineDrawer.drawRect((float)posX, (float)posY, (float)width, (float)height, (float)r, (float)g, (float)b, (float)a, (int)thicknessBorder);
    }
}
