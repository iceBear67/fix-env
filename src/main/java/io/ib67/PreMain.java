package io.ib67;

import sun.misc.Unsafe;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PreMain {
    public static void main(String[] args) throws Throwable {
        System.getenv().forEach((k, v) -> System.out.println(k + "=" + v));
    }

    public static void premain(String agent, Instrumentation instrumentation) {
        String output;
        try (var is = new BufferedInputStream(Runtime.getRuntime().exec(new String[]{"/usr/bin/zsh", "-c", "source ~/.zshrc && env"})
                .getInputStream())) {
            output = new String(is.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var env = System.getenv();
        var missingEnv = Arrays.stream(output.split("\n"))
                .map(it -> it.split("="))
                .filter(it -> !env.containsKey(it[0]) && it.length == 2)
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        // getting the map
        var accessibleMap = getEnvMap(env);
        accessibleMap.putAll(missingEnv);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> getEnvMap(Map<String, String> env) {
        try {
            var unsafe = Unsafe.class.getDeclaredField("theUnsafe");
            unsafe.setAccessible(true);
            Unsafe usf = (Unsafe) unsafe.get(null);
            var fieldM = env.getClass().getDeclaredField("m");
            var offset = usf.objectFieldOffset(fieldM);
            return (Map<String, String>) usf.getObject(env, offset);
        } catch (Exception e) {
            throw new RuntimeException("Cannot make env accessible", e);
        }
    }
}