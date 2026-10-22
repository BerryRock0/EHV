package EtherHack.utils;

import zombie.core.Core;
import zombie.core.PerformanceSettings;

/**
 * Класс для управления настройками производительности игры.
 * Позволяет снять ограничение FPS, а также снизить нагрузку на рендеринг.
 */
public class PerformanceUtils
{
    /** Настройки игры на момент запуска клиента, используются для сброса параметров */
    private static boolean defaultUncappedFPS = false;
    private static int defaultLockFPS = 60;
    private static int defaultLightingQuality = 0;
    private static int defaultLightingFPS = 30;
    private static int defaultWaterQuality = 0;
    private static int defaultPuddlesQuality = 0;
    private static int defaultFogQuality = 0;
    private static boolean defaultReflections = true;
    private static boolean defaultAnimationInterpolation = true;

    /**
     * Запоминание текущих настроек игры, чтобы их можно было вернуть обратно.
     */
    public static void saveDefaultSettings()
    {
        defaultUncappedFPS = PerformanceSettings.isUncappedFPS();
        defaultLockFPS = PerformanceSettings.getLockFPS();
        defaultLightingQuality = PerformanceSettings.instance.getLightingQuality();
        defaultLightingFPS = PerformanceSettings.instance.getLightingFPS();
        defaultWaterQuality = PerformanceSettings.instance.getWaterQuality();
        defaultPuddlesQuality = PerformanceSettings.instance.getPuddlesQuality();
        defaultFogQuality = PerformanceSettings.instance.getFogQuality();
        defaultReflections = Core.getInstance().getPerfReflections();
        defaultAnimationInterpolation = PerformanceSettings.InterpolateAnims;
    }

    /**
     * Возвращение настроек игры к состоянию на момент запуска клиента.
     */
    public static void resetToDefaultSettings()
    {
        setUncappedFPS(defaultUncappedFPS);
        setLockFPS(defaultLockFPS);
        setLightingQuality(defaultLightingQuality);
        setLightingFPS(defaultLightingFPS);
        setWaterQuality(defaultWaterQuality);
        setPuddlesQuality(defaultPuddlesQuality);
        setFogQuality(defaultFogQuality);
        setReflectionsEnabled(defaultReflections);
        setAnimationInterpolation(defaultAnimationInterpolation);
    }

    /**
     * Снятие ограничения частоты кадров.
     */
    public static void setUncappedFPS(boolean isEnabled)
    {
        PerformanceSettings.setUncappedFPS(isEnabled);
    }

    public static boolean isUncappedFPS()
    {
        return PerformanceSettings.isUncappedFPS();
    }

    /**
     * Ограничение частоты кадров.
     */
    public static void setLockFPS(int fps)
    {
        PerformanceSettings.setLockFPS(Math.max(10, fps));
    }

    public static int getLockFPS()
    {
        return PerformanceSettings.getLockFPS();
    }

    /**
     * Качество освещения. Чем больше значение, тем реже пересчитывается освещение.
     * Значение 3 полностью отключает затенение мира.
     */
    public static void setLightingQuality(int quality)
    {
        PerformanceSettings.instance.setLightingQuality(Math.max(0, Math.min(3, quality)));
    }

    public static int getLightingQuality()
    {
        return PerformanceSettings.instance.getLightingQuality();
    }

    /**
     * Частота пересчёта освещения (в кадрах в секунду).
     */
    public static void setLightingFPS(int fps)
    {
        PerformanceSettings.instance.setLightingFPS(Math.max(1, Math.min(120, fps)));
    }

    public static int getLightingFPS()
    {
        return PerformanceSettings.instance.getLightingFPS();
    }

    /**
     * Качество воды. 0 - без воды, 1 - среднее, 2 - высокое.
     */
    public static void setWaterQuality(int quality)
    {
        PerformanceSettings.instance.setWaterQuality(Math.max(0, Math.min(2, quality)));
    }

    public static int getWaterQuality()
    {
        return PerformanceSettings.instance.getWaterQuality();
    }

    /**
     * Качество луж. 0 - без луж, 1 - только земля, 2 - земля с колеями.
     */
    public static void setPuddlesQuality(int quality)
    {
        PerformanceSettings.instance.setPuddlesQuality(Math.max(0, Math.min(2, quality)));
    }

    public static int getPuddlesQuality()
    {
        return PerformanceSettings.instance.getPuddlesQuality();
    }

    /**
     * Качество тумана. 0 - без тумана, 1 - среднее, 2 - высокое.
     */
    public static void setFogQuality(int quality)
    {
        PerformanceSettings.instance.setFogQuality(Math.max(0, Math.min(2, quality)));
    }

    public static int getFogQuality()
    {
        return PerformanceSettings.instance.getFogQuality();
    }

    /**
     * Отражения. Отключение отражений снижает нагрузку на рендеринг.
     */
    public static void setReflectionsEnabled(boolean isEnabled)
    {
        Core.getInstance().setPerfReflections(isEnabled);
    }

    public static boolean isReflectionsEnabled()
    {
        return Core.getInstance().getPerfReflections();
    }

    /**
     * Небесное небо. Статичное небо не перерисовывается каждый кадр.
     */
    public static void setStaticSkybox(boolean isEnabled)
    {
        Core.getInstance().setPerfSkybox(isEnabled ? Core.iPerfSkybox_Static : Core.iPerfSkybox_High);
    }

    public static boolean isStaticSkybox()
    {
        return Core.getInstance().getPerfSkybox() == Core.iPerfSkybox_Static;
    }

    /**
     * Интерполяция анимаций. Её отключение немного снижает нагрузку на процессор.
     */
    public static void setAnimationInterpolation(boolean isEnabled)
    {
        PerformanceSettings.InterpolateAnims = isEnabled;
    }

    public static boolean isAnimationInterpolation()
    {
        return PerformanceSettings.InterpolateAnims;
    }
}
