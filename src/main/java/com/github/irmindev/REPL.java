package com.github.irmindev;
import java.io.*;
import java.util.stream.Stream;

public class REPL {
    public static void main(String[] args) {
        if (args.length == 0) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String line;
                System.out.print(">> ");
                while ((line = reader.readLine()) != null) {
                    execute(line);
                    System.out.print(">> ");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                closeStream(reader);
            }
        } else if (args.length == 1) {
            try {
                execute(readFile(args[0]).lines());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void execute(String s) {
        System.out.println(s);
    }

    private static void execute(Stream<String> lines) {
        lines.forEach(System.out::println);
    }

    public static String readFile(String path) throws FileNotFoundException {
        InputStream input = null;
        String content = null;

        try {
            input = REPL.class.getClassLoader().getResourceAsStream(path);
            if (input == null) throw new FileNotFoundException("File " + path + " not found");

            byte[] buffer = input.readAllBytes();
            content = new String(buffer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeStream(input);
        }

        return content;
    }

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
