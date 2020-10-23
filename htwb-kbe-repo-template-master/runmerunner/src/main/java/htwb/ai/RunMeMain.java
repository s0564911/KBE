package htwb.ai;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RunMeMain {

    public static Object instanze;

    public static void main(String[] args) {
        String className = "";
        try {
            if (args.length == 0)
                throw new ClassNotFoundException();

            List<Method> runMeNot = new ArrayList<>();
            List<Method> runMe = new ArrayList<>();
            List<Method> notInvocable = new ArrayList<>();
            List<Method> needsArguments = new ArrayList<>();

            className = args[0];


            Class<?> clazz = Class.forName(className);
            Method[] declMethods = clazz.getDeclaredMethods();

            // entweder hier oder unten, hier wird bei nicht instanzierbaren Klassen direkt ein Error ausgeworfen
            if (instanze == null)
                instanze = clazz.getDeclaredConstructor().newInstance();

            System.out.println("Analyzed class '" + clazz.getCanonicalName() + "':");

            for (Method m : declMethods) {
                if (!m.isAnnotationPresent(RunMe.class)) {
                    runMeNot.add(m);
                }
            }

            for (Method m : declMethods) {
                if (m.isAnnotationPresent(RunMe.class)) {
                    runMe.add(m);
                }
            }

            if (runMeNot.size() > 0) {
                System.out.println("Methods without @RunMe: ");
                for (Method m : runMeNot)
                    System.out.println("  " + m.getName());
            } else {
                System.out.println("No methods without @RunMe");
            }

            if (runMe.size() > 0) {
                // hier wuerden bei Klassen wie java.lang.Number noch Methoden ohne @RunMe angezeigt
                //Object instanze = clazz.getDeclaredConstructor().newInstance();
                System.out.println("Methods with @RunMe: ");
                for (Method m : runMe) {
                    try {
                        m.invoke(instanze);
                        System.out.println("  " + m.getName());
                    } catch (InvocationTargetException | IllegalAccessException e) {
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
            System.err.println("Error: Could not find class " + className);
            System.err.println("Usage: java -jar runmerunner-KBE.jar className");
        } catch (InstantiationException e) {
            System.err.println("Error: Could not instantiate class " + className);
            System.err.println("Usage: java -jar runmerunner-KBE.jar className");
        } catch (NoSuchMethodException e) {
            System.err.println("Error: Could not find constructor of class " + className);
            System.err.println("Error: Could not instantiate class " + className);
            System.err.println("Usage: java -jar runmerunner-KBE.jar className");
        } catch (InvocationTargetException | IllegalAccessException e) {
            System.err.println("Error: Could not access constructor of class " + className);
            System.err.println("Error: Could not instantiate class " + className);
            System.err.println("Usage: java -jar runmerunner-KBE.jar className");
//        } catch (InvocationTargetException e) {
//            System.out.println("Error: Could not invoke constructor of class " + className);
//            System.out.println("Error: Could not instantiate class " + className);
//            System.out.println("Usage: java -jar runmerunner-KBE.jar className");
        }
    }

    protected static Object mockThis() {
        return instanze;
    }
}
