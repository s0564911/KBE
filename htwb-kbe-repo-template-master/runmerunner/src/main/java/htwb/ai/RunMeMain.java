package htwb.ai;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RunMeMain {
    public static void main(String[] args) {
        String className = "";
        try {
            if (args.length == 0)
                throw new ClassNotFoundException();

            List<Method> runMe = new ArrayList<>();
            List<Method> notInvocable = new ArrayList<>();
            List<Method> needsArguments = new ArrayList<>();

            className = args[0]; //"htwb.ai.TestClass"; //


            Class<?> clazz = Class.forName(className);
            Method[] declMethods = clazz.getDeclaredMethods();

            // ToDo: entweder hier oder unten, hier wird bei nicht instanzierbaren Klassen direkt ein Error ausgeworfen
            Object instanze = clazz.getDeclaredConstructor().newInstance();

            System.out.println("Analyzed class '" + clazz.getCanonicalName() + "':");

            System.out.println("Methods without @RunMe: ");
            for (Method m : declMethods) {
                if (!m.isAnnotationPresent(RunMe.class)) {
                    System.out.println("  " + m.getName());
                }
            }

            for (Method m : declMethods) {
                if (m.isAnnotationPresent(RunMe.class)) {
                    runMe.add(m);
                }
            }

            if (runMe.size() > 0) {

                // ToDo: oder hier werden bei Klassen wie java.lang.Number noch Methoden ohne @RunMe angezeigt
                //Object instanze = clazz.getDeclaredConstructor().newInstance();
                System.out.println("Methods with @RunMe: ");
                for (Method m : runMe) {
                    try {
                        m.invoke(instanze);
                        System.out.println("  " + m.getName());
                    } catch (IllegalAccessException e) {
                        notInvocable.add(m);
                    } catch (IllegalArgumentException e) {
                        needsArguments.add(m);
                    }
                }
            } else {
                System.out.println("No methods with @RunMe");
            }

            if (notInvocable.size() > 0) {
                System.out.println("not invocable:");
                for (Method m : notInvocable) {
                    System.out.println("  " + m.getName() + ": IllegalAccessException");
                }
            }

            if (needsArguments.size() > 0) {
                System.out.println("needing arguments to be run:");
                for (Method m : needsArguments) {
                    System.out.print("  " + m.getName());
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Error: Could not find class " + className);
            System.out.println("Usage: java -jar runmerunner-KBE.jar className");
        } catch (InstantiationException e) {
            System.out.println("Error: Could not instantiate class " + className);
            System.out.println("Usage: java -jar runmerunner-KBE.jar className");
        } catch (NoSuchMethodException e) {
            System.out.println("Error: Could not find constructor of class " + className);
            System.out.println("Error: Could not instantiate class " + className);
            System.out.println("Usage: java -jar runmerunner-KBE.jar className");
        } catch (IllegalAccessException e) {
            System.out.println("Error: Could not access constructor " + className);
            System.out.println("Error: Could not instantiate class " + className);
            System.out.println("Usage: java -jar runmerunner-KBE.jar className");
        } catch (InvocationTargetException e) {
            System.out.println("Error: Could not invoke constructor of class " + className);
            System.out.println("Error: Could not instantiate class " + className);
            System.out.println("Usage: java -jar runmerunner-KBE.jar className");
        }
    }
}
