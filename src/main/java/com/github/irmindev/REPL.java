package com.github.irmindev;
import java.io.*;
import java.util.List;

import com.github.irmindev.controller.Lexer;
import com.github.irmindev.controller.Parser;
import com.github.irmindev.model.Environment;
import com.github.irmindev.model.VisitorImplementationInterpreter;
import com.github.irmindev.model.statements.Statement;

public class REPL {
    private static Environment environment = new Environment();    
    private static VisitorImplementationInterpreter visitor = new VisitorImplementationInterpreter(environment, environment);

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
                e.printStackTrace();
            } finally {
                closeStream(reader);
            }
        } else if (args.length == 1) {
            try {
                execute(readFile(args[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void execute(String s) {
        Lexer lexer = new Lexer(s);
        Parser parser = new Parser(lexer.scanTokens());
        List<Statement> program = parser.parse();   
        for (Statement statement : program) {
            try {
                statement.accept(visitor);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static String readFile(String path) throws FileNotFoundException {
        InputStream input = null;
        String content = null;
    
        try {
            input = new FileInputStream(path); // Use FileInputStream for absolute path
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
