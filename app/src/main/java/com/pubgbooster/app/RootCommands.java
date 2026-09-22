package com.pubgbooster.app;

import java.io.DataOutputStream;

public class RootCommands {

    private static void runAsRoot(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void performanceMode() {
        String cmd =
            // CPU max performance
            "for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo performance > $cpu; done\n" +
            // GPU max
            "echo 725000000 > /sys/class/kgsl/kgsl-3d0/max_gpuclk\n" +
            "echo performance > /sys/class/kgsl/kgsl-3d0/devfreq/governor\n" +
            // RAM flush
            "echo 3 > /proc/sys/vm/drop_caches\n" +
            // Freeze all third party apps
            "for app in $(cmd package list packages -3 | cut -d: -f2); do am force-stop $app 2>/dev/null; done\n" +
            // 90fps force for PUBG
            "cmd device_config put game_overlay com.tencent.ig frameRate 90\n" +
            // PUBG priority boost
            "sleep 2 && PUBG_PID=$(pidof com.tencent.ig) && [ ! -z \"$PUBG_PID\" ] && renice -20 $PUBG_PID && taskset -p ff $PUBG_PID\n";

        runAsRoot(cmd);
    }

    public static void normalMode() {
        String cmd =
            // CPU schedutil wapis
            "for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo schedutil > $cpu; done\n" +
            // GPU normal
            "echo 587000000 > /sys/class/kgsl/kgsl-3d0/max_gpuclk\n" +
            "echo msm-adreno-tz > /sys/class/kgsl/kgsl-3d0/devfreq/governor\n";

        runAsRoot(cmd);
    }

    public static void clearRam() {
        runAsRoot("echo 3 > /proc/sys/vm/drop_caches\n");
    }
}
